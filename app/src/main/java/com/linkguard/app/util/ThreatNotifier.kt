package com.linkguard.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.linkguard.app.R
import com.linkguard.app.model.ScanVerdict
import com.linkguard.app.model.VerdictLevel
import com.linkguard.app.ui.MainActivity

/**
 * Posts a system notification alerting the user to a detected threat.
 *
 * Used as a fallback when the overlay (SYSTEM_ALERT_WINDOW) permission has not been
 * granted — without overlay, the in-app warning card cannot be shown, so we fall
 * back to a standard Android notification so the user is still alerted.
 *
 * Channel: "linkguard_threats" — HIGH importance so it heads-up on screen.
 * Only called for SUSPICIOUS and DANGEROUS verdicts; SAFE verdicts are silent.
 */
object ThreatNotifier {

    private const val TAG = "ThreatNotifier"
    private const val CHANNEL_ID = "linkguard_threats"

    // Notification IDs are derived from the URL hashCode so that repeated scans
    // of the same URL update the existing notification instead of stacking new ones.
    fun notify(context: Context, verdict: ScanVerdict) {
        if (verdict.level == VerdictLevel.SAFE || verdict.level == VerdictLevel.SCANNING) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(nm, context)

        val title = when (verdict.level) {
            VerdictLevel.DANGEROUS -> context.getString(R.string.overlay_verdict_dangerous)
            else -> context.getString(R.string.overlay_verdict_suspicious)
        }

        // Show the URL (truncated) as the notification body
        val displayUrl = verdict.url.take(80).let { if (verdict.url.length > 80) "$it…" else it }

        val openApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_check)
            .setContentTitle(title)
            .setContentText(displayUrl)
            .setStyle(NotificationCompat.BigTextStyle().bigText(displayUrl))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notifId = verdict.url.hashCode()
        try {
            nm.notify(notifId, notification)
            Log.d(TAG, "Threat notification posted (${verdict.level}): ${verdict.url}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to post threat notification: ${e.message}")
        }
    }

    private fun ensureChannel(nm: NotificationManager, context: Context) {
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.threat_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.threat_channel_desc)
            enableVibration(true)
        }
        nm.createNotificationChannel(channel)
    }
}
