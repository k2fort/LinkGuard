package com.linkguard.app.data.db;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/linkguard/app/data/db/LocalBlocklistRepo;", "", "dao", "Lcom/linkguard/app/data/db/BlocklistDao;", "(Lcom/linkguard/app/data/db/BlocklistDao;)V", "TAG", "", "isDomainBlocked", "", "domain", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "seedIfEmpty", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class LocalBlocklistRepo {
    @org.jetbrains.annotations.NotNull()
    private final com.linkguard.app.data.db.BlocklistDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "Blocklist";
    
    /**
     * Starter blocklist bundled with the app.
     *
     * Legitimate Israeli bank domains for reference:
     *  bankhapoalim.co.il  |  leumi.co.il  |  mizrahi-tefahot.co.il
     *  discountbank.co.il  |  fibi.co.il   |  mercantile.co.il
     *  cal-online.co.il    |  max.co.il    |  isracard.co.il
     *
     * Anything impersonating these via lookalike domains is blocked below.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> SEED_DOMAINS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.data.db.LocalBlocklistRepo.Companion Companion = null;
    
    public LocalBlocklistRepo(@org.jetbrains.annotations.NotNull()
    com.linkguard.app.data.db.BlocklistDao dao) {
        super();
    }
    
    /**
     * Returns true if [domain] (or its root domain) appears in the blocklist.
     * e.g. "login.bank-hapoalim-secure.com" matches "bank-hapoalim-secure.com"
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isDomainBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String domain, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Seeds the database on first install. Safe to call on every launch — skips if already seeded.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object seedIfEmpty(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/linkguard/app/data/db/LocalBlocklistRepo$Companion;", "", "()V", "SEED_DOMAINS", "", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}