package com.linkguard.app.service;

/**
 * Scans for URLs when the user navigates INTO a chat/conversation within a monitored app.
 *
 * How it works:
 * 1. Detects window-state changes (TYPE_WINDOW_STATE_CHANGED)
 * 2. When a monitored app first comes to the foreground, records the initial screen class
 *    but does NOT scan — avoids scanning the chat list / inbox view.
 * 3. When the user navigates to a different screen WITHIN the same app (e.g., opens a
 *    specific chat), the class name changes → scan is triggered.
 * 4. Waits 800ms for the UI to fully render, then walks the accessibility tree.
 * 5. Extracts URLs from visible text and scans each one.
 * 6. Shows overlay only for SUSPICIOUS / DANGEROUS results (SAFE logged silently).
 *
 * Requires: Accessibility permission (Settings → Accessibility → LinkGuard)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\n\u0010\u0013\u001a\u00060\u0014j\u0002`\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u001e\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u0006H\u0082@\u00a2\u0006\u0002\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020\u0010H\u0016J\b\u0010 \u001a\u00020\u0010H\u0016J\b\u0010!\u001a\u00020\u0010H\u0014J\u0016\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020\u0006H\u0082@\u00a2\u0006\u0002\u0010$R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00040\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/linkguard/app/service/LinkGuardAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "SAME_SCREEN_COOLDOWN_MS", "", "TAG", "", "currentForegroundPackage", "lastScanByPackage", "", "lastSeenClassByPackage", "overlayManager", "Lcom/linkguard/app/overlay/OverlayManager;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "collectText", "", "node", "Landroid/view/accessibility/AccessibilityNodeInfo;", "sb", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "depth", "", "handleUrl", "url", "sourcePackage", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onDestroy", "onInterrupt", "onServiceConnected", "scanVisibleContent", "packageName", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class LinkGuardAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "LinkGuardA11y";
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private com.linkguard.app.overlay.OverlayManager overlayManager;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentForegroundPackage;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> lastSeenClassByPackage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Long> lastScanByPackage = null;
    private final long SAME_SCREEN_COOLDOWN_MS = 15000L;
    
    public LinkGuardAccessibilityService() {
        super();
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    private final java.lang.Object scanVisibleContent(java.lang.String packageName, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Recursively collects visible text from the accessibility tree.
     * Limits: max depth 15, max 8000 characters total.
     */
    @kotlin.Suppress(names = {"DEPRECATION"})
    private final void collectText(android.view.accessibility.AccessibilityNodeInfo node, java.lang.StringBuilder sb, int depth) {
    }
    
    private final java.lang.Object handleUrl(java.lang.String url, java.lang.String sourcePackage, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
}