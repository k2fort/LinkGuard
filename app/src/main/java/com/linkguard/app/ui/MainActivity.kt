package com.linkguard.app.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.linkguard.app.R
import com.linkguard.app.data.prefs.OnboardingPrefs
import com.linkguard.app.databinding.ActivityMainBinding
import com.linkguard.app.service.KeepAliveService
import com.linkguard.app.service.LinkGuardAccessibilityService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OnboardingPrefs.isCompleted(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGrantNotificationAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        binding.btnGrantOverlayAccess.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:$packageName")
                )
            )
        }

        binding.btnGrantNotifyPerm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQ_NOTIFY_PERM
                )
            }
        }

        binding.btnDisableBatteryOpt.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
            )
        }

        binding.btnSelectApps.setOnClickListener {
            startActivity(Intent(this, AppSelectorActivity::class.java))
        }

        binding.btnGrantAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_NOTIFY_PERM) updatePermissionStatus()
    }

    private fun updatePermissionStatus() {
        val notificationGranted = isNotificationAccessGranted()
        val overlayGranted = Settings.canDrawOverlays(this)
        val notifyPermGranted = isNotifyPermGranted()
        val batteryOk = isBatteryOptimizationDisabled()
        val accessibilityGranted = isAccessibilityGranted()

        binding.ivNotificationStatus.setImageResource(
            if (notificationGranted) R.drawable.ic_check_circle else R.drawable.ic_warning
        )
        binding.btnGrantNotificationAccess.visibility =
            if (notificationGranted) View.GONE else View.VISIBLE

        binding.ivOverlayStatus.setImageResource(
            if (overlayGranted) R.drawable.ic_check_circle else R.drawable.ic_warning
        )
        binding.btnGrantOverlayAccess.visibility =
            if (overlayGranted) View.GONE else View.VISIBLE

        binding.ivNotifyPermStatus.setImageResource(
            if (notifyPermGranted) R.drawable.ic_check_circle else R.drawable.ic_warning
        )
        binding.btnGrantNotifyPerm.visibility =
            if (notifyPermGranted) View.GONE else View.VISIBLE

        binding.ivBatteryStatus.setImageResource(
            if (batteryOk) R.drawable.ic_check_circle else R.drawable.ic_warning
        )
        binding.btnDisableBatteryOpt.visibility =
            if (batteryOk) View.GONE else View.VISIBLE

        binding.ivAccessibilityStatus.setImageResource(
            if (accessibilityGranted) R.drawable.ic_check_circle else R.drawable.ic_warning
        )
        binding.btnGrantAccessibility.visibility =
            if (accessibilityGranted) View.GONE else View.VISIBLE

        val allGranted = notificationGranted && overlayGranted && notifyPermGranted
        binding.btnSelectApps.visibility = if (allGranted) View.VISIBLE else View.GONE

        if (allGranted) {
            val svc = Intent(this, KeepAliveService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(svc)
            else startService(svc)
        }

        binding.tvStatusSummary.text = when {
            allGranted && batteryOk -> getString(R.string.status_active)
            else -> getString(R.string.status_permissions_needed)
        }
    }

    private fun isNotifyPermGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isBatteryOptimizationDisabled(): Boolean {
        val pm = getSystemService(PowerManager::class.java)
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun isAccessibilityGranted(): Boolean {
        val expectedComponent = ComponentName(this, LinkGuardAccessibilityService::class.java)
            .flattenToString()
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(":").any { it.equals(expectedComponent, ignoreCase = true) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQ_NOTIFY_PERM = 1001
    }

    private fun isNotificationAccessGranted(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabledListeners.contains(packageName)
    }
}
