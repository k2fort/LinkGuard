package com.linkguard.app.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.linkguard.app.R
import com.linkguard.app.data.prefs.ScanHistoryPrefs
import com.linkguard.app.data.prefs.WhitelistPrefs
import com.linkguard.app.databinding.ActivityHistoryBinding
import com.linkguard.app.databinding.ItemHistoryBinding
import com.linkguard.app.model.VerdictLevel
import com.linkguard.app.util.UrlExtractor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        loadHistory()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_CLEAR, Menu.NONE, getString(R.string.history_clear))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == MENU_CLEAR) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.history_clear))
                .setMessage(getString(R.string.history_clear_confirm))
                .setPositiveButton(getString(R.string.history_clear_yes)) { _, _ ->
                    ScanHistoryPrefs.clearHistory(this)
                    loadHistory()
                }
                .setNegativeButton(getString(R.string.history_clear_no), null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadHistory() {
        val raw = ScanHistoryPrefs.getHistory(this)
        if (raw.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            return
        }
        binding.tvEmpty.visibility = View.GONE
        binding.rvHistory.visibility = View.VISIBLE
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = HistoryAdapter(groupEntries(raw))
    }

    /**
     * Groups entries by URL (same link scanned multiple times → one card).
     * Within each group: keep the most severe verdict, tie-break by latest timestamp.
     */
    private fun groupEntries(
        items: List<ScanHistoryPrefs.HistoryEntry>
    ): List<Pair<ScanHistoryPrefs.HistoryEntry, Int>> {
        return items
            .groupBy { it.url }
            .values
            .map { group ->
                val best = group.maxWith(
                    compareByDescending<ScanHistoryPrefs.HistoryEntry> { it.level.ordinal }
                        .thenByDescending { it.timestamp }
                )
                Pair(best, group.size)
            }
            .sortedByDescending { it.first.timestamp }
    }

    private fun appLabel(packageName: String): String = try {
        val info = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    }

    // ── Adapter ────────────────────────────────────────────────────────────

    private inner class HistoryAdapter(
        private val items: List<Pair<ScanHistoryPrefs.HistoryEntry, Int>>
    ) : RecyclerView.Adapter<HistoryAdapter.VH>() {

        private val expandedPositions = mutableSetOf<Int>()

        inner class VH(val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val (entry, count) = items[position]
            val b = holder.b
            val isExpanded = position in expandedPositions

            // ── Icon + colors ──
            val (iconRes, iconColor, labelText, labelColor) = when (entry.level) {
                VerdictLevel.SAFE       -> Quad(R.drawable.ic_shield_check, 0xFF2E7D32.toInt(),
                    getString(R.string.overlay_verdict_safe),       0xFF2E7D32.toInt())
                VerdictLevel.SUSPICIOUS -> Quad(R.drawable.ic_shield_alert, 0xFFF9A825.toInt(),
                    getString(R.string.overlay_verdict_suspicious),  0xFFF9A825.toInt())
                VerdictLevel.DANGEROUS  -> Quad(R.drawable.ic_shield_alert, 0xFFD32F2F.toInt(),
                    getString(R.string.overlay_verdict_dangerous),   0xFFD32F2F.toInt())
                VerdictLevel.SCANNING   -> Quad(R.drawable.ic_warning,      0xFF888888.toInt(),
                    "…",                                             0xFF888888.toInt())
            }

            val drawable = DrawableCompat.wrap(
                resources.getDrawable(iconRes, theme).mutate()
            )
            DrawableCompat.setTint(drawable, iconColor)
            b.ivHistoryIcon.setImageDrawable(drawable)

            b.tvVerdictLabel.text = labelText
            b.tvVerdictLabel.setTextColor(labelColor)

            // ── URL ──
            // Show resolved URL if available (reveals true destination of shortlinks)
            b.tvHistoryUrl.text = entry.resolvedUrl ?: entry.url

            // ── First reason ──
            val sourceApp = appLabel(entry.sourcePackage)
            b.tvHistoryReason.text = when {
                entry.reasons.isNotEmpty() -> entry.reasons.first()
                else -> sourceApp
            }

            // ── Timestamp ──
            val date = Date(entry.timestamp)
            b.tvHistoryTime.text = timeFormat.format(date)
            b.tvHistoryDate.text = dateFormat.format(date)

            // ── Scan count badge ──
            if (count > 1) {
                b.tvScanCount.visibility = View.VISIBLE
                b.tvScanCount.text = "×$count"
            } else {
                b.tvScanCount.visibility = View.GONE
            }

            // ── Expand / collapse ──
            b.tvChevron.text = if (isExpanded) "▴" else "▾"
            b.dividerDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE
            b.layoutDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE

            if (isExpanded) {
                b.tvDetailSource.text = getString(R.string.history_detail_source, sourceApp)

                if (entry.resolvedUrl != null && entry.resolvedUrl != entry.url) {
                    b.tvDetailOriginalUrl.visibility = View.VISIBLE
                    b.tvDetailOriginalUrl.text =
                        getString(R.string.history_detail_original, entry.url)
                } else {
                    b.tvDetailOriginalUrl.visibility = View.GONE
                }

                if (entry.reasons.isNotEmpty()) {
                    b.tvDetailReasons.visibility = View.VISIBLE
                    b.tvDetailReasons.text = entry.reasons.joinToString("\n") { "• $it" }
                } else {
                    b.tvDetailReasons.visibility = View.GONE
                }

                // Trust domain button — extract host from the scanned URL
                val host = UrlExtractor.extractHost(entry.url)
                if (host != null) {
                    b.btnTrustDomain.visibility = View.VISIBLE
                    val alreadyTrusted = WhitelistPrefs.isTrusted(this@HistoryActivity, host)
                    if (alreadyTrusted) {
                        b.btnTrustDomain.text = getString(R.string.history_already_trusted, host)
                        b.btnTrustDomain.isEnabled = false
                    } else {
                        b.btnTrustDomain.text = getString(R.string.overlay_btn_trust_domain, host)
                        b.btnTrustDomain.isEnabled = true
                        b.btnTrustDomain.setOnClickListener {
                            WhitelistPrefs.addDomain(this@HistoryActivity, host)
                            b.btnTrustDomain.text = getString(R.string.history_already_trusted, host)
                            b.btnTrustDomain.isEnabled = false
                            Snackbar.make(
                                binding.root,
                                getString(R.string.history_trusted_toast, host),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    b.btnTrustDomain.visibility = View.GONE
                }
            }

            b.layoutHeader.setOnClickListener {
                @Suppress("DEPRECATION")
                val pos = holder.adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                if (pos in expandedPositions) expandedPositions.remove(pos)
                else expandedPositions.add(pos)
                notifyItemChanged(pos)
            }
        }
    }

    // Simple 4-value tuple to keep the when() expression readable
    private data class Quad(val a: Int, val b: Int, val c: String, val d: Int)

    companion object {
        private const val MENU_CLEAR = 1
    }
}
