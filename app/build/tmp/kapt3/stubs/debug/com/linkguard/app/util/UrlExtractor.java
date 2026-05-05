package com.linkguard.app.util;

/**
 * Extracts URLs from plain text (notification bodies, message content, etc.).
 *
 * Three-pass strategy:
 *  Pass 1 — full https?:// URLs       (highest confidence, consumes character ranges)
 *  Pass 2 — www. URLs                 (skips positions already consumed by Pass 1)
 *  Pass 3 — bare domain URLs          (skips positions already consumed by Pass 1/2)
 *
 * Position tracking prevents the same substring from being extracted twice
 * (e.g. "leumi.co.il" re-extracted from inside "https://web.leumi.co.il/...").
 *
 * Host deduplication treats "www.X" and "X" as the same domain so that a URL
 * appearing with and without "www." only produces one scan.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00052\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002J\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u0005J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u0012\u001a\u00020\u0005J\u000e\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0005J\u0010\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0002J\u0010\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0002J\u001a\u0010\u0016\u001a\u00020\u000b*\b\u0012\u0004\u0012\u00020\u00170\u00112\u0006\u0010\u0018\u001a\u00020\u0017H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/linkguard/app/util/UrlExtractor;", "", "()V", "SHORTENER_DOMAINS", "", "", "URL_BARE_DOMAIN", "Lkotlin/text/Regex;", "URL_WITH_SCHEME", "URL_WWW", "alreadyCovered", "", "candidate", "existing", "extractHost", "url", "extractUrls", "", "text", "isShortened", "normalise", "stripTrailingPunctuation", "overlaps", "Lkotlin/ranges/IntRange;", "other", "app_debug"})
public final class UrlExtractor {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex URL_WITH_SCHEME = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex URL_WWW = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex URL_BARE_DOMAIN = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SHORTENER_DOMAINS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.util.UrlExtractor INSTANCE = null;
    
    private UrlExtractor() {
        super();
    }
    
    /**
     * Extracts all URLs from [text].
     * Returns a deduplicated list, all normalised to https://.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> extractUrls(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
        return null;
    }
    
    /**
     * Returns true if [url] is a shortened URL that needs resolving.
     */
    public final boolean isShortened(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return false;
    }
    
    /**
     * Extracts host from a URL. e.g. "https://bit.ly/3x" → "bit.ly"
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String extractHost(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
        return null;
    }
    
    /**
     * Ensures URL starts with https://
     */
    private final java.lang.String normalise(java.lang.String url) {
        return null;
    }
    
    /**
     * Returns true if [candidate] is already represented in [existing].
     *
     * "Covered" means the same root domain is already present:
     *  - exact host match:               pepper.co.il == pepper.co.il
     *  - www variant:                    www.pepper.co.il  ↔  pepper.co.il
     *  - subdomain of existing:          web.leumi.co.il covered by web.leumi.co.il (exact)
     *
     * We do NOT suppress genuine subdomain differences (a.example.com vs b.example.com)
     * — those could be different services. Only www. is treated as identical.
     */
    private final boolean alreadyCovered(java.lang.String candidate, java.util.Set<java.lang.String> existing) {
        return false;
    }
    
    private final java.lang.String stripTrailingPunctuation(java.lang.String url) {
        return null;
    }
    
    private final boolean overlaps(java.util.List<kotlin.ranges.IntRange> $this$overlaps, kotlin.ranges.IntRange other) {
        return false;
    }
}