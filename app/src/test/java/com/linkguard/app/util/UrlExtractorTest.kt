package com.linkguard.app.util

import org.junit.Assert.*
import org.junit.Test

class UrlExtractorTest {

    @Test
    fun `extracts simple https url`() {
        val result = UrlExtractor.extractUrls("Check this out: https://example.com/page")
        assertEquals(listOf("https://example.com/page"), result)
    }

    @Test
    fun `extracts url from WhatsApp-style message`() {
        val result = UrlExtractor.extractUrls("הי! לחץ כאן: https://bank-login.example.com/auth?token=abc123")
        assertTrue(result.any { it.contains("bank-login.example.com") })
    }

    @Test
    fun `strips trailing period`() {
        val result = UrlExtractor.extractUrls("Visit https://example.com.")
        assertEquals(listOf("https://example.com"), result)
    }

    @Test
    fun `strips trailing closing paren`() {
        val result = UrlExtractor.extractUrls("See (https://example.com)")
        assertEquals(listOf("https://example.com"), result)
    }

    @Test
    fun `extracts multiple urls`() {
        val result = UrlExtractor.extractUrls("Go to https://a.com and https://b.com for details.")
        assertEquals(2, result.size)
    }

    @Test
    fun `deduplicates repeated urls`() {
        val result = UrlExtractor.extractUrls("https://example.com https://example.com")
        assertEquals(1, result.size)
    }

    @Test
    fun `returns empty list when no url present`() {
        val result = UrlExtractor.extractUrls("שלום, מה שלומך?")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `extracts bare www url as seen in WhatsApp notifications`() {
        val result = UrlExtractor.extractUrls("www.google.com")
        assertEquals(1, result.size)
        assertTrue(result.first().startsWith("https://"))
        assertTrue(result.first().contains("google.com"))
    }

    @Test
    fun `normalises bare www url to https`() {
        val result = UrlExtractor.extractUrls("check this: www.phishing-site.xyz/login")
        assertTrue(result.any { it.startsWith("https://") && it.contains("phishing-site.xyz") })
    }

    @Test
    fun `extracts bare domain without www`() {
        val result = UrlExtractor.extractUrls("google.com")
        assertTrue(result.isNotEmpty())
        assertTrue(result.first().startsWith("https://"))
        assertTrue(result.first().contains("google.com"))
    }

    @Test
    fun `extracts bare domain with path`() {
        val result = UrlExtractor.extractUrls("זיהוי: bank-login.com/auth?user=test")
        assertTrue(result.isNotEmpty())
        assertTrue(result.first().contains("bank-login.com"))
    }

    @Test
    fun `extracts israeli co-il domain`() {
        val result = UrlExtractor.extractUrls("fake-bank.co.il/login")
        assertTrue(result.isNotEmpty())
        assertTrue(result.first().contains("fake-bank.co.il"))
    }

    @Test
    fun `does not extract email addresses as urls`() {
        val result = UrlExtractor.extractUrls("contact us at support@example.com")
        // Should not treat the domain part of an email as a URL
        assertTrue(result.none { it.contains("@") })
    }

    @Test
    fun `does not duplicate url already captured by scheme regex`() {
        val result = UrlExtractor.extractUrls("https://google.com and google.com")
        val googleUrls = result.filter { it.contains("google.com") }
        assertEquals(1, googleUrls.size)
    }

    @Test
    fun `extracts bare domain with es tld`() {
        val result = UrlExtractor.extractUrls("israel-auth.es")
        assertTrue(result.isNotEmpty())
        assertTrue(result.first().contains("israel-auth.es"))
    }

    @Test
    fun `detects bitly as shortened`() {
        assertTrue(UrlExtractor.isShortened("https://bit.ly/3xYz123"))
    }

    @Test
    fun `detects tco as shortened`() {
        assertTrue(UrlExtractor.isShortened("https://t.co/abc"))
    }

    @Test
    fun `does not flag regular url as shortened`() {
        assertFalse(UrlExtractor.isShortened("https://www.google.com"))
    }

    @Test
    fun `extracts host correctly`() {
        assertEquals("example.com", UrlExtractor.extractHost("https://example.com/path?q=1"))
    }

    @Test
    fun `extracts url with query params`() {
        val url = "https://phishing.example.com/login?user=test&token=abc"
        val result = UrlExtractor.extractUrls("Click: $url now!")
        assertEquals(listOf(url), result)
    }
}
