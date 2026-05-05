package com.linkguard.app.data.prefs

import android.content.Context

object BlocklistUpdatePrefs {
    private const val PREFS = "blocklist_update_prefs"
    private const val KEY_LAST_UPDATED = "last_updated_ms"
    private const val KEY_LAST_COUNT = "last_domain_count"

    fun setLastUpdated(context: Context, timestamp: Long, domainCount: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong(KEY_LAST_UPDATED, timestamp)
            .putInt(KEY_LAST_COUNT, domainCount)
            .apply()
    }

    /** Returns the epoch-ms timestamp of the last successful update, or 0 if never updated. */
    fun getLastUpdated(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_UPDATED, 0L)

    fun getLastCount(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_LAST_COUNT, 0)
}
