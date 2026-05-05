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
import com.linkguard.app.scanner.ScanDeduplicator
import com.linkguard.app.scanner.ScanOrchestrator
import com.linkguard.app.util.UrlExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Scans for URLs when the user navigates INTO a chat/conversation within a monitored app.
 *
 * How it works:
 *  1. Detects window-state changes (TYPE_WINDOW_STATE_CHANGED)
 *  2. When a monitored app first comes to the foreground, records the initial screen class
 *     but does NOT scan — avoids scanning the chat list / inbox view.
 *  3. When the user navigates to a different screen WITHIN the same app (e.g., opens a
 *     specific chat), the class name changes → scan is triggered.
 *  4. Waits 800ms for the UI to fully render, then walks the accessibility tree.
 *  5. Extracts URLs from visible text and scans each one.
 *  6. Shows overlay only for SUSPICIOUS / DANGEROUS results (SAFE logged silently).
 *
 * Requires: Accessibility permission (Settings → Accessibility → LinkGuard)
 */
class LinkGuardAccessibilityService : AccessibilityService() {

    private val TAG = "LinkGuardA11y"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var overlayManager: OverlayManager

    // The package currently in the foreground
    private var currentForegroundPackage: String? = null

    // Last Activity/Fragment class name seen per package
    // Used to detect navigation between screens within the same app
    private val lastSeenClassByPackage = mutableMapOf<String, String>()

    // Per-package scan cooldown — suppresses rapid re-scans of the same screen
    // (e.g., dialogs or fragment animations that fire extra state-change events)
    private val lastScanByPackage = mutableMapOf<String, Long>()
    private val SAME_SCREEN_COOLDOWN_MS = 15_000L

    override fun onServiceConnected() {
        overlayManager = OverlayManager(applicationContext)

        // Confirm we want window-state events and screen content access
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }

        Log.d(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == this.packageName) return
        if (packageName == "android") return // System UI / launcher transitions

        val className = event.className?.toString() ?: return

        // Check monitored apps list
        val monitored = MonitoredAppsPrefs.getMonitoredPackages(applicationContext)
        if (monitored.isNotEmpty() && packageName !in monitored) return

        val previousPackage = currentForegroundPackage
        currentForegroundPackage = packageName

        if (packageName != previousPackage) {
            // App just came to the foreground — record the opening screen class but
            // do NOT scan. This prevents scanning the chat list / inbox overview.
            Log.d(TAG, "App foregrounded: $packageName [$className] — skipping initial screen")
            lastSeenClassByPackage[packageName] = className
            return
        }

        // Same app — check whether the screen (Activity/Fragment class) changed
        val lastClass = lastSeenClassByPackage[packageName]
        val screenChanged = className != lastClass

        if (!screenChanged) {
            // Same screen: apply cooldown to suppress fragment/dialog noise
            val now = System.currentTimeMillis()
            val lastScan = lastScanByPackage[packageName] ?: 0L
            if (now - lastScan < SAME_SCREEN_COOLDOWN_MS) {
                Log.d(TAG, "Same screen cooldown active for $packageName — skipping")
                return
            }
        }

        // User navigated to a new screen within the app (e.g., opened a specific chat)
        lastSeenClassByPackage[packageName] = className
        lastScanByPackage[packageName] = System.currentTimeMillis()

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

        urls.forEach { url ->
            if (ScanDeduplicator.shouldScan(url)) {
                handleUrl(url, packageName)
            } else {
                Log.d(TAG, "Skipping (already scanned): $url")
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
            collectText(child, sb, depth + 1)
            child.recycle()
        }
    }

    private suspend fun handleUrl(url: String, sourcePackage: String) {
        Log.d(TAG, "Scanning (accessibility): $url from $sourcePackage")

        if (!Settings.canDrawOverlays(applicationContext)) {
            Log.w(TAG, "Overlay permission not granted — scanning silently")
            val verdict = ScanOrchestrator.scan(url, applicationContext)
            ScanHistoryPrefs.addEntry(applicationContext, verdict, sourcePackage)
            return
        }

        // Show scanning overlay immediately
        overlayManager.showScanning(url)

        val verdict = ScanOrchestrator.scan(url, applicationContext)
        ScanHistoryPrefs.addEntry(applicationContext, verdict, sourcePackage)

        // For accessibility scans: only interrupt the user for threats
        // SAFE links are logged silently (auto-dismiss overlay)
        when (verdict.level) {
            VerdictLevel.SAFE -> {
                Log.d(TAG, "Safe (silent): $url")
                overlayManager.dismiss()
            }
            else -> overlayManager.updateVerdict(verdict)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.dismiss()
        serviceScope.cancel()
        Log.d(TAG, "Accessibility service stopped")
    }
}
