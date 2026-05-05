package com.linkguard.app.scanner;

/**
 * Thread-safe singleton that prevents the same URL from being scanned twice
 * within a 60-second window.
 *
 * Shared between LinkGuardNotificationService and LinkGuardAccessibilityService
 * so a URL detected in both a notification AND on-screen is only scanned once.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00040\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/linkguard/app/scanner/ScanDeduplicator;", "", "()V", "WINDOW_MS", "", "cache", "", "", "lock", "shouldScan", "", "url", "app_debug"})
public final class ScanDeduplicator {
    private static final long WINDOW_MS = 60000L;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.Long> cache = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.Object lock = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.scanner.ScanDeduplicator INSTANCE = null;
    
    private ScanDeduplicator() {
        super();
    }
    
    /**
     * Returns true and records the URL if it hasn't been scanned recently.
     * Returns false (skip) if the same URL was already scanned within [WINDOW_MS].
     */
    public final boolean shouldScan(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return false;
    }
}