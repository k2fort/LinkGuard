package com.linkguard.app.ui

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linkguard.app.data.prefs.MonitoredAppsPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: android.graphics.drawable.Drawable?,
    val isMonitored: Boolean
)

class AppSelectorViewModel(application: Application) : AndroidViewModel(application) {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val appList = withContext(Dispatchers.IO) {
                val pm = getApplication<Application>().packageManager
                val monitoredPackages = MonitoredAppsPrefs.getMonitoredPackages(getApplication())

                // Collect all packages that have a launcher icon (user-facing apps).
                // This includes system messaging apps (Samsung Messages, Google Messages)
                // while excluding invisible background services.
                val launcherPackages = pm.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.MATCH_ALL
                ).map { it.activityInfo.packageName }.toSet()

                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { it.packageName in launcherPackages }
                    .filter { it.packageName != "com.linkguard.app" }
                    .map { info ->
                        AppInfo(
                            packageName = info.packageName,
                            appName = pm.getApplicationLabel(info).toString(),
                            icon = pm.getApplicationIcon(info.packageName),
                            isMonitored = info.packageName in monitoredPackages
                        )
                    }
                    .sortedBy { it.appName.lowercase() }
            }
            _apps.value = appList
            _isLoading.value = false
        }
    }

    fun toggleMonitoring(packageName: String) {
        val context = getApplication<Application>()
        val isCurrentlyMonitored = MonitoredAppsPrefs.isMonitored(context, packageName)

        if (isCurrentlyMonitored) {
            MonitoredAppsPrefs.removePackage(context, packageName)
        } else {
            MonitoredAppsPrefs.addPackage(context, packageName)
        }

        // Update local state immediately (no need to reload all apps)
        _apps.value = _apps.value.map { app ->
            if (app.packageName == packageName) app.copy(isMonitored = !isCurrentlyMonitored)
            else app
        }
    }
}
