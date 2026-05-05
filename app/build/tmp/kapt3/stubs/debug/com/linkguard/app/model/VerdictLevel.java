package com.linkguard.app.model;

/**
 * Represents the result of scanning a URL.
 *
 * Flow:
 * SCANNING → shown immediately while local + cloud checks run
 * SAFE     → all checks passed, overlay auto-dismisses after a short delay
 * SUSPICIOUS → at least one signal is concerning, user must choose to proceed or block
 * DANGEROUS  → known threat confirmed, block is the default action
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/linkguard/app/model/VerdictLevel;", "", "(Ljava/lang/String;I)V", "SCANNING", "SAFE", "SUSPICIOUS", "DANGEROUS", "app_debug"})
public enum VerdictLevel {
    /*public static final*/ SCANNING /* = new SCANNING() */,
    /*public static final*/ SAFE /* = new SAFE() */,
    /*public static final*/ SUSPICIOUS /* = new SUSPICIOUS() */,
    /*public static final*/ DANGEROUS /* = new DANGEROUS() */;
    
    VerdictLevel() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.linkguard.app.model.VerdictLevel> getEntries() {
        return null;
    }
}