package com.linkguard.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.linkguard.app.data.prefs.MonitoredAppsPrefs
import com.linkguard.app.data.prefs.ScanHistoryPrefs
import com.linkguard.app.model.VerdictLevel
import com.linkguard.app.overlay.OverlayManager
import com.linkguard.app.scanner.ScanOrchestrator
import com.linkguard.app.util.ThreatNotifier
import com.linkguard.app.util.UrlExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Scans for URLs when the user navigates INTO a chat/conversation within a monitored app.
 *
 * How it works:
 *  1. Detects window-state changes (TYPE_WINDOW_STATE_CHANGED)
 *  2. When a monitored app first comes to the foreground, a 1.5s grace period begins.
 *     Events during this window (e.g., splash → home screen) are ignored — this prevents
 *     scanning the chat list / inbox when the app is first opened.
 *  3. When the user navigates to a different screen WITHIN the same app AFTER the grace
 *     period (e.g., opens a specific chat), the class name changes → scan is triggered.
 *  4. Waits 800ms for the UI to finish rendering, then walks the accessibility tree.
 *  5. Extracts URLs from visible text and scans each one silently in the background.
 *  6. Shows overlay ONLY if the verdict is SUSPICIOUS or DANGEROUS. SAFE = silent log.
 *
 * Requires: Accessibility permission (Settings → Accessibility → LinkGuard)
 */
class LinkGuardAccessibilityService : AccessibilityService() {

    private val TAG = "LinkGuardA11y"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var overlayManager: OverlayManager

    // Cached set of monitored packages — updated via SharedPreferences listener.
    // Reading MonitoredAppsPrefs on every accessibility event (main thread, synchronous I/O)
    // caused jank under heavy event load. Cache it here instead.
    @Volatile private var monitoredPackages: Set<String> = emptySet()
    private lateinit var monitoredAppsListener: android.content.SharedPreferences.OnSharedPreferenceChangeListener

    // The monitored-app package currently in the foreground
    private var currentForegroundPackage: String? = null

    // Timestamp (ms) when each package became foreground — used for the launch grace period
    private val foregroundTimestampByPackage = mutableMapOf<String, Long>()

    // Last Activity/Fragment class name seen per package
    // Used to detect navigation between screens within the same app
    private val lastSeenClassByPackage = mutableMapOf<String, String>()

    // Per-package scan cooldown — suppresses rapid re-scans of the SAME screen
    // (e.g., dialogs or fragment animations that fire extra state-change events)
    private val lastScanByPackage = mutableMapOf<String, Long>()

    // Periodic scan job — used for apps (e.g. Google Messages with Jetpack NavComponent)
    // that don't fire TYPE_WINDOW_STATE_CHANGED when navigating between chats.
    // Runs every 3s for 30s after the grace period whenever a monitored app foregrounds,
    // ensuring chat content is scanned regardless of navigation event gaps.
    // URL-level dedup (recentlyScannedByA11y) prevents duplicate API calls.
    private var periodicScanJob: Job? = null

    // Per-URL dedup cache local to THIS service.
    // NOT shared with the notification service's ScanDeduplicator — that shared cache
    // was blocking accessibility scans whenever the notification service had already
    // scanned the same URL within the last 60s (which is almost always).
    // Using a local cache means the accessibility service scans what the user sees in the
    // chat, regardless of whether a notification scan happened earlier.
    // ConcurrentHashMap — scanVisibleContent runs on Dispatchers.Default (thread pool),
    // so reads and writes to this map can race if it were a plain mutableMapOf.
    private val recentlyScannedByA11y = java.util.concurrent.ConcurrentHashMap<String, Long>()

    companion object {
        // Skip events for 1.5s after a monitored app first foregrounds.
        // Prevents splash → home from being treated as a "screen navigation".
        private const val FOREGROUND_GRACE_MS = 1_500L
        // Additional blocking window for same-class events after grace expires.
        // The inbox/conversation-list fires 1-3 same-class events right after it
        // finishes rendering — these almost always land within 600ms of grace expiry.
        // This 700ms buffer blocks those "inbox settled" events without preventing
        // the user from opening a chat immediately after the app loads.
        private const val SAME_CLASS_POST_GRACE_DELAY_MS = 700L
        // Cooldown when the same Activity/screen fires repeated events.
        // 3s is long enough to suppress rapid dialog/animation noise on a single chat,
        // but short enough that switching to a different chat triggers a fresh scan.
        private const val SAME_SCREEN_COOLDOWN_MS = 3_000L
        // How long to remember a URL scanned by the accessibility service.
        // 3 minutes: prevents re-scanning the same chat on every dialog/animation event,
        // but allows fresh results if the user comes back to the chat later.
        private const val A11Y_URL_DEDUP_MS = 3 * 60 * 1000L
        // Periodic scan fallback: how often to poll when in a monitored app foreground.
        // Catches navigation events that don't fire TYPE_WINDOW_STATE_CHANGED.
        private const val PERIODIC_SCAN_INTERVAL_MS = 3_000L
        // How many periodic scans to run after the grace period (3s × 10 = 30s total).
        private const val PERIODIC_SCAN_COUNT = 10
    }

