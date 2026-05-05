package com.linkguard.app.data.prefs

import android.content.Context
import com.linkguard.app.model.ScanVerdict
import com.linkguard.app.model.VerdictLevel
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the last [MAX_ENTRIES] scan results in SharedPreferences as a JSON array.
 * Newest entries are stored first.
 */
object ScanHistoryPrefs {

    private const val PREFS_NAME = "scan_history"
    private const val KEY_ENTRIES = "entries"
    private const val MAX_ENTRIES = 100

    data class HistoryEntry(
        val timestamp: Long,
        val url: String,
        val resolvedUrl: String?,
        val level: VerdictLevel,
        val reasons: List<String>,
        val sourcePackage: String
    )

    fun addEntry(context: Context, verdict: ScanVerdict, sourcePackage: String) {
        if (verdict.level == VerdictLevel.SCANNING) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = parseJson(prefs.getString(KEY_ENTRIES, null))

        val entry = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("url", verdict.url)
            put("resolvedUrl", verdict.resolvedUrl ?: "")
            put("level", verdict.level.name)
            put("reasons", JSONArray(verdict.reasons))
            put("sourcePackage", sourcePackage)
        }

        // Prepend new entry, keep max MAX_ENTRIES total
        val updated = JSONArray()
        updated.put(entry)
        for (i in 0 until minOf(existing.length(), MAX_ENTRIES - 1)) {
            updated.put(existing.get(i))
        }

        prefs.edit().putString(KEY_ENTRIES, updated.toString()).apply()
    }

    fun getHistory(context: Context): List<HistoryEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = parseJson(prefs.getString(KEY_ENTRIES, null))
        val result = mutableListOf<HistoryEntry>()

        for (i in 0 until json.length()) {
            val obj = json.optJSONObject(i) ?: continue
            val level = try {
                VerdictLevel.valueOf(obj.optString("level", "SAFE"))
            } catch (e: IllegalArgumentException) {
                VerdictLevel.SAFE
            }
            val reasonsJson = obj.optJSONArray("reasons") ?: JSONArray()
            val reasons = (0 until reasonsJson.length()).map { reasonsJson.getString(it) }

            result.add(
                HistoryEntry(
                    timestamp = obj.optLong("timestamp", 0L),
                    url = obj.optString("url", ""),
                    resolvedUrl = obj.optString("resolvedUrl", "").takeIf { it.isNotBlank() },
                    level = level,
                    reasons = reasons,
                    sourcePackage = obj.optString("sourcePackage", "")
                )
            )
        }
        return result
    }

    fun clearHistory(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_ENTRIES).apply()
    }

    private fun parseJson(raw: String?): JSONArray =
        if (raw.isNullOrBlank()) JSONArray()
        else try { JSONArray(raw) } catch (e: Exception) { JSONArray() }
}
