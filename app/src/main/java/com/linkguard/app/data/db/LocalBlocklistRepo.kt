package com.linkguard.app.data.db

import android.util.Log

/**
 * Checks URLs against a local Room-backed blocklist.
 *
 * On first launch the DB is empty — call [seedIfEmpty] from Application.onCreate()
 * to populate it with the bundled starter list.
 *
 * The starter list covers:
 * - Israeli bank / government impersonation patterns
 * - Known global phishing infrastructure domains
 *
 * Future: replace/supplement seed with downloaded PhishTank CSV feed.
 */
class LocalBlocklistRepo(private val dao: BlocklistDao) {

    private val TAG = "Blocklist"

    /**
     * Returns true if [domain] (or its root domain) appears in the blocklist.
     * e.g. "login.bank-hapoalim-secure.com" matches "bank-hapoalim-secure.com"
     */
    suspend fun isDomainBlocked(domain: String): Boolean {
        val normalized = domain.lowercase().removePrefix("www.")

        // Exact match
        if (dao.isDomainBlocked(normalized) > 0) {
            Log.d(TAG, "Blocked (exact): $normalized")
            return true
        }

        // Check each parent domain level, stopping before bare TLDs.
        // e.g. "a.b.evil.com"  → checks "b.evil.com", "evil.com"  (not "com")
        //      "login.evil.co.il" → checks "evil.co.il"             (not "co.il" or "il")
        // We require at least 2 labels to remain so we never match a TLD or ccTLD.
        val parts = normalized.split(".")
        for (i in 1 until parts.size - 1) {
            val parent = parts.drop(i).joinToString(".")
            if (parent.count { it == '.' } < 1) continue  // skip bare TLDs
            if (dao.isDomainBlocked(parent) > 0) {
                Log.d(TAG, "Blocked (parent match: $parent): $normalized")
                return true
            }
        }

        return false
    }

    /** Seeds the database on first install. Safe to call on every launch — skips if already seeded. */
    suspend fun seedIfEmpty() {
        if (dao.count() > 0) return
        val entries = SEED_DOMAINS.map { BlocklistEntry(domain = it, source = "seed") }
        dao.insertAll(entries)
        Log.d(TAG, "Seeded blocklist with ${entries.size} entries")
    }

    companion object {
        /**
         * Starter blocklist bundled with the app.
         *
         * Legitimate Israeli bank domains for reference:
         *   bankhapoalim.co.il  |  leumi.co.il  |  mizrahi-tefahot.co.il
         *   discountbank.co.il  |  fibi.co.il   |  mercantile.co.il
         *   cal-online.co.il    |  max.co.il    |  isracard.co.il
         *
         * Anything impersonating these via lookalike domains is blocked below.
         */
        private val SEED_DOMAINS = listOf(
            // Israeli bank impersonation
            "bank-hapoalim-secure.com",
            "hapoalim-login.com",
            "poalim-online-il.com",
            "bankleumi-login.com",
            "leumi-secure.net",
            "leumi-online-il.com",
            "mizrahi-tefahot-online.com",
            "mizrahi-login.net",
            "discount-bank-il.com",
            "discountbank-secure.com",
            "fibi-login.com",
            "isracard-secure.com",
            "isracard-login.net",
            "cal-online-login.com",
            "cal-online-secure.net",
            "max-it-secure.com",
            "max-it-login.net",

            // Israeli government impersonation
            "misrad-habriut-il.com",
            "bituach-leumi-online.com",
            "gov-il-tax.com",
            "mas-hachnasa-online.com",
            "gov-il-payment.com",
            "misrad-hapnim-online.com",

            // Global payment / account phishing
            "secure-paypal-login.com",
            "paypal-secure-login.net",
            "paypal-verify-account.com",
            "accounts-google-verify.com",
            "google-accounts-login.net",
            "facebook-security-check.com",
            "fb-account-verify.com",
            "apple-id-verify.net",
            "apple-account-secure.com",
            "amazon-security-alert.net",
            "amazon-account-verify.com",

            // Known malware distribution infrastructure
            "download-update-now.com",
            "critical-update-required.net",
            "your-device-is-infected.com",
            "free-virus-scan-now.net",
            "security-alert-windows.com"
        )
    }
}
