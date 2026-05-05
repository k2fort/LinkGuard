package com.linkguard.app.data.prefs;

/**
 * Stores the user's trusted domains in SharedPreferences.
 *
 * A trusted domain also covers all its subdomains:
 * trusting "google.com" means "mail.google.com" is also trusted.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\f2\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004J\u0010\u0010\u000f\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0004H\u0002J\u0018\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u00112\u0006\u0010\b\u001a\u00020\tH\u0002J\u0016\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/linkguard/app/data/prefs/WhitelistPrefs;", "", "()V", "KEY_DOMAINS", "", "PREFS_NAME", "addDomain", "", "context", "Landroid/content/Context;", "domain", "getTrustedDomains", "", "isTrusted", "", "normalize", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "removeDomain", "app_debug"})
public final class WhitelistPrefs {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "whitelist_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_DOMAINS = "trusted_domains";
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.data.prefs.WhitelistPrefs INSTANCE = null;
    
    private WhitelistPrefs() {
        super();
    }
    
    /**
     * Returns true if [domain] is trusted (exact match or subdomain of a trusted entry).
     */
    public final boolean isTrusted(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String domain) {
        return false;
    }
    
    public final void addDomain(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String domain) {
    }
    
    public final void removeDomain(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String domain) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getTrustedDomains(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    private final java.lang.String normalize(java.lang.String domain) {
        return null;
    }
    
    private final android.content.SharedPreferences prefs(android.content.Context context) {
        return null;
    }
}