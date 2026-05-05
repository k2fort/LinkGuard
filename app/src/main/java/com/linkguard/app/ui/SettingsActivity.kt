package com.linkguard.app.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.linkguard.app.R
import com.linkguard.app.data.prefs.BlocklistUpdatePrefs
import com.linkguard.app.data.prefs.LanguagePrefs
import com.linkguard.app.databinding.ActivitySettingsBinding
import com.linkguard.app.worker.BlocklistUpdateWorker
import java.text.DateFormat
import java.util.Date

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        updateLanguageButtonStates()
        updateBlocklistStatus()

        binding.btnLangHebrew.setOnClickListener { setLanguage(LanguagePrefs.HEBREW) }
        binding.btnLangEnglish.setOnClickListener { setLanguage(LanguagePrefs.ENGLISH) }

        binding.btnRevokeNotificationAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        binding.btnRevokeOverlay.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:$packageName")
                )
            )
        }
        binding.btnRevokeAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnManageWhitelist.setOnClickListener {
            startActivity(Intent(this, WhitelistActivity::class.java))
        }

        binding.btnUpdateBlocklist.setOnClickListener {
            triggerManualUpdate()
        }
    }

    private fun updateLanguageButtonStates() {
        val isHebrew = LanguagePrefs.getLanguage(this) == LanguagePrefs.HEBREW
        binding.btnLangHebrew.isEnabled = !isHebrew
        binding.btnLangEnglish.isEnabled = isHebrew
    }

    private fun updateBlocklistStatus() {
        val lastUpdated = BlocklistUpdatePrefs.getLastUpdated(this)
        val count = BlocklistUpdatePrefs.getLastCount(this)

        binding.tvBlocklistStatus.text = if (lastUpdated == 0L) {
            getString(R.string.settings_blocklist_never)
        } else {
            val dateStr = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(Date(lastUpdated))
            getString(R.string.settings_blocklist_last_updated, dateStr, count)
        }
    }

    private fun triggerManualUpdate() {
        binding.btnUpdateBlocklist.isEnabled = false
        binding.btnUpdateBlocklist.text = getString(R.string.settings_blocklist_updating)

        val request = OneTimeWorkRequestBuilder<BlocklistUpdateWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(MANUAL_UPDATE_TAG)
            .build()

        val wm = WorkManager.getInstance(this)
        wm.enqueue(request)

        wm.getWorkInfoByIdLiveData(request.id).observe(this) { info ->
            if (info == null) return@observe
            when (info.state) {
                WorkInfo.State.SUCCEEDED -> {
                    updateBlocklistStatus()
                    binding.btnUpdateBlocklist.isEnabled = true
                    binding.btnUpdateBlocklist.text = getString(R.string.settings_update_blocklist_now)
                }
                WorkInfo.State.FAILED -> {
                    binding.btnUpdateBlocklist.isEnabled = true
                    binding.btnUpdateBlocklist.text = getString(R.string.settings_update_blocklist_now)
                    binding.tvBlocklistStatus.text = getString(R.string.settings_blocklist_update_failed)
                }
                else -> { /* RUNNING / ENQUEUED — keep button disabled */ }
            }
        }
    }

    private fun setLanguage(lang: String) {
        if (LanguagePrefs.getLanguage(this) == lang) return
        LanguagePrefs.setLanguage(this, lang)
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    companion object {
        private const val MANUAL_UPDATE_TAG = "manual_blocklist_update"
    }
}