    override fun onServiceConnected() {
        overlayManager = OverlayManager(applicationContext)
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }

        // Prime the monitored-packages cache and keep it in sync.
        monitoredPackages = MonitoredAppsPrefs.getMonitoredPackages(applicationContext)
        monitoredAppsListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            monitoredPackages = MonitoredAppsPrefs.getMonitoredPackages(applicationContext)
        }
        MonitoredAppsPrefs.registerListener(applicationContext, monitoredAppsListener)

        Log.d(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == this.packageName) return
        if (packageName == "android") return // System UI / launcher transitions

        // Some apps (e.g. Google Messages with Jetpack NavComponent) fire
        // TYPE_WINDOW_STATE_CHANGED with a null className for fragment navigation.
        // Don't discard these — fall back to "" so the state machine can still
        // detect transitions (null→"FragmentClass" or "FragmentClass"→null both
        // produce screenChanged=true and trigger a scan).
        val className = event.className?.toString() ?: ""

        if (monitoredPackages.isNotEmpty() && packageName !in monitoredPackages) return

        val now = System.currentTimeMillis()
        val previousPackage = currentForegroundPackage
        currentForegroundPackage = packageName

        if (packageName != previousPackage) {
            // App just came to the foreground — start a grace period and record the class.
            // We do NOT scan here: this is the chat list / inbox view, not a conversation.
            // Reset lastScanByPackage so the "inbox guard" (same-class, no prior scan)
            // kicks in fresh for this foreground session.
            Log.d(TAG, "App foregrounded: $packageName [$className] — grace period started")
            foregroundTimestampByPackage[packageName] = now
            lastSeenClassByPackage[packageName] = className
            lastScanByPackage.remove(packageName)

            // Start a periodic scan fallback for apps that don't fire
            // TYPE_WINDOW_STATE_CHANGED when navigating between chats (e.g. Google
            // Messages with Jetpack NavComponent). Waits out the grace+settle window,
            // then probes the accessibility tree every 3s for 30s. URL-level dedup
            // ensures we don't re-call the API for URLs already scanned this session.
            periodicScanJob?.cancel()
            val fgPackage = packageName
            periodicScanJob = serviceScope.launch {
                delay(FOREGROUND_GRACE_MS + SAME_CLASS_POST_GRACE_DELAY_MS)
                repeat(PERIODIC_SCAN_COUNT) {
                    if (!isActive || currentForegroundPackage != fgPackage) return@launch
                    Log.d(TAG, "Periodic scan tick for $fgPackage")
                    scanVisibleContent(fgPackage)
                    delay(PERIODIC_SCAN_INTERVAL_MS)
                }
            }

            return
        }

        // ── Same app, check if still in the launch grace period ──────────────────
        // Multi-event launches (e.g., SplashActivity → HomeActivity) arrive within ~1s.
        // We keep updating the recorded class so the first post-grace event can still
        // detect a genuine navigation away from the home screen.
        val foregroundedAt = foregroundTimestampByPackage[packageName] ?: 0L
        if (now - foregroundedAt < FOREGROUND_GRACE_MS) {
            Log.d(TAG, "Grace period active for $packageName [$className] — skipping")
            lastSeenClassByPackage[packageName] = className
            return
        }

        // ── Check whether the screen (Activity/Fragment) changed ─────────────────
        val lastClass = lastSeenClassByPackage[packageName]
        val screenChanged = className != lastClass

        if (!screenChanged) {
            // Block scans for (grace + 0.7s) after foregrounding — lets the inbox settle.
            val foregroundedAt = foregroundTimestampByPackage[packageName] ?: 0L
            if (now < foregroundedAt + FOREGROUND_GRACE_MS + SAME_CLASS_POST_GRACE_DELAY_MS) {
                Log.d(TAG, "Post-foreground same-class block active for $packageName — skipping")
                return
            }

            // Same-screen cooldown: for KNOWN class names only.
            //
            // When className is "" (null/unknown — e.g. Google Messages NavComponent), we
            // cannot distinguish "same chat fired another event" from "user navigated to a
            // different chat". Applying the cooldown here blocks the second chat every time.
            // Instead, skip the screen-level cooldown and rely entirely on the per-URL
            // dedup cache (recentlyScannedByA11y, 3-minute window) which prevents
            // duplicate API calls even when scanVisibleContent runs more frequently.
            if (className.isNotEmpty()) {
                val lastScan = lastScanByPackage[packageName] ?: 0L
                if (now - lastScan < SAME_SCREEN_COOLDOWN_MS) {
                    Log.d(TAG, "Same-screen cooldown active for $packageName — skipping")
                    return
                }
            }
        }

        // User navigated to a new screen within the app (e.g., opened a specific chat)
        // OR the same screen reloaded after cooldown expired.
        lastSeenClassByPackage[packageName] = className
        lastScanByPackage[packageName] = now

        Log.d(TAG, "Screen navigated in $packageName → $className — scheduling scan")

        serviceScope.launch {
            delay(800) // Let the UI finish rendering
            scanVisibleContent(packageName)
        }
    }

    private suspend fun scanVisibleContent(packageName: String) {
        // rootInActiveWindow can throw SecurityException on MIUI / One UI if the
        // window changes between the delay and this read. Treat it as "nothing to scan".
        val root = try {
            rootInActiveWindow
        } catch (e: Exception) {
            Log.w(TAG, "Could not read window root for $packageName: ${e.message}")
            return
        }

        if (root == null) {
            Log.d(TAG, "No accessible window root for $packageName")
            return
        }

        val text = buildString {
            try {
                collectText(root, this, depth = 0)
            } catch (e: Exception) {
                Log.w(TAG, "Error walking accessibility tree: ${e.message}")
            }
        }

        @Suppress("DEPRECATION") // recycle() deprecated on API 34+ but safe on older versions
        try { root.recycle() } catch (_: Exception) { /* already recycled */ }

        if (text.isBlank()) {
            Log.d(TAG, "No text found in $packageName")
            return
        }

        val urls = UrlExtractor.extractUrls(text)
        if (urls.isEmpty()) {
            Log.d(TAG, "No URLs in visible content of $packageName")
            return
        }

        Log.d(TAG, "Found ${urls.size} URL(s) in $packageName: $urls")

        val now = System.currentTimeMillis()
        // Purge expired entries to keep the map from growing indefinitely
        recentlyScannedByA11y.entries.removeIf { now - it.value > A11Y_URL_DEDUP_MS }

        urls.forEach { url ->
            val lastSeen = recentlyScannedByA11y[url] ?: 0L
            if (now - lastSeen < A11Y_URL_DEDUP_MS) {
                Log.d(TAG, "Skipping (scanned by accessibility within 3min): $url")
            } else {
                recentlyScannedByA11y[url] = now
                handleUrl(url, packageName)
            }
        }
    }

    /**
     * Recursively collects visible text from the accessibility tree.
     * Limits: max depth 15, max 8000 characters total.
     */
    @Suppress("DEPRECATION") // recycle() deprecated on API 34+ but safe to call on older versions
    private fun collectText(node: AccessibilityNodeInfo?, sb: StringBuilder, depth: Int) {
        if (node == null || depth > 15 || sb.length > 8000) return

        node.text?.toString()?.takeIf { it.isNotBlank() }?.let {
            sb.append(it).append(" ")
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            // Use finally so child is always recycled even if collectText throws.
            // Without this, leaked nodes accumulate across every scan event and can
            // cause the accessibility service to be force-stopped on older API levels.
            try {
                collectText(child, sb, depth + 1)
            } finally {
                @Suppress("DEPRECATION") child.recycle()
            }
        }
    }

    private suspend fun handleUrl(url: String, sourcePackage: String) {
        Log.d(TAG, "Scanning silently (accessibility): $url from $sourcePackage")

        // Scan entirely in the background — no scanning spinner shown.
        // For accessibility scans we only interrupt the user if a threat is found.
        // Showing a "Scanning…" card that immediately disappears (when safe) is
        // confusing and was causing the "cards disappear" symptom.
        //
        // scanQuick = VT cache lookup only (< 1s), no 25-second polling loop.
        // This lets multiple chat URLs be scanned in parallel quickly without
        // exhausting the VT rate limit (4 req/min free tier).
        val verdict = ScanOrchestrator.scanQuick(url, applicationContext)
        ScanHistoryPrefs.addEntry(applicationContext, verdict, sourcePackage)

        when (verdict.level) {
            VerdictLevel.SAFE -> {
                Log.d(TAG, "Safe — logged silently: $url")
                // No overlay shown; entry saved to scan log.
            }
            else -> {
                if (!Settings.canDrawOverlays(applicationContext)) {
                    Log.w(TAG, "Threat detected but overlay permission not granted — falling back to notification: $url")
                    ThreatNotifier.notify(applicationContext, verdict)
                    return
                }
                // We already have the verdict — show it directly without the scanning
                // intermediate state. Both calls post to the main thread in order.
                overlayManager.showScanning(url)
                overlayManager.updateVerdict(verdict)
                Log.d(TAG, "Threat overlay shown (${verdict.level}): $url")
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        MonitoredAppsPrefs.unregisterListener(applicationContext, monitoredAppsListener)
        overlayManager.dismiss()
        periodicScanJob?.cancel()
        serviceScope.cancel()
        Log.d(TAG, "Accessibility service stopped")
    }
}
