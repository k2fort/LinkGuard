package com.linkguard.app.scanner

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Checks how old a domain is using the free RDAP protocol (no API key required).
 *
 * RDAP (Registration Data Access Protocol) is the modern replacement for WHOIS.
 * rdap.org routes requests to the correct registry for any TLD.
 *
 * A very new domain is a strong phishing signal — most phishing campaigns
 * register domains days before launching attacks.
 *
 * Thresholds:
 *   < 7 days  → strong suspicious signal
 *   7–30 days → moderate suspicious signal
 *   > 30 days → no signal (domain age alone not suspicious)
 */
object DomainAgeChecker {

    private val TAG = "DomainAge"

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    data class AgeResult(
        val ageInDays: Int?,        // null = unknown (RDAP failed or TLD not supported)
        val reasonHebrew: String?   // non-null only when age is suspiciously low
    )

    suspend fun check(domain: String): AgeResult = withContext(Dispatchers.IO) {
        try {
            val clean = domain.lowercase().removePrefix("www.").trimEnd('/')

            val request = Request.Builder()
                .url("https://rdap.org/domain/$clean")
                .get()
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            response.close()

            if (!response.isSuccessful || body.isNullOrBlank()) {
                Log.d(TAG, "RDAP returned ${response.code} for $clean")
                return@withContext AgeResult(null, null)
            }

            val json = JSONObject(body)
            val events = json.optJSONArray("events")
                ?: return@withContext AgeResult(null, null)

            var registrationDateStr: String? = null
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                if (event.optString("eventAction").equals("registration", ignoreCase = true)) {
                    registrationDateStr = event.optString("eventDate").takeIf { it.isNotBlank() }
                    break
                }
            }

            if (registrationDateStr == null) return@withContext AgeResult(null, null)

            // RDAP dates are ISO 8601 — truncate to 19 chars to drop timezone suffix,
            // then parse as UTC. Slight inaccuracy (≤1 day) is acceptable for our thresholds.
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val regDate = sdf.parse(registrationDateStr.take(19))
                ?: return@withContext AgeResult(null, null)

            val ageInDays = ((System.currentTimeMillis() - regDate.time) /
                    (1000L * 60 * 60 * 24)).toInt().coerceAtLeast(0)

            Log.d(TAG, "$clean registered $ageInDays days ago")

            val reason = when {
                ageInDays < 7  -> "הדומיין נרשם לפני $ageInDays ימים בלבד — אתר חדש מאוד וחשוד"
                ageInDays < 30 -> "הדומיין נרשם לפני $ageInDays ימים — אתר חדש יחסית"
                else           -> null
            }

            AgeResult(ageInDays, reason)

        } catch (e: Exception) {
            Log.w(TAG, "RDAP check failed for $domain: ${e.message}")
            AgeResult(null, null) // Fail open — don't block on RDAP errors
        }
    }
}
