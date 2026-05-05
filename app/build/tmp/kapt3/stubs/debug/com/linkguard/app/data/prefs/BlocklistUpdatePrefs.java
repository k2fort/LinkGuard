package com.linkguard.app.data.prefs;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nJ\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/linkguard/app/data/prefs/BlocklistUpdatePrefs;", "", "()V", "KEY_LAST_COUNT", "", "KEY_LAST_UPDATED", "PREFS", "getLastCount", "", "context", "Landroid/content/Context;", "getLastUpdated", "", "setLastUpdated", "", "timestamp", "domainCount", "app_debug"})
public final class BlocklistUpdatePrefs {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS = "blocklist_update_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LAST_UPDATED = "last_updated_ms";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LAST_COUNT = "last_domain_count";
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.data.prefs.BlocklistUpdatePrefs INSTANCE = null;
    
    private BlocklistUpdatePrefs() {
        super();
    }
    
    public final void setLastUpdated(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long timestamp, int domainCount) {
    }
    
    /**
     * Returns the epoch-ms timestamp of the last successful update, or 0 if never updated.
     */
    public final long getLastUpdated(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
    
    public final int getLastCount(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
}