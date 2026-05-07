package com.linkguard.app.data.prefs

import android.content.Context

/**
 * Stores which app packages the user has chosen to monitor.
 * Uses SharedPreferences — lightweight enough for a set of package names.
 */
object MonitoredAppsPrefs {

    private const val PREFS_NAME = "monitored_apps"
    private const val KEY_PACKAGES = "packages"

    /**
     * Returns the set of package names to monitor.
     * Empty set = user has not selected any apps yet.
     */
    fun getMonitoredPackages(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_PACKAGES, emptySet()) ?: emptySet()
    }

    fun setMonitoredPackages(context: Context, packages: Set<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_PACKAGES, packages)
            .apply()
    }

    fun addPackage(context: Context, packageName: String) {
        val current = getMonitoredPackages(context).toMutableSet()
        current.add(packageName)
        setMonitoredPackages(context, current)
    }

    fun removePackage(context: Context, packageName: String) {
        val current = getMonitoredPackages(context).toMutableSet()
        current.remove(packageName)
        setMonitoredPackages(context, current)
    }

    fun isMonitored(context: Context, packageName: String): Boolean {
        return packageName in getMonitoredPackages(context)
    }

    fun registerListener(
        context: Context,
        listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(
        context: Context,
        listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(listener)
    }
}
