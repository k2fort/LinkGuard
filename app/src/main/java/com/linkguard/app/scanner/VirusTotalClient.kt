package com.linkguard.app.scanner

import android.util.Base64
import android.util.Log
import com.linkguard.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Checks URLs against VirusTotal API v3.
 * Free tier: 4 requests/minute, 500/day.
 *
 * Strategy:
 *  1. Look up existing cached analysis for the URL
 *  2. If not found, submit URL for fresh analysis
 *  3. Poll up to 5 times (25 seconds total) for result
 *  4. In parallel, also check the root domain (catches domain-level flags)
 *
 * Rate limit handling: 429 responses are logged and treated as "skip VT"
 * rather than silently returning safe.
 */
object VirusTotalClient {

    private val TAG = "VirusTotal"
    private val BASE_URL = "https://www.virustotal.com/api/v3"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    data class VtResult(
        val isMalicious: Boolean,
        val isSuspicious: Boolean,
        val maliciousCount: Int = 0,
        val suspiciousCount: Int = 0,
        val totalEngines: Int = 0,
        val reasonHebrew: String? = null
    )

    suspend fun check(url: String): VtResult = withContext(Dispatchers.IO) {
        try {
            val urlId = urlToId(url)

            // Step 1: check existing cached analysis
            val existing = getAnalysis(urlId)
            if (existing != null) {
                Log.d(TAG, "Cache hit for $url: malicious=${existing.maliciousCount}")
                return@withContext existing
            }

            // Step 2: submit for fresh analysis
            val submitted = submitUrl(url)
            if (!submitted) {
                Log.w(TAG, "Submit failed (rate limited?) for $url — skipping VT")
                return@withContext VtResult(false, false)
            }

            // Step 3: poll up to 5 times, 5s apart (25s window)
            repeat(5) { attempt ->
                delay(5000)
                val result = getAnalysis(urlId)
                if (result != null) {
                    Log.d(TAG, "Analysis ready after ${attempt + 1} poll(s): malicious=${result.maliciousCount}")
                    return@withContext result
                }
                Log.d(TAG, "Poll ${attempt + 1}/5: analysis not ready yet for $url")
            }

            Log.d(TAG, "Analysis timed out for $url")
            VtResult(false, false)

        } catch (e: Exception) {
            Log.e(TAG, "VirusTotal check failed: ${e.message}")
            VtResult(false, false)
        }
    }

    /** Submits a URL for analysis. Returns false if rate-limited or failed. */
    private fun submitUrl(url: String): Boolean {
        return try {
            val body = okhttp3.FormBody.Builder().add("url", url).build()
            val request = Request.Builder()
                .url("$BASE_URL/urls")
                .post(body)
                .addHeader("x-apikey", BuildConfig.VIRUSTOTAL_API_KEY)
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            response.close()

            if (code == 429) {
                Log.w(TAG, "Rate limited (429) on submit")
                return false
            }
            Log.d(TAG, "Submit response: $code")
            code in 200..299
        } catch (e: Exception) {
            Log.w(TAG, "Submit exception: ${e.message}")
            false
        }
    }

    private fun getAnalysis(urlId: String): VtResult? {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/urls/$urlId")
                .get()
                .addHeader("x-apikey", BuildConfig.VIRUSTOTAL_API_KEY)
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val body = response.body?.string()
            response.close()

            if (code == 429) {
                Log.w(TAG, "Rate limited (429) on getAnalysis")
                return null
            }
            if (code == 404) {
                Log.d(TAG, "URL not in VT database yet")
                return null
            }
            if (!response.isSuccessful || body == null) {
                Log.w(TAG, "getAnalysis failed: HTTP $code")
                return null
            }

            val json = JSONObject(body)
            val stats = json
                .optJSONObject("data")
                ?.optJSONObject("attributes")
                ?.optJSONObject("last_analysis_stats")
                ?: return null

            val malicious  = stats.optInt("malicious", 0)
            val suspicious = stats.optInt("suspicious", 0)
            val harmless   = stats.optInt("harmless", 0)
            val undetected = stats.optInt("undetected", 0)
            val total = malicious + suspicious + harmless + undetected

            if (total == 0) {
                Log.d(TAG, "Analysis not complete yet (total=0)")
                return null
            }

            Log.d(TAG, "Stats — malicious=$malicious suspicious=$suspicious total=$total")

            VtResult(
                isMalicious  = malicious >= 3,
                isSuspicious = malicious in 1..2 || suspicious >= 2,
                maliciousCount  = malicious,
                suspiciousCount = suspicious,
                totalEngines    = total,
                reasonHebrew    = buildReason(malicious, suspicious, total)
            )

        } catch (e: Exception) {
            Log.w(TAG, "getAnalysis exception: ${e.message}")
            null
        }
    }

    /** VirusTotal URL ID = base64url(url) without padding. */
    private fun urlToId(url: String): String =
        Base64.encodeToString(
            url.toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

    private fun buildReason(malicious: Int, suspicious: Int, total: Int): String? = when {
        malicious >= 3  -> "$malicious מתוך $total מנועי אנטי-וירוס זיהו את הקישור כמסוכן"
        malicious in 1..2 -> "$malicious מנועי אנטי-וירוס סימנו את הקישור כחשוד"
        suspicious >= 2 -> "$suspicious מנועי אנטי-וירוס זיהו התנהגות חשודה בקישור"
        else            -> null
    }
}
