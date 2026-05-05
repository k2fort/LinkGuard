package com.linkguard.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.linkguard.app.data.prefs.BlocklistUpdatePrefs
import com.linkguard.app.service.KeepAliveService
import com.linkguard.app.worker.BlocklistUpdateWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d(TAG, "Device booted — starting KeepAliveService")
        val svc = Intent(context, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(svc)
        } else {
            context.startService(svc)
        }

        // If the blocklist has never been updated, or the last update was more than
        // 24 hours ago, schedule an immediate one-time refresh.
        // (WorkManager's periodic job is re-registered by Application.onCreate, but
        // it won't fire immediately on boot — this ensures the list stays fresh.)
        val lastUpdated = BlocklistUpdatePrefs.getLastUpdated(context)
        val elapsedMs = System.currentTimeMillis() - lastUpdated
        val elapsedHours = elapsedMs / (1000L * 60 * 60)

        if (lastUpdated == 0L || elapsedHours >= 24) {
            Log.d(TAG, "Blocklist overdue (${elapsedHours}h since last update) — scheduling refresh")
            val request = OneTimeWorkRequestBuilder<BlocklistUpdateWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(request)
        } else {
            Log.d(TAG, "Blocklist up to date (${elapsedHours}h ago) — skipping boot refresh")
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
