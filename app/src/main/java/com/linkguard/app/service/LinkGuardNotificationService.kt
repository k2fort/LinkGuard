package com.linkguard.app.service

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.linkguard.app.data.prefs.MonitoredAppsPrefs
import com.linkguard.app.data.prefs.ScanHistoryPrefs
import com.linkguard.app.overlay.OverlayManager
import com.linkguard.app.scanner.ScanDeduplicator
import com.linkguard.app.scanner.ScanOrchestrator
import com.linkguard.app.util.ThreatNotifier
import com.linkguard.app.util.UrlExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LinkGuardNotificationService : NotificationListenerService() {

    private val TAG = "LinkGuardService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var overlayManager: OverlayManager

    // Deduplication handled by ScanDeduplicator.
    // Note: the accessibility service uses its OWN independent dedup cache
    // (recentlyScannedByA11y) — it does NOT share this one. Sharing caused
    // accessibility scans to be silently blocked whenever the notification
    // service had already scanned the same URL within the dedup window.

    override fun onCreate() {
        super.onCreate()
        overlayManager = OverlayManager(applicationContext)
        Log.d(TAG, "Service started")
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.dismiss()
        serviceScope.cancel()
        Log.d(TAG, "Service stopped")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        if (packageName == this.packageName) return

        Log.d(TAG, "Notification received from: $packageName")

        val monitoredApps = MonitoredAppsPrefs.getMonitoredPackages(applicationContext)
        if (monitoredApps.isNotEmpty() && packageName !in monitoredApps) {
            Log.d(TAG, "Skipping $packageName — not in monitored list")
            return
        }

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        // Some OEM notification bundles are malformed and throw when accessed.
        // Wrap the entire extraction in a try/catch so one bad notification
        // doesn't crash the service.
        val textParts = try {
            buildList {
                extras.getCharSequence(android.app.Notification.EXTRA_TEXT)
                    ?.let { add(it.toString()) }
                extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT)
                    ?.let { add(it.toString()) }
                extras.getCharSequence(android.app.Notification.EXTRA_TITLE)
                    ?.let { add(it.toString()) }
                extras.getCharSequenceArray(android.app.Notification.EXTRA_TEXT_LINES)
                    ?.forEach { add(it.toString()) }

                // MessagingStyle messages — use typed API on API 33+ to avoid silent null return
                getMessageBundles(extras, android.app.Notification.EXTRA_MESSAGES)
                    .forEach { msg -> msg.getCharSequence("text")?.let { add(it.toString()) } }

                // Historic/older bundled messages
                getMessageBundles(extras, android.app.Notification.EXTRA_HISTORIC_MESSAGES)
                    .forEach { msg -> msg.getCharSequence("text")?.let { add(it.toString()) } }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read extras from $packageName: ${e.message}")
            emptyList()
        }

        val fullText = textParts.joinToString(" ")
        Log.d(TAG, "Notification text from $packageName: \"$fullText\"")

        if (fullText.isBlank()) return

        val urls = UrlExtractor.extractUrls(fullText)
        if (urls.isEmpty()) {
            Log.d(TAG, "No URLs found in notification from $packageName")
            return
        }

        Log.d(TAG, "Found ${urls.size} URL(s): $urls")

        val freshUrls = urls.filter { url ->
            val ok = ScanDeduplicator.shouldScan(url)
            if (!ok) Log.d(TAG, "Skipping duplicate URL: $url")
            ok
        }
        if (freshUrls.isEmpty()) return

        // Each URL scanned in its own coroutine so they run in parallel.
        // Previously forEach ran serially — a notification with 3 URLs could
        // take up to 75 seconds (3 × 25s VT polling) before the last verdict appeared.
        freshUrls.forEach { url ->
            serviceScope.launch { handleDetectedUrl(url, packageName) }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    /**
     * Reads a Parcelable array of Bundles from notification extras.
     *
     * On API 33+, Bundle.getParcelableArray(String) without a class argument is deprecated
     * and returns null when the target app was compiled with the new API.  The typed overload
     * must be used instead.
     */
    private fun getMessageBundles(extras: Bundle, key: String): List<Bundle> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelableArray(key, Bundle::class.java)?.toList() ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelableArray(key)?.mapNotNull { it as? Bundle } ?: emptyList()
        }
    }

    private suspend fun handleDetectedUrl(url: String, sourcePackage: String) {
        Log.d(TAG, "Scanning URL from $sourcePackage: $url")

        val canShowOverlay = Settings.canDrawOverlays(applicationContext)

        // Show the scanning overlay immediately (if permission allows).
        // Either way, we always scan and always log — overlay permission should not
        // gate the scan itself.
        if (canShowOverlay) {
            overlayManager.showScanning(url)
        }

        val verdict = ScanOrchestrator.scan(url, applicationContext)
        Log.d(TAG, "Verdict: ${verdict.level} for $url")

        ScanHistoryPrefs.addEntry(applicationContext, verdict, sourcePackage)

        if (canShowOverlay) {
            overlayManager.updateVerdict(verdict)
        } else {
            Log.w(TAG, "Overlay permission not granted — falling back to notification: ${verdict.level}")
            ThreatNotifier.notify(applicationContext, verdict)
        }
    }
}
