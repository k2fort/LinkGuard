package com.linkguard.app.scanner;

/**
 * Checks how old a domain is using the free RDAP protocol (no API key required).
 *
 * RDAP (Registration Data Access Protocol) is the modern replacement for WHOIS.
 * rdap.org routes requests to the correct registry for any TLD.
 *
 * A very new domain is a strong phishing signal — most phishing campaigns
 * register domains days before launching attacks.
 *
 * Thresholds:
 *  < 7 days  → strong suspicious signal
 *  7–30 days → moderate suspicious signal
 *  > 30 days → no signal (domain age alone not suspicious)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u000bB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/linkguard/app/scanner/DomainAgeChecker;", "", "()V", "TAG", "", "client", "Lokhttp3/OkHttpClient;", "check", "Lcom/linkguard/app/scanner/DomainAgeChecker$AgeResult;", "domain", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "AgeResult", "app_debug"})
public final class DomainAgeChecker {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "DomainAge";
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.scanner.DomainAgeChecker INSTANCE = null;
    
    private DomainAgeChecker() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object check(@org.jetbrains.annotations.NotNull()
    java.lang.String domain, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.linkguard.app.scanner.DomainAgeChecker.AgeResult> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J&\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u000fJ\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lcom/linkguard/app/scanner/DomainAgeChecker$AgeResult;", "", "ageInDays", "", "reasonHebrew", "", "(Ljava/lang/Integer;Ljava/lang/String;)V", "getAgeInDays", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getReasonHebrew", "()Ljava/lang/String;", "component1", "component2", "copy", "(Ljava/lang/Integer;Ljava/lang/String;)Lcom/linkguard/app/scanner/DomainAgeChecker$AgeResult;", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class AgeResult {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer ageInDays = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String reasonHebrew = null;
        
        public AgeResult(@org.jetbrains.annotations.Nullable()
        java.lang.Integer ageInDays, @org.jetbrains.annotations.Nullable()
        java.lang.String reasonHebrew) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getAgeInDays() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getReasonHebrew() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.scanner.DomainAgeChecker.AgeResult copy(@org.jetbrains.annotations.Nullable()
        java.lang.Integer ageInDays, @org.jetbrains.annotations.Nullable()
        java.lang.String reasonHebrew) {
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