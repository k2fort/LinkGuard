package com.linkguard.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0014J\b\u0010\u000b\u001a\u00020\bH\u0002J\b\u0010\f\u001a\u00020\bH\u0002R\u0012\u0010\u0003\u001a\u00060\u0004R\u00020\u0000X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/linkguard/app/ui/WhitelistActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "adapter", "Lcom/linkguard/app/ui/WhitelistActivity$WhitelistAdapter;", "binding", "Lcom/linkguard/app/databinding/ActivityWhitelistBinding;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "refreshList", "showAddDomainDialog", "WhitelistAdapter", "app_debug"})
public final class WhitelistActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.linkguard.app.databinding.ActivityWhitelistBinding binding;
    private com.linkguard.app.ui.WhitelistActivity.WhitelistAdapter adapter;
    
    public WhitelistActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void refreshList() {
    }
    
    private final void showAddDomainDialog() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0017B\'\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0002\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J \u0010\r\u001a\u00020\t2\u000e\u0010\u000e\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u000f\u001a\u00020\fH\u0016J \u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016J\u0014\u0010\u0014\u001a\u00020\t2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00060\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/linkguard/app/ui/WhitelistActivity$WhitelistAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/linkguard/app/ui/WhitelistActivity$WhitelistAdapter$ViewHolder;", "Lcom/linkguard/app/ui/WhitelistActivity;", "domains", "", "", "onRemove", "Lkotlin/Function1;", "", "(Lcom/linkguard/app/ui/WhitelistActivity;Ljava/util/List;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setDomains", "newDomains", "", "ViewHolder", "app_debug"})
    final class WhitelistAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.linkguard.app.ui.WhitelistActivity.WhitelistAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> domains = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit> onRemove = null;
        
        public WhitelistAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> domains, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRemove) {
            super();
        }
        
        public final void setDomains(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> newDomains) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.linkguard.app.ui.WhitelistActivity.WhitelistAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.linkguard.app.ui.WhitelistActivity.WhitelistAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/linkguard/app/ui/WhitelistActivity$WhitelistAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/linkguard/app/ui/WhitelistActivity$WhitelistAdapter;Landroid/view/View;)V", "btnRemove", "Landroid/widget/ImageButton;", "getBtnRemove", "()Landroid/widget/ImageButton;", "tvDomain", "Landroid/widget/TextView;", "getTvDomain", "()Landroid/widget/TextView;", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvDomain = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.ImageButton btnRemove = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View view) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvDomain() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.ImageButton getBtnRemove() {
                return null;
            }
        }
    }
}