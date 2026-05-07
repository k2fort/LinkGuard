package com.linkguard.app.scanner

import android.content.Context
import android.util.Log
import com.linkguard.app.data.db.AppDatabase
import com.linkguard.app.data.db.LocalBlocklistRepo
import com.linkguard.app.data.prefs.WhitelistPrefs
import com.linkguard.app.model.ScanVerdict
import com.linkguard.app.model.VerdictLevel
import com.linkguard.app.model.VerdictSource
import com.linkguard.app.util.UrlExtractor
import com.linkguard.app.util.UrlUnshortener
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Full URL scanning pipeline:
 *
 *  1. Whitelist check      — instant SAFE if user trusts this domain
 *  2. Local blocklist      — instant DANGEROUS if domain is known-bad (offline)
 *  3. Shortlink resolution — follow redirects to reveal real destination
 *  4. Cloud checks         — GSB + VirusTotal + domain age (run in parallel)
 *  5. Verdict assembly     — combine all signals, highest severity wins
 */
object ScanOrchestrator {

    private val TAG = "ScanOrchestrator"

    /**
     * Full scan — used by the notification service.
     * Runs the complete pipeline including VT polling (up to 25s per URL).
     */
    suspend fun scan(url: String, context: Context): ScanVerdict =
        scanInternal(url, context, quickScan = false)

    /**
     * Quick scan — used by the accessibility service.
     * Skips VT polling: only queries the VT cache (< 1s). If not cached, submits for
     * background analysis and moves on. GSB + heuristics still run in full.
     * This lets the accessibility service scan multiple chat URLs quickly without
     * hitting VT rate limits or blocking for 25 seconds per URL.
     */
    suspend fun scanQuick(url: String, context: Context): ScanVerdict =
        scanInternal(url, context, quickScan = true)

    private suspend fun scanInternal(url: String, context: Context, quickScan: Boolean): ScanVerdict = coroutineScope {
        Log.d(TAG, "Starting scan for: $url (quick=$quickScan)")

        val host = UrlExtractor.extractHost(url) ?: run {
            Log.w(TAG, "Could not extract host from: $url")
            return@coroutineScope safeFallback(url)
        }

        // ── Layer 1: Whitelist ──────────────────────────────────────────────
        if (WhitelistPrefs.isTrusted(context, host)) {
            Log.d(TAG, "Whitelisted: $host")
            return@coroutineScope ScanVerdict(
                url = url,
                level = VerdictLevel.SAFE,
                reasons = listOf("הדומיין נמצא ברשימת הדומיינים הבטוחים שלך"),
                source = VerdictSource.LOCAL
            )
        }

        // ── Layer 2: Local blocklist ────────────────────────────────────────
        val blocklist = LocalBlocklistRepo(AppDatabase.getInstance(context).blocklistDao())
        if (blocklist.isDomainBlocked(host)) {
            Log.d(TAG, "Locally blocked: $host")
            return@coroutineScope ScanVerdict(
                url = url,
                level = VerdictLevel.DANGEROUS,
                reasons = listOf("הדומיין מופיע ברשימת האתרים המסוכנים הידועים"),
                source = VerdictSource.LOCAL
            )
        }

        // ── Layer 3: Always follow redirects ────────────────────────────────
        // We resolve ALL URLs, not just known shorteners. Many phishing URLs use
        // redirect chains — the original URL is clean, the final destination is malicious.
        // UrlUnshortener returns the same URL unchanged if there are no redirects.
        Log.d(TAG, "Resolving redirects for: $url")
        val resolved = UrlUnshortener.resolve(url)

        val scanUrl = resolved.finalUrl
        val resolvedHost = UrlExtractor.extractHost(scanUrl) ?: host
        Log.d(TAG, "Scanning final URL: $scanUrl")

        // ── Layer 4: Cloud checks (parallel) ────────────────────────────────
        val gsbDeferred  = async { GoogleSafeBrowsingClient.check(scanUrl) }
        val vtDeferred   = async {
            if (quickScan) VirusTotalClient.checkCacheOnly(scanUrl)
            else           VirusTotalClient.check(scanUrl)
        }
        val ageDeferred  = async { DomainAgeChecker.check(resolvedHost) }

        val gsb = gsbDeferred.await()
        val vt  = vtDeferred.await()
        val age = ageDeferred.await()

        Log.d(TAG, "GSB: isThreat=${gsb.isThreat} | " +
                "VT: malicious=${vt.maliciousCount}, suspicious=${vt.suspiciousCount} | " +
                "Domain age: ${age.ageInDays ?: "unknown"} days")

        // ── Layer 5: Verdict assembly ────────────────────────────────────────
        buildVerdict(url, resolved.finalUrl, gsb, vt, age)
    }

