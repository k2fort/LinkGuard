package com.linkguard.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.linkguard.app.R
import com.linkguard.app.data.prefs.WhitelistPrefs
import com.linkguard.app.model.ScanVerdict
import com.linkguard.app.model.VerdictLevel
import com.linkguard.app.util.UrlExtractor

/**
 * Manages the system overlay window shown when a URL is detected in a notification.
 *
 * Lifecycle:
 * 1. showScanning(url) — display immediately with spinner
 * 2. updateVerdict(verdict) — called when local/cloud scan completes
 * 3. dismiss() — called by user action or auto-dismiss for SAFE verdicts
 *
 * Must be called from the main thread.
 */
class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private var overlayView: View? = null
    private var autoDismissRunnable: Runnable? = null

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        // Convert 80dp → px so the overlay clears the status bar on all screen densities.
        // Raw pixel value (48) was too small on xxhdpi/xxxhdpi devices where the status bar
        // is 72–96px tall.
        y = (80 * context.resources.displayMetrics.density).toInt()
    }

    /**
     * Shows the overlay immediately in "scanning" state.
     * Call this as soon as a URL is detected — before any cloud results.
     */
    fun showScanning(url: String) {
        mainHandler.post {
            dismissInternal() // called directly — we're already on the main thread

            try {
            // ContextThemeWrapper is required — applicationContext has no theme,
            // so Material theme attributes (?attr/colorOnSurface etc.) would crash inflation
            val themedContext = ContextThemeWrapper(context, R.style.Theme_LinkGuard)
            val inflater = LayoutInflater.from(themedContext)
            val view = inflater.inflate(R.layout.overlay_verdict, null)

            view.findViewById<TextView>(R.id.tvUrl).text = url
            view.findViewById<TextView>(R.id.tvVerdictTitle).text =
                context.getString(R.string.overlay_scanning)
            view.findViewById<LinearLayout>(R.id.layoutScanning).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.layoutVerdictReason).visibility = View.GONE
            view.findViewById<ImageView>(R.id.ivVerdictIcon).setImageResource(R.drawable.ic_warning)

            // Proceed button is disabled while scanning
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnProceed).apply {
                isEnabled = false
                alpha = 0.5f
            }

            view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener { dismiss() }
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBlock)
                .setOnClickListener { dismiss() }

            windowManager.addView(view, layoutParams)
            overlayView = view
            Log.d("OverlayManager", "Overlay shown for: $url")
            } catch (e: Exception) {
                Log.e("OverlayManager", "Failed to show overlay: ${e.message}", e)
            }
        }
    }

    /**
     * Updates the existing overlay with a scan result.
     * If the overlay was dismissed before the result arrived, this is a no-op.
     */
    fun updateVerdict(verdict: ScanVerdict) {
        mainHandler.post {
            val view = overlayView ?: return@post

            // Update URL display (may now show resolved URL)
            view.findViewById<TextView>(R.id.tvUrl).text = verdict.url

            // Show resolved URL row if the link was a shortlink
            if (verdict.resolvedUrl != null && verdict.resolvedUrl != verdict.url) {
                view.findViewById<LinearLayout>(R.id.layoutResolvedUrl).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvResolvedUrl).text = verdict.resolvedUrl
            }

            // Hide spinner
            view.findViewById<LinearLayout>(R.id.layoutScanning).visibility = View.GONE

            // Enable proceed button
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnProceed).apply {
                isEnabled = true
                alpha = 1.0f
            }

            when (verdict.level) {
                VerdictLevel.SCANNING -> {
                    // Still scanning — keep spinner (shouldn't normally be called)
                    view.findViewById<LinearLayout>(R.id.layoutScanning).visibility = View.VISIBLE
                }

                VerdictLevel.SAFE -> {
                    applyVerdictStyle(
                        view = view,
                        titleRes = R.string.overlay_verdict_safe,
                        iconRes = R.drawable.ic_shield_check,
                        reasons = verdict.reasons,
                        cardBgColorRes = R.color.safe_green_bg
                    )
                    // Safe links auto-dismiss after 3 seconds
                    scheduleAutoDismiss(3000)
                }

                VerdictLevel.SUSPICIOUS -> {
                    applyVerdictStyle(
                        view = view,
                        titleRes = R.string.overlay_verdict_suspicious,
                        iconRes = R.drawable.ic_warning,
                        reasons = verdict.reasons,
                        cardBgColorRes = R.color.warning_orange_bg
                    )
                }

                VerdictLevel.DANGEROUS -> {
                    applyVerdictStyle(
                        view = view,
                        titleRes = R.string.overlay_verdict_dangerous,
                        iconRes = R.drawable.ic_shield_alert,
                        reasons = verdict.reasons,
                        cardBgColorRes = R.color.danger_red_bg
                    )
                    // For dangerous links, change "Proceed" label to warn the user
                    view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnProceed)
                        .text = context.getString(R.string.overlay_btn_proceed_anyway)
                }
            }

            // Wire proceed button to dismiss (user chose to proceed)
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnProceed)
                .setOnClickListener { dismiss() }

            // Show "Trust domain" button once verdict is known.
            // Hidden for DANGEROUS — a panicked user could accidentally whitelist
            // a confirmed phishing domain while reading the warning.
            val host = UrlExtractor.extractHost(verdict.url)
            if (host != null && verdict.level != VerdictLevel.DANGEROUS) {
                val btnTrust = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnTrustDomain)
                btnTrust.visibility = View.VISIBLE
                btnTrust.text = context.getString(R.string.overlay_btn_trust_domain, host)
                btnTrust.setOnClickListener {
                    WhitelistPrefs.addDomain(context, host)
                    dismiss()
                }
            }
        }
    }

    /** Public dismiss — safe to call from any thread. */
    fun dismiss() {
        mainHandler.post { dismissInternal() }
    }

    /** Internal dismiss — must be called on the main thread. */
    private fun dismissInternal() {
        autoDismissRunnable?.let { mainHandler.removeCallbacks(it) }
        autoDismissRunnable = null
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
                // View may have already been removed
            }
            overlayView = null
        }
    }

    fun isShowing(): Boolean = overlayView != null

    private fun applyVerdictStyle(
        view: View,
        titleRes: Int,
        iconRes: Int,
        reasons: List<String>,
        cardBgColorRes: Int
    ) {
        view.findViewById<TextView>(R.id.tvVerdictTitle).text = context.getString(titleRes)
        view.findViewById<ImageView>(R.id.ivVerdictIcon).setImageResource(iconRes)
        view.findViewById<CardView>(R.id.cardRoot)
            .setCardBackgroundColor(ContextCompat.getColor(context, cardBgColorRes))

        if (reasons.isNotEmpty()) {
            view.findViewById<LinearLayout>(R.id.layoutVerdictReason).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tvReasons).text = reasons.joinToString("\n• ", "• ")
        }
    }

    private fun scheduleAutoDismiss(delayMs: Long) {
        autoDismissRunnable?.let { mainHandler.removeCallbacks(it) }
        val runnable = Runnable { dismiss() }
        autoDismissRunnable = runnable
        mainHandler.postDelayed(runnable, delayMs)
    }
}
