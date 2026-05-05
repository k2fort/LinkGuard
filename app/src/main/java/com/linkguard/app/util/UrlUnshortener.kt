package com.linkguard.app.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Resolves shortened URLs by following HTTP redirects.
 * Only follows redirects — never downloads page content.
 * Used before scanning so we always scan the final destination, not the shortlink.
 */
object UrlUnshortener {

    private val TAG = "UrlUnshortener"
    private val MAX_REDIRECTS = 10

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .followRedirects(false) // We follow manually to capture the chain
        .build()

    data class ResolveResult(
        val originalUrl: String,
        val finalUrl: String,
        val redirectChain: List<String>
    ) {
        val wasRedirected: Boolean get() = finalUrl != originalUrl
    }

    /**
     * Follows redirects from [url] and returns the final destination.
     * Safe — uses HEAD requests only, never downloads body content.
     */
    suspend fun resolve(url: String): ResolveResult = withContext(Dispatchers.IO) {
        val chain = mutableListOf<String>()
        var current = url

        try {
            repeat(MAX_REDIRECTS) {
                val request = Request.Builder()
                    .url(current)
                    .head() // HEAD only — no page content downloaded
                    .addHeader("User-Agent", "LinkGuard-Scanner/1.0")
                    .build()

                val response = client.newCall(request).execute()
                val location = response.header("Location")

                response.close()

                if (location != null) {
                    // Resolve relative redirects against current URL
                    val next = if (location.startsWith("http")) {
                        location
                    } else {
                        val base = java.net.URL(current)
                        java.net.URL(base, location).toString()
                    }
                    chain.add(current)
                    current = next
                } else {
                    return@withContext ResolveResult(url, current, chain)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to resolve $url: ${e.message}")
        }

        ResolveResult(url, current, chain)
    }
}
