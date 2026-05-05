package com.linkguard.app.scanner

/**
 * Thread-safe singleton that prevents the same URL from being scanned twice
 * within a 60-second window.
 *
 * Shared between LinkGuardNotificationService and LinkGuardAccessibilityService
 * so a URL detected in both a notification AND on-screen is only scanned once.
 */
object ScanDeduplicator {

    private const val WINDOW_MS = 60_000L
    private val cache = mutableMapOf<String, Long>()
    private val lock = Any()

    /**
     * Returns true and records the URL if it hasn't been scanned recently.
     * Returns false (skip) if the same URL was already scanned within [WINDOW_MS].
     */
    fun shouldScan(url: String): Boolean {
        val now = System.currentTimeMillis()
        synchronized(lock) {
            cache.entries.removeIf { now - it.value > WINDOW_MS }
            val last = cache[url]
            return if (last != null && now - last < WINDOW_MS) {
                false
            } else {
                cache[url] = now
                true
            }
        }
    }
}
