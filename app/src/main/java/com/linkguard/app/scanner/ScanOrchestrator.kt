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

        // Re-check the whitelist with the resolved destination.
        // The first check (above) used the original host — so if the user whitelisted
        // google.com but the URL arrived as a bit.ly shortlink that resolves to Google,
        // it would have been missed. Check again now that we know where it actually goes.
        if (resolvedHost != host && WhitelistPrefs.isTrusted(context, resolvedHost)) {
            Log.d(TAG, "Whitelisted (resolved destination): $resolvedHost")
            return@coroutineScope ScanVerdict(
                url = url,
                level = VerdictLevel.SAFE,
                resolvedUrl = resolvedHost,
                reasons = listOf("הדומיין היעד נמצא ברשימת הדומיינים הבטוחים שלך"),
                source = VerdictSource.LOCAL
            )
        }

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
        val verdict = buildVerdict(url, resolved.finalUrl, gsb, vt, age)

        // Safety net: if buildVerdict returned SAFE but the original host is on the
        // high-risk TLD list, escalate to SUSPICIOUS.
        // `host` is the value already validated by extractHost() at Layer 1 (it
        // passed the null-check), so this check is robust even if buildVerdict's
        // internal extractHost() returns null due to invisible Unicode in the URL.
        if (verdict.level == VerdictLevel.SAFE && isHighRiskTld(host)) {
            Log.d(TAG, "TLD safety-net triggered for $host — escalating to SUSPICIOUS")
            val tldReason = "סיומת הדומיין (${host.trimEnd('.').substringAfterLast('.')}) נפוצה מאוד באתרי הונאה ופישינג"
            return@coroutineScope ScanVerdict(
                url = url,
                level = VerdictLevel.SUSPICIOUS,
                resolvedUrl = verdict.resolvedUrl,
                reasons = verdict.reasons + tldReason,
                source = VerdictSource.HEURISTIC
            )
        }

        verdict
    }

    internal fun buildVerdict(
        originalUrl: String,
        resolvedUrl: String,
        gsb: GoogleSafeBrowsingClient.ThreatResult,
        vt: VirusTotalClient.VtResult,
        age: DomainAgeChecker.AgeResult
    ): ScanVerdict {
        val resolvedDisplay = if (resolvedUrl != originalUrl) resolvedUrl else null
        val isShortened = UrlExtractor.isShortened(originalUrl)

        // Heuristic reasons that escalate the verdict to SUSPICIOUS.
        // NOTE: "isShortened" is intentionally NOT here — every safe bit.ly link
        // in WhatsApp would produce a false SUSPICIOUS alert, training users to ignore
        // warnings. Domain age, redirector pattern, and high-risk TLD ARE genuine signals.
        val suspiciousReasons = mutableListOf<String>()
        age.reasonHebrew?.let { suspiciousReasons.add(it) }
        if (looksLikeRedirector(resolvedUrl)) {
            suspiciousReasons.add("הכתובת נראית כמנגנון הפניה מוסתר — ייתכן שהיעד האמיתי שונה")
        }
        // Check high-risk TLD on the ORIGINAL URL first — phishing sites often redirect
        // to a clean-looking domain so the resolved URL passes, while the link the user
        // actually received was already on an abused TLD.
        val originalHost = UrlExtractor.extractHost(originalUrl)
        val resolvedHost = UrlExtractor.extractHost(resolvedUrl)
        val riskyHost = when {
            originalHost != null && isHighRiskTld(originalHost) -> originalHost
            resolvedHost != null && isHighRiskTld(resolvedHost) -> resolvedHost
            else -> null
        }
        if (riskyHost != null) {
            suspiciousReasons.add("סיומת הדומיין (${ riskyHost.trimEnd('.').substringAfterLast('.') }) נפוצה מאוד באתרי הונאה ופישינג")
        }

        // Informational note shown on the SAFE verdict for transparency.
        // Does NOT escalate severity on its own.
        val infoReasons = mutableListOf<String>()
        if (isShortened) {
            infoReasons.add("קישור מקוצר — היעד האמיתי נבדק בהצלחה")
        }

        // DANGEROUS: Google Safe Browsing confirmed threat
        if (gsb.isThreat) {
            val reasons = (suspiciousReasons + infoReasons).toMutableList()
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
            val reasons = (suspiciousReasons + infoReasons).toMutableList()
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
            val reasons = (suspiciousReasons + infoReasons).toMutableList()
            vt.reasonHebrew?.let { reasons.add(it) }
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.SUSPICIOUS,
                resolvedUrl = resolvedDisplay,
                reasons = reasons,
                source = VerdictSource.CLOUD
            )
        }

        // SUSPICIOUS: heuristic signals present (new domain, redirector pattern)
        if (suspiciousReasons.isNotEmpty()) {
            return ScanVerdict(
                url = originalUrl,
                level = VerdictLevel.SUSPICIOUS,
                resolvedUrl = resolvedDisplay,
                reasons = suspiciousReasons + infoReasons,
                source = VerdictSource.HEURISTIC
            )
        }

        // SAFE: passed all checks — include informational notes (e.g. "shortened link verified")
        return ScanVerdict(
            url = originalUrl,
            level = VerdictLevel.SAFE,
            resolvedUrl = resolvedDisplay,
            reasons = infoReasons.ifEmpty { listOf("הקישור עבר את כל בדיקות האבטחה בהצלחה") },
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

    /**
     * Returns true if the domain's TLD is in the high-abuse list.
     *
     * These TLDs consistently rank in the top-10 most-abused by Spamhaus, SURBL,
     * and abuse.ch. Legitimate businesses rarely register on these TLDs, so a hit
     * here is a meaningful risk signal even when all cloud APIs return clean.
     *
     * Source: Spamhaus TLD reputation reports, ICANN abuse data.
     */
    internal fun isHighRiskTld(host: String): Boolean {
        val tld = host.trimEnd('.').substringAfterLast('.').lowercase()
        return tld in HIGH_RISK_TLDS
    }

    private val HIGH_RISK_TLDS = setOf(
        // Consistently top-abused generic TLDs
        "top", "tk", "ml", "ga", "cf", "gq",
        // High-abuse gTLDs (cheap/free registration attracts spammers)
        "buzz", "click", "work", "loan", "win", "download",
        "racing", "stream", "trade", "accountant", "science",
        "review", "country", "cricket", "party", "faith",
        "date", "webcam", "men", "bid", "loan", "gdn"
    )

    private fun safeFallback(url: String) = ScanVerdict(
        url = url,
        level = VerdictLevel.SUSPICIOUS,
        reasons = listOf("לא ניתן לנתח את הכתובת — ייתכן שהיא מוסתרת או מעוצבת בצורה חריגה"),
        source = VerdictSource.LOCAL
    )
}
