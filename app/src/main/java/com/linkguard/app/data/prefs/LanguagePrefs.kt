package com.linkguard.app.data.prefs

import android.content.Context

object LanguagePrefs {
    private const val PREFS = "language_prefs"
    private const val KEY   = "language"
    const val HEBREW  = "iw"
    const val ENGLISH = "en"

    fun getLanguage(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, HEBREW) ?: HEBREW

    fun setLanguage(context: Context, lang: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, lang).apply()
    }
}
