package com.linkguard.app.util

/**
 * Extracts URLs from plain text (notification bodies, message content, etc.).
 *
 * Three-pass strategy:
 *   Pass 1 — full https?:// URLs       (highest confidence, consumes character ranges)
 *   Pass 2 — www. URLs                 (skips positions already consumed by Pass 1)
 *   Pass 3 — bare domain URLs          (skips positions already consumed by Pass 1/2)
 *
 * Position tracking prevents the same substring from being extracted twice
 * (e.g. "leumi.co.il" re-extracted from inside "https://web.leumi.co.il/...").
 *
 * Host deduplication treats "www.X" and "X" as the same domain so that a URL
 * appearing with and without "www." only produces one scan.
 */
object UrlExtractor {

    // Pass 1: full URLs with scheme
    private val URL_WITH_SCHEME = Regex(
        """https?://[^\s\[\](){}<>"'«»]+""",
        setOf(RegexOption.IGNORE_CASE)
    )

    // Pass 2: bare www. URLs (WhatsApp sometimes strips the scheme)
    private val URL_WWW = Regex(
        """www\.[a-zA-Z0-9\-]+\.[a-zA-Z]{2,}[^\s\[\](){}<>"'«»]*""",
        setOf(RegexOption.IGNORE_CASE)
    )

    // Pass 3: bare domain URLs (no scheme, no www)
    // Negative lookbehind avoids email addresses and words ending in a dot
    private val URL_BARE_DOMAIN = Regex(
        """(?<![/@\w])([a-zA-Z0-9][a-zA-Z0-9\-]{1,61}[a-zA-Z0-9]\.)+""" +
        """(?:com|net|org|io|co\.il|co\.uk|il|me|ly|app|dev|xyz|info|biz|gl|gg|ai|tv|""" +
        """click|link|site|online|store|shop|tech|news|blog|page|live|top|pro|fun|world|""" +
        """cloud|social|chat|stream|mobi|name|museum|travel|jobs|post|web|wiki|zone|""" +
        """eu|de|fr|es|it|nl|pl|pt|ro|gr|hu|cz|sk|se|no|fi|dk|be|at|ch|""" +
        """ru|br|au|ca|jp|in|cn|us|uk|nz|mx|za|sg|hk|tr|ua|by|kz|ge|am)""" +
        """(?:/[^\s\[\](){}<>"'«»]*)?""",
        setOf(RegexOption.IGNORE_CASE)
    )

    // Known URL shortener domains
    private val SHORTENER_DOMAINS = setOf(
        "bit.ly", "tinyurl.com", "t.co", "t.me", "goo.gl",
        "ow.ly", "buff.ly", "rebrand.ly", "short.link", "tiny.cc",
        "is.gd", "v.gd", "cutt.ly", "rb.gy", "shorturl.at"
    )

    /**
     * Extracts all URLs from [text].
     * Returns a deduplicated list, all normalised to https://.
     */
    fun extractUrls(text: String): List<String> {
        val results = mutableSetOf<String>()
        // Track character ranges already consumed so later passes don't re-extract
        // substrings that were part of a higher-confidence match.
        val consumed = mutableListOf<IntRange>()

        // ── Pass 1: full scheme URLs ───────────────────────────────────────
        for (match in URL_WITH_SCHEME.findAll(text)) {
            val url = stripTrailingPunctuation(match.value)
            if (url.isNotBlank()) {
                results.add(url)
                consumed.add(match.range)
            }
        }

        // ── Pass 2: www. URLs ──────────────────────────────────────────────
        for (match in URL_WWW.findAll(text)) {
            if (consumed.overlaps(match.range)) continue
            val url = normalise(stripTrailingPunctuation(match.value))
            if (url.isNotBlank() && !alreadyCovered(url, results)) {
                results.add(url)
                consumed.add(match.range)
            }
        }

        // ── Pass 3: bare domain URLs ───────────────────────────────────────
        for (match in URL_BARE_DOMAIN.findAll(text)) {
            if (consumed.overlaps(match.range)) continue
            val url = normalise(stripTrailingPunctuation(match.value))
            if (url.isNotBlank() && !alreadyCovered(url, results)) {
                results.add(url)
                // No need to add to consumed — Pass 3 is last
            }
        }

        return results.toList()
    }

    /** Returns true if [url] is a shortened URL that needs resolving. */
    fun isShortened(url: String): Boolean {
        val host = extractHost(url)?.lowercase() ?: return false
        return SHORTENER_DOMAINS.any { host == it || host.endsWith(".$it") }
    }

    /** Extracts host from a URL. e.g. "https://bit.ly/3x" → "bit.ly" */
    fun extractHost(url: String): String? {
        return try {
            val withScheme = if (!url.startsWith("http", ignoreCase = true)) "https://$url" else url
            // trimEnd('.') strips the FQDN trailing dot that Android ART's java.net.URL
            // appends (e.g. "ghostraper.top." → "ghostraper.top"). Without this, TLD
            // checks like substringAfterLast('.') return "" and silently miss the domain.
            java.net.URL(withScheme).host.takeIf { it.isNotBlank() }?.trimEnd('.')
        } catch (e: Exception) {
            null
        }
    }

    /** Ensures URL starts with https:// */
    private fun normalise(url: String): String =
        if (url.startsWith("http", ignoreCase = true)) url else "https://$url"

    /**
     * Returns true if [candidate] is already represented in [existing].
     *
     * "Covered" means the same root domain is already present:
     *   - exact host match:               pepper.co.il == pepper.co.il
     *   - www variant:                    www.pepper.co.il  ↔  pepper.co.il
     *   - subdomain of existing:          web.leumi.co.il covered by web.leumi.co.il (exact)
     *
     * We do NOT suppress genuine subdomain differences (a.example.com vs b.example.com)
     * — those could be different services. Only www. is treated as identical.
     */
    private fun alreadyCovered(candidate: String, existing: Set<String>): Boolean {
        val host = extractHost(candidate)?.lowercase() ?: return false
        val rootHost = host.removePrefix("www.")
        return existing.any { existingUrl ->
            val existingHost = extractHost(existingUrl)?.lowercase() ?: return@any false
            val existingRoot = existingHost.removePrefix("www.")
            existingRoot == rootHost
        }
    }

    private fun stripTrailingPunctuation(url: String): String {
        var result = url
        val trailingChars = setOf('.', ',', '!', '?', ')', ']', '}', '\'', '"', '»', ':')
        while (result.isNotEmpty() && result.last() in trailingChars) {
            result = result.dropLast(1)
        }
        return result
    }

    private fun List<IntRange>.overlaps(other: IntRange): Boolean =
        any { it.first <= other.last && it.last >= other.first }
}
