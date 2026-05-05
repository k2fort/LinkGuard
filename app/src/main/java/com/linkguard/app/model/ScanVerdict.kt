package com.linkguard.app.model

/**
 * Represents the result of scanning a URL.
 *
 * Flow:
 * SCANNING → shown immediately while local + cloud checks run
 * SAFE     → all checks passed, overlay auto-dismisses after a short delay
 * SUSPICIOUS → at least one signal is concerning, user must choose to proceed or block
 * DANGEROUS  → known threat confirmed, block is the default action
 */
enum class VerdictLevel {
    SCANNING,
    SAFE,
    SUSPICIOUS,
    DANGEROUS
}

data class ScanVerdict(
    val url: String,
    val level: VerdictLevel,

    // If the original URL was a shortlink, this is where it actually leads
    val resolvedUrl: String? = null,

    // Plain-language reasons shown to the user (in Hebrew)
    // e.g. ["הדומיין נרשם לפני יומיים", "זוהה כאתר פישינג"]
    val reasons: List<String> = emptyList(),

    // Source of the verdict for the detail view
    val source: VerdictSource = VerdictSource.LOCAL
)

enum class VerdictSource {
    LOCAL,      // From local blocklist (fast, offline)
    CLOUD,      // From cloud API (Google Safe Browsing / VirusTotal)
    HEURISTIC   // From pattern analysis (domain age, shortlink, etc.)
}
