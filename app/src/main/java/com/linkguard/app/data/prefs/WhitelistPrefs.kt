package com.linkguard.app.data.prefs

import android.content.Context

/**
 * Stores the user's trusted domains in SharedPreferences.
 *
 * A trusted domain also covers all its subdomains:
 * trusting "google.com" means "mail.google.com" is also trusted.
 */
object WhitelistPrefs {

    private const val PREFS_NAME = "whitelist_prefs"
    private const val KEY_DOMAINS = "trusted_domains"

    /**
     * Returns true if [domain] is trusted (exact match or subdomain of a trusted entry).
     */
    fun isTrusted(context: Context, domain: String): Boolean {
        val normalized = normalize(domain)
        return getTrustedDomains(context).any { trusted ->
            normalized == trusted || normalized.endsWith(".$trusted")
        }
    }

    fun addDomain(context: Context, domain: String) {
        val domains = getTrustedDomains(context).toMutableSet()
        domains.add(normalize(domain))
        prefs(context).edit().putStringSet(KEY_DOMAINS, domains).apply()
    }

    fun removeDomain(context: Context, domain: String) {
        val domains = getTrustedDomains(context).toMutableSet()
        domains.remove(normalize(domain))
        prefs(context).edit().putStringSet(KEY_DOMAINS, domains).apply()
    }

    fun getTrustedDomains(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_DOMAINS, emptySet()) ?: emptySet()

    private fun normalize(domain: String): String =
        // trimEnd('.') strips FQDN trailing dot from Android ART's java.net.URL
        // before www. removal, so "www.youtube.com." → "youtube.com" not "youtube.com."
        domain.lowercase().trimEnd('.').removePrefix("www.").trimEnd('/')

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
