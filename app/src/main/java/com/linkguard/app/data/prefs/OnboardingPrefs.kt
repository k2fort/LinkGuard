package com.linkguard.app.data.prefs

import android.content.Context

object OnboardingPrefs {
    private const val PREFS = "onboarding_prefs"
    private const val KEY_COMPLETED = "completed"

    fun isCompleted(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_COMPLETED, false)

    fun setCompleted(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_COMPLETED, true).apply()
    }
}
