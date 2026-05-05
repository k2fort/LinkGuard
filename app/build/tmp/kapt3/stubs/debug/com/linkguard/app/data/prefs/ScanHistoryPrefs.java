package com.linkguard.app.data.prefs;

/**
 * Persists the last [MAX_ENTRIES] scan results in SharedPreferences as a JSON array.
 * Newest entries are stored first.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0016B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0004J\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\n\u001a\u00020\u000bJ\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/linkguard/app/data/prefs/ScanHistoryPrefs;", "", "()V", "KEY_ENTRIES", "", "MAX_ENTRIES", "", "PREFS_NAME", "addEntry", "", "context", "Landroid/content/Context;", "verdict", "Lcom/linkguard/app/model/ScanVerdict;", "sourcePackage", "clearHistory", "getHistory", "", "Lcom/linkguard/app/data/prefs/ScanHistoryPrefs$HistoryEntry;", "parseJson", "Lorg/json/JSONArray;", "raw", "HistoryEntry", "app_debug"})
public final class ScanHistoryPrefs {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "scan_history";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ENTRIES = "entries";
    private static final int MAX_ENTRIES = 100;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.data.prefs.ScanHistoryPrefs INSTANCE = null;
    
    private ScanHistoryPrefs() {
        super();
    }
    
    public final void addEntry(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.linkguard.app.model.ScanVerdict verdict, @org.jetbrains.annotations.NotNull()
    java.lang.String sourcePackage) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry> getHistory(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void clearHistory(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    private final org.json.JSONArray parseJson(java.lang.String raw) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\n\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00050\nH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003JM\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\b\b\u0002\u0010\u000b\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012\u00a8\u0006$"}, d2 = {"Lcom/linkguard/app/data/prefs/ScanHistoryPrefs$HistoryEntry;", "", "timestamp", "", "url", "", "resolvedUrl", "level", "Lcom/linkguard/app/model/VerdictLevel;", "reasons", "", "sourcePackage", "(JLjava/lang/String;Ljava/lang/String;Lcom/linkguard/app/model/VerdictLevel;Ljava/util/List;Ljava/lang/String;)V", "getLevel", "()Lcom/linkguard/app/model/VerdictLevel;", "getReasons", "()Ljava/util/List;", "getResolvedUrl", "()Ljava/lang/String;", "getSourcePackage", "getTimestamp", "()J", "getUrl", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class HistoryEntry {
        private final long timestamp = 0L;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String url = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String resolvedUrl = null;
        @org.jetbrains.annotations.NotNull()
        private final com.linkguard.app.model.VerdictLevel level = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> reasons = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String sourcePackage = null;
        
        public HistoryEntry(long timestamp, @org.jetbrains.annotations.NotNull()
        java.lang.String url, @org.jetbrains.annotations.Nullable()
        java.lang.String resolvedUrl, @org.jetbrains.annotations.NotNull()
        com.linkguard.app.model.VerdictLevel level, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> reasons, @org.jetbrains.annotations.NotNull()
        java.lang.String sourcePackage) {
            super();
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getResolvedUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.model.VerdictLevel getLevel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getReasons() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSourcePackage() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.model.VerdictLevel component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry copy(long timestamp, @org.jetbrains.annotations.NotNull()
        java.lang.String url, @org.jetbrains.annotations.Nullable()
        java.lang.String resolvedUrl, @org.jetbrains.annotations.NotNull()
        com.linkguard.app.model.VerdictLevel level, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> reasons, @org.jetbrains.annotations.NotNull()
        java.lang.String sourcePackage) {
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