package com.linkguard.app.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u0004H\u0002J\u001e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0004H\u0082@\u00a2\u0006\u0002\u0010\u0012J\b\u0010\u0013\u001a\u00020\u000fH\u0016J\b\u0010\u0014\u001a\u00020\u000fH\u0016J\u0010\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0017H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/linkguard/app/service/LinkGuardNotificationService;", "Landroid/service/notification/NotificationListenerService;", "()V", "TAG", "", "overlayManager", "Lcom/linkguard/app/overlay/OverlayManager;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "getMessageBundles", "", "Landroid/os/Bundle;", "extras", "key", "handleDetectedUrl", "", "url", "sourcePackage", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onCreate", "onDestroy", "onNotificationPosted", "sbn", "Landroid/service/notification/StatusBarNotification;", "onNotificationRemoved", "app_debug"})
public final class LinkGuardNotificationService extends android.service.notification.NotificationListenerService {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "LinkGuardService";
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private com.linkguard.app.overlay.OverlayManager overlayManager;
    
    public LinkGuardNotificationService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public void onNotificationPosted(@org.jetbrains.annotations.NotNull()
    android.service.notification.StatusBarNotification sbn) {
    }
    
    @java.lang.Override()
    public void onNotificationRemoved(@org.jetbrains.annotations.NotNull()
    android.service.notification.StatusBarNotification sbn) {
    }
    
    /**
     * Reads a Parcelable array of Bundles from notification extras.
     *
     * On API 33+, Bundle.getParcelableArray(String) without a class argument is deprecated
     * and returns null when the target app was compiled with the new API.  The typed overload
     * must be used instead.
     */
    private final java.util.List<android.os.Bundle> getMessageBundles(android.os.Bundle extras, java.lang.String key) {
        return null;
    }
    
    private final java.lang.Object handleDetectedUrl(java.lang.String url, java.lang.String sourcePackage, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}