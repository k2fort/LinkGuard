package com.linkguard.app.overlay;

/**
 * Manages the system overlay window shown when a URL is detected in a notification.
 *
 * Lifecycle:
 * 1. showScanning(url) — display immediately with spinner
 * 2. updateVerdict(verdict) — called when local/cloud scan completes
 * 3. dismiss() — called by user action or auto-dismiss for SAFE verdicts
 *
 * Must be called from the main thread.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J6\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00132\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00162\u0006\u0010\u0018\u001a\u00020\u0013H\u0002J\u0006\u0010\u0019\u001a\u00020\u0010J\b\u0010\u001a\u001a\u00020\u0010H\u0002J\u0006\u0010\u001b\u001a\u00020\u001cJ\u0010\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u000e\u0010 \u001a\u00020\u00102\u0006\u0010!\u001a\u00020\u0017J\u000e\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020$R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/linkguard/app/overlay/OverlayManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "autoDismissRunnable", "Ljava/lang/Runnable;", "layoutParams", "Landroid/view/WindowManager$LayoutParams;", "mainHandler", "Landroid/os/Handler;", "overlayView", "Landroid/view/View;", "windowManager", "Landroid/view/WindowManager;", "applyVerdictStyle", "", "view", "titleRes", "", "iconRes", "reasons", "", "", "cardBgColorRes", "dismiss", "dismissInternal", "isShowing", "", "scheduleAutoDismiss", "delayMs", "", "showScanning", "url", "updateVerdict", "verdict", "Lcom/linkguard/app/model/ScanVerdict;", "app_debug"})
public final class OverlayManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager windowManager = null;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    @org.jetbrains.annotations.Nullable()
    private android.view.View overlayView;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Runnable autoDismissRunnable;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager.LayoutParams layoutParams = null;
    
    public OverlayManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * Shows the overlay immediately in "scanning" state.
     * Call this as soon as a URL is detected — before any cloud results.
     */
    public final void showScanning(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    /**
     * Updates the existing overlay with a scan result.
     * If the overlay was dismissed before the result arrived, this is a no-op.
     */
    public final void updateVerdict(@org.jetbrains.annotations.NotNull()
    com.linkguard.app.model.ScanVerdict verdict) {
    }
    
    /**
     * Public dismiss — safe to call from any thread.
     */
    public final void dismiss() {
    }
    
    /**
     * Internal dismiss — must be called on the main thread.
     */
    private final void dismissInternal() {
    }
    
    public final boolean isShowing() {
        return false;
    }
    
    private final void applyVerdictStyle(android.view.View view, int titleRes, int iconRes, java.util.List<java.lang.String> reasons, int cardBgColorRes) {
    }
    
    private final void scheduleAutoDismiss(long delayMs) {
    }
}