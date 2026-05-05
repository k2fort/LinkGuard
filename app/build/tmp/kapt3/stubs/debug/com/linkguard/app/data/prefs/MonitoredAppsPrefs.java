package com.linkguard.app.data.prefs;

/**
 * Stores which app packages the user has chosen to monitor.
 * Uses SharedPreferences — lightweight enough for a set of package names.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\f2\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004J\u0016\u0010\u000f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004J\u001c\u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/linkguard/app/data/prefs/MonitoredAppsPrefs;", "", "()V", "KEY_PACKAGES", "", "PREFS_NAME", "addPackage", "", "context", "Landroid/content/Context;", "packageName", "getMonitoredPackages", "", "isMonitored", "", "removePackage", "setMonitoredPackages", "packages", "app_debug"})
public final class MonitoredAppsPrefs {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "monitored_apps";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PACKAGES = "packages";
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.data.prefs.MonitoredAppsPrefs INSTANCE = null;
    
    private MonitoredAppsPrefs() {
        super();
    }
    
    /**
     * Returns the set of package names to monitor.
     * Empty set = user has not selected any apps yet.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getMonitoredPackages(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void setMonitoredPackages(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> packages) {
    }
    
    public final void addPackage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
    }
    
    public final void removePackage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
    }
    
    public final boolean isMonitored(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return false;
    }
}