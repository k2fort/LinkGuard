package com.linkguard.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001d2\u00020\u0001:\u0003\u001d\u001e\u001fB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002J(\u0010\u000b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r0\f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000e0\fH\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u0012\u0010\u0013\u001a\u00020\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u001cH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/linkguard/app/ui/HistoryActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/linkguard/app/databinding/ActivityHistoryBinding;", "dateFormat", "Ljava/text/SimpleDateFormat;", "timeFormat", "appLabel", "", "packageName", "groupEntries", "", "Lkotlin/Pair;", "Lcom/linkguard/app/data/prefs/ScanHistoryPrefs$HistoryEntry;", "", "items", "loadHistory", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "Companion", "HistoryAdapter", "Quad", "app_debug"})
public final class HistoryActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.linkguard.app.databinding.ActivityHistoryBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat timeFormat = null;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat dateFormat = null;
    private static final int MENU_CLEAR = 1;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.ui.HistoryActivity.Companion Companion = null;
    
    public HistoryActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    private final void loadHistory() {
    }
    
    /**
     * Groups entries by URL (same link scanned multiple times → one card).
     * Within each group: keep the most severe verdict, tie-break by latest timestamp.
     */
    private final java.util.List<kotlin.Pair<com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry, java.lang.Integer>> groupEntries(java.util.List<com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry> items) {
        return null;
    }
    
    private final java.lang.String appLabel(java.lang.String packageName) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/linkguard/app/ui/HistoryActivity$Companion;", "", "()V", "MENU_CLEAR", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0015B\u001f\u0012\u0018\u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00060\u0005\u00a2\u0006\u0002\u0010\tJ\b\u0010\f\u001a\u00020\bH\u0016J \u0010\r\u001a\u00020\u000e2\u000e\u0010\u000f\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0010\u001a\u00020\bH\u0016J \u0010\u0011\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bH\u0016R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/linkguard/app/ui/HistoryActivity$HistoryAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/linkguard/app/ui/HistoryActivity$HistoryAdapter$VH;", "Lcom/linkguard/app/ui/HistoryActivity;", "items", "", "Lkotlin/Pair;", "Lcom/linkguard/app/data/prefs/ScanHistoryPrefs$HistoryEntry;", "", "(Lcom/linkguard/app/ui/HistoryActivity;Ljava/util/List;)V", "expandedPositions", "", "getItemCount", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "VH", "app_debug"})
    final class HistoryAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.linkguard.app.ui.HistoryActivity.HistoryAdapter.VH> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<kotlin.Pair<com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry, java.lang.Integer>> items = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.Set<java.lang.Integer> expandedPositions = null;
        
        public HistoryAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<kotlin.Pair<com.linkguard.app.data.prefs.ScanHistoryPrefs.HistoryEntry, java.lang.Integer>> items) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.linkguard.app.ui.HistoryActivity.HistoryAdapter.VH onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.linkguard.app.ui.HistoryActivity.HistoryAdapter.VH holder, int position) {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/linkguard/app/ui/HistoryActivity$HistoryAdapter$VH;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "b", "Lcom/linkguard/app/databinding/ItemHistoryBinding;", "(Lcom/linkguard/app/ui/HistoryActivity$HistoryAdapter;Lcom/linkguard/app/databinding/ItemHistoryBinding;)V", "getB", "()Lcom/linkguard/app/databinding/ItemHistoryBinding;", "app_debug"})
        public final class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final com.linkguard.app.databinding.ItemHistoryBinding b = null;
            
            public VH(@org.jetbrains.annotations.NotNull()
            com.linkguard.app.databinding.ItemHistoryBinding b) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.linkguard.app.databinding.ItemHistoryBinding getB() {
                return null;
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0082\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u0019"}, d2 = {"Lcom/linkguard/app/ui/HistoryActivity$Quad;", "", "a", "", "b", "c", "", "d", "(IILjava/lang/String;I)V", "getA", "()I", "getB", "getC", "()Ljava/lang/String;", "getD", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
    static final class Quad {
        private final int a = 0;
        private final int b = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String c = null;
        private final int d = 0;
        
        public Quad(int a, int b, @org.jetbrains.annotations.NotNull()
        java.lang.String c, int d) {
            super();
        }
        
        public final int getA() {
            return 0;
        }
        
        public final int getB() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getC() {
            return null;
        }
        
        public final int getD() {
            return 0;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        public final int component4() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.linkguard.app.ui.HistoryActivity.Quad copy(int a, int b, @org.jetbrains.annotations.NotNull()
        java.lang.String c, int d) {
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