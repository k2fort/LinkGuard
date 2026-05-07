package com.linkguard.app.scanner

import android.util.Log
import com.linkguard.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Checks URLs against Google Safe Browsing API v4.
 * Free tier: thousands of lookups per day.
 * Returns threat type if found, null if clean.
 */
object GoogleSafeBrowsingClient {

    private val TAG = "SafeBrowsing"
    private val API_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    data class ThreatResult(
        val isThreat: Boolean,
        val threatType: String? = null,       // e.g. "MALWARE", "SOCIAL_ENGINEERING"
        val threatTypeHebrew: String? = null  // Plain Hebrew for overlay display
    )

    /**
     * Checks [url] against Google Safe Browsing.
     * Returns ThreatResult(isThreat=false) if clean or if the API call fails.
     */
    suspend fun check(url: String): ThreatResult = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientId", "linkguard-android")
                    put("clientVersion", "1.0")
                })
                put("threatInfo", JSONObject().apply {
                    put("threatTypes", JSONArray().apply {
                        put("MALWARE")
                        put("SOCIAL_ENGINEERING")   // Phishing
                        put("UNWANTED_SOFTWARE")
                        put("POTENTIALLY_HARMFUL_APPLICATION")
                    })
                    put("platformTypes", JSONArray().apply { put("ANDROID") })
                    put("threatEntryTypes", JSONArray().apply { put("URL") })
                    put("threatEntries", JSONArray().apply {
                        put(JSONObject().apply { put("url", url) })
                    })
                })
            }.toString()

            // Key sent as a header instead of a URL query param so it is not
            // visible in OkHttp logs, crash reporters, or server access logs.
            // GCP API keys support the X-Goog-Api-Key header as an alternative
            // to the ?key= query param for all REST APIs.
            val request = Request.Builder()
                .url(API_URL)
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .addHeader("X-Goog-Api-Key", BuildConfig.GOOGLE_SAFE_BROWSING_API_KEY)
                .build()

            val response = client.newCall(request).execute()
            val body = try {
                response.body?.string() ?: return@withContext ThreatResult(false)
            } finally {
                response.close()
            }

            if (!response.isSuccessful) {
                Log.w(TAG, "API error ${response.code}: $body")
                return@withContext ThreatResult(false)
            }

            val json = JSONObject(body)
            val matches = json.optJSONArray("matches")

            if (matches != null && matches.length() > 0) {
                val threatType = matches.getJSONObject(0).optString("threatType", "UNKNOWN")
                Log.d(TAG, "Threat found: $threatType for $url")
                ThreatResult(
                    isThreat = true,
                    threatType = threatType,
                    threatTypeHebrew = threatTypeToHebrew(threatType)
                )
            } else {
                Log.d(TAG, "Clean: $url")
                ThreatResult(false)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Safe Browsing check failed: ${e.message}")
            ThreatResult(false) // Fail open — don't block on API errors
        }
    }

    private fun threatTypeToHebrew(type: String): String = when (type) {
        "MALWARE" -> "זוהה כאתר המפיץ תוכנות זדוניות"
        "SOCIAL_ENGINEERING" -> "זוהה כאתר פישינג — מנסה לגנוב פרטים אישיים"
        "UNWANTED_SOFTWARE" -> "זוהה כאתר המפיץ תוכנות לא רצויות"
        "POTENTIALLY_HARMFUL_APPLICATION" -> "זוהה כאפליקציה מסוכנת"
        else -> "זוהה כאיום על ידי Google Safe Browsing"
    }
}