    internal fun buildVerdict(
        originalUrl: String,
        resolvedUrl: String,
        gsb: GoogleSafeBrowsingClient.ThreatResult,
        vt: VirusTotalClient.VtResult,
        age: DomainAgeChecker.AgeResult
    ): ScanVerdict {
        val reasons = mutableListOf<String>()
        val resolvedDisplay = if (resolvedUrl != originalUrl) resolvedUrl else null
        val isShortened = UrlExtractor.isShortened(originalUrl)

        if (isShortened) {
            reasons.add("קישור מקוצר — היעד האמיתי הוסתר מאחורי קישור קצר")
        }
        age.reasonHebrew?.let { reasons.add(it) }
        if (looksLikeRedirector(resolvedUrl)) {
            reasons.add("הכתובת נראית כמנגנון הפניה מוסתר — ייתכן שהיעד האמיתי שונה")
        }

        // DANGEROUS: Google Safe Browsing confirmed threat
        if (gsb.isThreat) {
            gsb.threatTypeHebrew?.let { reasons.add(it) }
            vt.reasonHebrew?.let { reasons.add(it) }
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.DANGEROUS,
                resolvedUrl = resolvedDisplay,
                reasons = reasons.ifEmpty { listOf("זוהה כאיום על ידי Google Safe Browsing") },
                source = VerdictSource.CLOUD
            )
        }

        // DANGEROUS: VirusTotal — multiple engines flagged
        if (vt.isMalicious) {
            vt.reasonHebrew?.let { reasons.add(it) }
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.DANGEROUS,
                resolvedUrl = resolvedDisplay,
                reasons = reasons.ifEmpty { listOf("מספר מנועי אנטי-וירוס זיהו את הקישור כמסוכן") },
                source = VerdictSource.CLOUD
            )
        }

        // SUSPICIOUS: VirusTotal flagged by a few engines
        if (vt.isSuspicious) {
            vt.reasonHebrew?.let { reasons.add(it) }
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.SUSPICIOUS,
                resolvedUrl = resolvedDisplay,
                reasons = reasons,
                source = VerdictSource.CLOUD
            )
        }

        // SUSPICIOUS: heuristic signals present (redirector pattern, new domain, etc.)
        if (reasons.isNotEmpty()) {
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.SUSPICIOUS,
                resolvedUrl = resolvedDisplay,
                reasons = reasons,
                source = VerdictSource.HEURISTIC
            )
        }

        // SAFE: passed all checks
        return ScanVerdict(
            url = originalUrl,
            level = VerdictLevel.SAFE,
            resolvedUrl = resolvedDisplay,
            reasons = listOf("הקישור עבר את כל בדיקות האבטחה בהצלחה"),
            source = VerdictSource.CLOUD
        )
    }

    /**
     * Returns true if the URL looks like a hidden redirector even after resolution:
     *  - Random-looking subdomain (8+ alphanumeric chars, e.g. "2vz7dk6r84")
     *  - Combined with a redirect-style path (/l/, /r/, /go/, /click/, /track/)
     */
    internal fun looksLikeRedirector(url: String): Boolean {
        val host = UrlExtractor.extractHost(url) ?: return false
        val path = try { java.net.URL(url).path } catch (e: Exception) { return false }

        val subdomain = host.substringBefore(".")
        val hasRandomSubdomain = subdomain.length >= 8 &&
                subdomain.all { it.isLetterOrDigit() } &&
                subdomain.any { it.isDigit() } &&
                subdomain.any { it.isLetter() }

        val redirectPaths = listOf("/l/", "/r/", "/go/", "/go?", "/click/", "/track/", "/redirect/", "/out/")
        val hasRedirectPath = redirectPaths.any { path.startsWith(it) }

        return hasRandomSubdomain && hasRedirectPath
    }

    private fun safeFallback(url: String) = ScanVerdict(
        url = url,
        level = VerdictLevel.SAFE,
        reasons = listOf("לא ניתן לנתח את הקישור"),
        source = VerdictSource.LOCAL
    )
}
