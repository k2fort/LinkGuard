package com.linkguard.app.scanner;

/**
 * Checks URLs against Google Safe Browsing API v4.
 * Free tier: thousands of lookups per day.
 * Returns threat type if found, null if clean.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u000eB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/linkguard/app/scanner/GoogleSafeBrowsingClient;", "", "()V", "API_URL", "", "TAG", "client", "Lokhttp3/OkHttpClient;", "check", "Lcom/linkguard/app/scanner/GoogleSafeBrowsingClient$ThreatResult;", "url", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "threatTypeToHebrew", "type", "ThreatResult", "app_debug"})
public final class GoogleSafeBrowsingClient {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SafeBrowsing";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String API_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find";
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.scanner.GoogleSafeBrowsingClient INSTANCE = null;
    
    private GoogleSafeBrowsingClient() {
        super();
    }
    
    /**
     * Checks [url] against Google Safe Browsing.
     * Returns ThreatResult(isThreat=false) if clean or if the API call fails.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object check(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.linkguard.app.scanner.GoogleSafeBrowsingClient.ThreatResult> $completion) {
        return null;
    }
    
    private final java.lang.String threatTypeToHebrew(java.lang.String type) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J+\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00032\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/linkguard/app/scanner/GoogleSafeBrowsingClient$ThreatResult;", "", "isThreat", "", "threatType", "", "threatTypeHebrew", "(ZLjava/lang/String;Ljava/lang/String;)V", "()Z", "getThreatType", "()Ljava/lang/String;", "getThreatTypeHebrew", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class ThreatResult {
        private final boolean isThreat = false;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String threatType = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String threatTypeHebrew = null;
        
        public ThreatResult(boolean isThreat, @org.jetbrains.annotations.Nullable()
        java.lang.String threatType, @org.jetbrains.annotations.Nullable()
        java.lang.String threatTypeHebrew) {
            super();
        }
        
        public final boolean isThreat() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getThreatType() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getThreatTypeHebrew() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.scanner.GoogleSafeBrowsingClient.ThreatResult copy(boolean isThreat, @org.jetbrains.annotations.Nullable()
        java.lang.String threatType, @org.jetbrains.annotations.Nullable()
        java.lang.String threatTypeHebrew) {
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