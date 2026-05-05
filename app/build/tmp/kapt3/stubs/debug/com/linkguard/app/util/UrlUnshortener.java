package com.linkguard.app.util;

/**
 * Resolves shortened URLs by following HTTP redirects.
 * Only follows redirects — never downloads page content.
 * Used before scanning so we always scan the final destination, not the shortlink.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\rB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/linkguard/app/util/UrlUnshortener;", "", "()V", "MAX_REDIRECTS", "", "TAG", "", "client", "Lokhttp3/OkHttpClient;", "resolve", "Lcom/linkguard/app/util/UrlUnshortener$ResolveResult;", "url", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ResolveResult", "app_debug"})
public final class UrlUnshortener {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "UrlUnshortener";
    private static final int MAX_REDIRECTS = 10;
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.util.UrlUnshortener INSTANCE = null;
    
    private UrlUnshortener() {
        super();
    }
    
    /**
     * Follows redirects from [url] and returns the final destination.
     * Safe — uses HEAD requests only, never downloads body content.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object resolve(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.linkguard.app.util.UrlUnshortener.ResolveResult> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u00c6\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u000e2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001a"}, d2 = {"Lcom/linkguard/app/util/UrlUnshortener$ResolveResult;", "", "originalUrl", "", "finalUrl", "redirectChain", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getFinalUrl", "()Ljava/lang/String;", "getOriginalUrl", "getRedirectChain", "()Ljava/util/List;", "wasRedirected", "", "getWasRedirected", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class ResolveResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String originalUrl = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String finalUrl = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> redirectChain = null;
        
        public ResolveResult(@org.jetbrains.annotations.NotNull()
        java.lang.String originalUrl, @org.jetbrains.annotations.NotNull()
        java.lang.String finalUrl, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> redirectChain) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getOriginalUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getFinalUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getRedirectChain() {
            return null;
        }
        
        public final boolean getWasRedirected() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.util.UrlUnshortener.ResolveResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String originalUrl, @org.jetbrains.annotations.NotNull()
        java.lang.String finalUrl, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> redirectChain) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}