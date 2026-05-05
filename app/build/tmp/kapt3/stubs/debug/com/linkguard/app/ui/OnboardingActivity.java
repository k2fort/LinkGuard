package com.linkguard.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0012\u0010\u0012\u001a\u00020\u000f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\b\u0010\u0015\u001a\u00020\u000fH\u0014J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0006H\u0002J\b\u0010\u0018\u001a\u00020\u000fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R!\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u001a"}, d2 = {"Lcom/linkguard/app/ui/OnboardingActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/linkguard/app/databinding/ActivityOnboardingBinding;", "currentStep", "", "dots", "", "Landroid/view/View;", "getDots", "()Ljava/util/List;", "dots$delegate", "Lkotlin/Lazy;", "finishOnboarding", "", "isNotificationAccessGranted", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "showStep", "step", "updateDots", "Companion", "app_debug"})
public final class OnboardingActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.linkguard.app.databinding.ActivityOnboardingBinding binding;
    private int currentStep = 0;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy dots$delegate = null;
    private static final int TOTAL_STEPS = 4;
    private static final int STEP_WELCOME = 0;
    private static final int STEP_NOTIFICATION = 1;
    private static final int STEP_OVERLAY = 2;
    private static final int STEP_DONE = 3;
    @org.jetbrains.annotations.NotNull()
    public static final com.linkguard.app.ui.OnboardingActivity.Companion Companion = null;
    
    public OnboardingActivity() {
        super();
    }
    
    private final java.util.List<android.view.View> getDots() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    private final void showStep(int step) {
    }
    
    private final void updateDots() {
    }
    
    private final void finishOnboarding() {
    }
    
    private final boolean isNotificationAccessGranted() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/linkguard/app/ui/OnboardingActivity$Companion;", "", "()V", "STEP_DONE", "", "STEP_NOTIFICATION", "STEP_OVERLAY", "STEP_WELCOME", "TOTAL_STEPS", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}