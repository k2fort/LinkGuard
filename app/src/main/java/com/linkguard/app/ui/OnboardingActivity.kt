package com.linkguard.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.linkguard.app.R
import com.linkguard.app.data.prefs.OnboardingPrefs
import com.linkguard.app.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var currentStep = 0

    private val dots by lazy {
        listOf(binding.dot0, binding.dot1, binding.dot2, binding.dot3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showStep(0)

        binding.btnNext.setOnClickListener {
            if (currentStep < TOTAL_STEPS - 1) {
                showStep(currentStep + 1)
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.btnGrantPermission.setOnClickListener {
            when (currentStep) {
                STEP_NOTIFICATION ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                STEP_OVERLAY ->
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh permission status when returning from system settings
        showStep(currentStep)
    }

    private fun showStep(step: Int) {
        currentStep = step
        updateDots()

        when (step) {
            STEP_WELCOME -> {
                binding.ivPage.setImageResource(R.mipmap.ic_launcher)
                binding.tvTitle.text = getString(R.string.onboarding_welcome_title)
                binding.tvDescription.text = getString(R.string.onboarding_welcome_desc)
                binding.btnGrantPermission.visibility = View.GONE
                binding.tvPermissionGranted.visibility = View.GONE
                binding.tvSkip.visibility = View.VISIBLE
                binding.btnNext.text = getString(R.string.onboarding_next)
            }

            STEP_NOTIFICATION -> {
                val granted = isNotificationAccessGranted()
                binding.ivPage.setImageResource(R.drawable.ic_notifications)
                binding.tvTitle.text = getString(R.string.onboarding_notification_title)
                binding.tvDescription.text = getString(R.string.onboarding_notification_desc)
                binding.btnGrantPermission.text = getString(R.string.onboarding_btn_grant_notification)
                binding.btnGrantPermission.visibility = if (granted) View.GONE else View.VISIBLE
                binding.tvPermissionGranted.visibility = if (granted) View.VISIBLE else View.GONE
                binding.tvSkip.visibility = View.VISIBLE
                binding.btnNext.text = getString(R.string.onboarding_next)
            }

            STEP_OVERLAY -> {
                val granted = Settings.canDrawOverlays(this)
                binding.ivPage.setImageResource(R.drawable.ic_shield_check)
                binding.tvTitle.text = getString(R.string.onboarding_overlay_title)
                binding.tvDescription.text = getString(R.string.onboarding_overlay_desc)
                binding.btnGrantPermission.text = getString(R.string.onboarding_btn_grant_overlay)
                binding.btnGrantPermission.visibility = if (granted) View.GONE else View.VISIBLE
                binding.tvPermissionGranted.visibility = if (granted) View.VISIBLE else View.GONE
                binding.tvSkip.visibility = View.VISIBLE
                binding.btnNext.text = getString(R.string.onboarding_next)
            }

            STEP_DONE -> {
                binding.ivPage.setImageResource(R.drawable.ic_check_circle)
                binding.tvTitle.text = getString(R.string.onboarding_done_title)
                binding.tvDescription.text = getString(R.string.onboarding_done_desc)
                binding.btnGrantPermission.visibility = View.GONE
                binding.tvPermissionGranted.visibility = View.GONE
                binding.tvSkip.visibility = View.GONE
                binding.btnNext.text = getString(R.string.onboarding_lets_go)
            }
        }
    }

    private fun updateDots() {
        dots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                if (index == currentStep) R.drawable.bg_dot_active
                else R.drawable.bg_dot_inactive
            )
        }
    }

    private fun finishOnboarding() {
        OnboardingPrefs.setCompleted(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun isNotificationAccessGranted(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, "enabled_notification_listeners"
        ) ?: return false
        return enabled.contains(packageName)
    }

    companion object {
        private const val TOTAL_STEPS = 4
        private const val STEP_WELCOME = 0
        private const val STEP_NOTIFICATION = 1
        private const val STEP_OVERLAY = 2
        private const val STEP_DONE = 3
    }
}
