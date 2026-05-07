package com.linkguard.app.scanner

import com.linkguard.app.model.VerdictLevel
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ScanOrchestrator verdict assembly and redirect heuristic.
 *
 * buildVerdict and looksLikeRedirector are internal functions — tested directly
 * to verify the core decision logic without spinning up Android framework.
 */
class ScanOrchestratorTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun gsb(threat: Boolean, reason: String? = null) =
        GoogleSafeBrowsingClient.ThreatResult(threat, reason)

    private fun vt(
        malicious: Int = 0,
        suspicious: Int = 0,
        total: Int = 50,
        reason: String? = null
    ) = VirusTotalClient.VtResult(
        isMalicious  = malicious >= 3,
        isSuspicious = malicious in 1..2 || suspicious >= 2,
        maliciousCount  = malicious,
        suspiciousCount = suspicious,
        totalEngines    = total,
        reasonHebrew    = reason
    )

    private fun age(days: Int?) =
        DomainAgeChecker.AgeResult(
            ageInDays    = days,
            reasonHebrew = when {
                days == null  -> null
                days < 7      -> "הדומיין נרשם לפני $days ימים בלבד"
                days < 30     -> "הדומיין נרשם לפני $days ימים"
                else          -> null
            }
        )

    // ── buildVerdict: DANGEROUS paths ────────────────────────────────────────

    @Test
    fun `GSB threat returns DANGEROUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://evil.com", "https://evil.com",
            gsb(true, "פישינג"), vt(), age(null)
        )
        assertEquals(VerdictLevel.DANGEROUS, v.level)
    }

    @Test
    fun `VT malicious at threshold (3) returns DANGEROUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://evil.com", "https://evil.com",
            gsb(false), vt(malicious = 3), age(null)
        )
        assertEquals(VerdictLevel.DANGEROUS, v.level)
    }

    @Test
    fun `VT malicious above threshold returns DANGEROUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://evil.com", "https://evil.com",
            gsb(false), vt(malicious = 10), age(null)
        )
        assertEquals(VerdictLevel.DANGEROUS, v.level)
    }

    // ── buildVerdict: SUSPICIOUS paths ───────────────────────────────────────

    @Test
    fun `VT 1 malicious engine returns SUSPICIOUS not DANGEROUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://maybe-bad.com", "https://maybe-bad.com",
            gsb(false), vt(malicious = 1), age(null)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
    }

    @Test
    fun `VT 2 malicious engines returns SUSPICIOUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://maybe-bad.com", "https://maybe-bad.com",
            gsb(false), vt(malicious = 2), age(null)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
    }

    @Test
    fun `VT 2 suspicious engines returns SUSPICIOUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://maybe-bad.com", "https://maybe-bad.com",
            gsb(false), vt(suspicious = 2), age(null)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
    }

    @Test
    fun `new domain (under 7 days) with no other signals returns SUSPICIOUS`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://new-site.com", "https://new-site.com",
            gsb(false), vt(), age(3)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
        assertTrue(v.reasons.any { it.contains("3") })
    }

    @Test
    fun `domain 7-30 days old returns SUSPICIOUS via heuristic`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://fairly-new.com", "https://fairly-new.com",
            gsb(false), vt(), age(15)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
    }

    @Test
    fun `shortened URL resolving to clean destination is SAFE with info reason`() {
        // Shortened links that resolve to a clean destination should NOT escalate to
        // SUSPICIOUS — the short-link note is informational only (infoReasons).
        val v = ScanOrchestrator.buildVerdict(
            "https://bit.ly/3xYz", "https://real-destination.com/page",
            gsb(false), vt(), age(null)
        )
        assertEquals(VerdictLevel.SAFE, v.level)
        assertTrue("Expected short-link info reason", v.reasons.any { it.contains("מקוצר") })
    }

    // ── buildVerdict: SAFE path ───────────────────────────────────────────────

    @Test
    fun `clean URL with no signals returns SAFE`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://google.com", "https://google.com",
            gsb(false), vt(), age(null)
        )
        assertEquals(VerdictLevel.SAFE, v.level)
    }

    @Test
    fun `domain over 30 days old returns SAFE when all else clean`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://established.com", "https://established.com",
            gsb(false), vt(), age(365)
        )
        assertEquals(VerdictLevel.SAFE, v.level)
    }

    @Test
    fun `VT 1 suspicious engine alone returns SAFE (below threshold)`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://borderline.com", "https://borderline.com",
            gsb(false), vt(suspicious = 1), age(null)
        )
        assertEquals(VerdictLevel.SAFE, v.level)
    }

    // ── buildVerdict: resolvedUrl display ────────────────────────────────────

    @Test
    fun `resolvedUrl is null when original and resolved are the same`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://example.com", "https://example.com",
            gsb(false), vt(), age(null)
        )
        assertNull(v.resolvedUrl)
    }

    @Test
    fun `resolvedUrl is populated when redirect occurred`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://bit.ly/abc", "https://real-site.com/page",
            gsb(false), vt(), age(null)
        )
        assertEquals("https://real-site.com/page", v.resolvedUrl)
    }

    // ── buildVerdict: GSB wins over VT ───────────────────────────────────────

    @Test
    fun `GSB threat dominates even when VT clean`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://phishing.com", "https://phishing.com",
            gsb(true, "פישינג"), vt(malicious = 0), age(null)
        )
        assertEquals(VerdictLevel.DANGEROUS, v.level)
    }

    // ── isHighRiskTld ─────────────────────────────────────────────────────────

    @Test
    fun `high-risk TLD top triggers SUSPICIOUS when cloud is clean`() {
        val v = ScanOrchestrator.buildVerdict(
            "https://ghostraper.top", "https://ghostraper.top",
            gsb(false), vt(), age(null)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
        assertTrue(v.reasons.any { it.contains("top") })
    }

    @Test
    fun `high-risk TLD on original URL caught even when resolved URL is clean`() {
        // Phishing sites often redirect to a safe-looking domain after landing.
        // The original URL's TLD must be checked regardless of where it resolves.
        val v = ScanOrchestrator.buildVerdict(
            "https://ghostraper.top", "https://some-clean-site.com/page",
            gsb(false), vt(), age(null)
        )
        assertEquals(VerdictLevel.SUSPICIOUS, v.level)
        assertTrue(v.reasons.any { it.contains("top") })
    }

    @Test
    fun `high-risk TLD tk triggers SUSPICIOUS`() {
        assertTrue(ScanOrchestrator.isHighRiskTld("evil.tk"))
    }

    @Test
    fun `legitimate TLD com does not trigger high-risk heuristic`() {
        assertFalse(ScanOrchestrator.isHighRiskTld("google.com"))
    }

    @Test
    fun `legitimate TLD co il does not trigger high-risk heuristic`() {
        assertFalse(ScanOrchestrator.isHighRiskTld("leumi.co.il"))
    }

    // ── looksLikeRedirector ───────────────────────────────────────────────────

    @Test
    fun `random subdomain with redirect path triggers heuristic`() {
        assertTrue(ScanOrchestrator.looksLikeRedirector(
            "https://2vz7dk6r84.example.com/l/abc"
        ))
    }

    @Test
    fun `random subdomain with go path triggers heuristic`() {
        assertTrue(ScanOrchestrator.looksLikeRedirector(
            "https://a1b2c3d4e5.tracker.com/go/link"
        ))
    }

    @Test
    fun `normal subdomain does not trigger heuristic`() {
        assertFalse(ScanOrchestrator.looksLikeRedirector(
            "https://www.google.com/search?q=test"
        ))
    }

    @Test
    fun `random subdomain without redirect path does not trigger`() {
        assertFalse(ScanOrchestrator.looksLikeRedirector(
            "https://2vz7dk6r84.example.com/products/item"
        ))
    }

    @Test
    fun `short subdomain with redirect path does not trigger (length guard)`() {
        assertFalse(ScanOrchestrator.looksLikeRedirector(
            "https://ab.example.com/l/abc"
        ))
    }
}
