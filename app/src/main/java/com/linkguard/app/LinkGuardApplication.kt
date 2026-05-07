package com.linkguard.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.linkguard.app.data.db.AppDatabase
import com.linkguard.app.data.db.LocalBlocklistRepo
import com.linkguard.app.data.prefs.LanguagePrefs
import com.linkguard.app.worker.BlocklistUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class LinkGuardApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun attachBaseContext(base: Context) {
        val lang = LanguagePrefs.getLanguage(base)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(base.createConfigurationContext(config))
    }

    override fun onCreate() {
        super.onCreate()
        seedBlocklistAsync()
        scheduleBlocklistUpdates()
    }

    private fun seedBlocklistAsync() {
        appScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).blocklistDao()
            LocalBlocklistRepo(dao).seedIfEmpty()
        }
    }

    private fun scheduleBlocklistUpdates() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<BlocklistUpdateWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // UPDATE instead of KEEP: if the interval or constraints change in a future
        // release, KEEP would leave existing users running the old work forever until
        // they clear app data. UPDATE replaces stale enqueued work automatically.
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            BlocklistUpdateWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
