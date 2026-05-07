package com.linkguard.app.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.linkguard.app.data.db.AppDatabase
import com.linkguard.app.data.db.BlocklistEntry
import com.linkguard.app.data.prefs.BlocklistUpdatePrefs
import com.linkguard.app.util.UrlExtractor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


/**
 * Periodic background worker that refreshes the local phishing domain blocklist
 * from the OpenPhish community feed (no API key required).
 *
 * Feed URL: https://openphish.com/feed.txt
 * Format: one URL per line (e.g. "https://evil.com/phish/login")
 *
 * Strategy:
 *  1. Download feed
 *  2. Extract hostnames
 *  3. Delete old "openphish" entries from DB
 *  4. Insert fresh entries
 *  5. Record timestamp for display in Settings
 *
 * Scheduled daily via WorkManager. Returns Result.retry() on network failure
 * so WorkManager retries with exponential backoff.
 */
class BlocklistUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting blocklist update")
        return try {
            val domains = fetchOpenPhishDomains()
            if (domains.isEmpty()) {
                Log.w(TAG, "Feed returned 0 domains — retrying later")
                return Result.retry()
            }

            val dao = AppDatabase.getInstance(applicationContext).blocklistDao()
            dao.deleteBySource(SOURCE)
            val entries = domains.map { BlocklistEntry(domain = it, source = SOURCE) }
            dao.insertAll(entries)

            BlocklistUpdatePrefs.setLastUpdated(
                applicationContext,
                System.currentTimeMillis(),
                domains.size
            )
            Log.d(TAG, "Blocklist updated: ${domains.size} domains from OpenPhish")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Blocklist update failed: ${e.message}")
            Result.retry()
        }
    }

    private fun fetchOpenPhishDomains(): Set<String> {
        val request = Request.Builder()
            .url(FEED_URL)
            .header("User-Agent", "LinkGuard-Android/1.0")
            .build()

        val response = httpClient.newCall(request).execute()
        val body = try {
            if (!response.isSuccessful) {
                Log.w(TAG, "Feed HTTP ${response.code}")
                return emptySet()
            }
            response.body?.string() ?: return emptySet()
        } finally {
            response.close() // always release the connection, even on early returns above
        }

        return body.lines()
            .mapNotNull { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) null
                else UrlExtractor.extractHost(trimmed)
            }
            .filter { it.contains(".") } // skip bare hostnames without a TLD
            .toSet()
    }

    companion object {
        private const val TAG = "BlocklistUpdateWorker"
        private const val SOURCE = "openphish"
        private const val FEED_URL = "https://openphish.com/feed.txt"
        const val WORK_NAME = "blocklist_daily_update"

        // Shared singleton — creating a new OkHttpClient on every doWork() call leaks
        // thread and connection pools (they are never shut down). One instance is enough.
        private val httpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
