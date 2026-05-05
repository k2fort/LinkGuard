package com.linkguard.app.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkguard.app.R
import com.linkguard.app.data.prefs.WhitelistPrefs
import com.linkguard.app.databinding.ActivityWhitelistBinding

class WhitelistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhitelistBinding
    private lateinit var adapter: WhitelistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhitelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = WhitelistAdapter(
            domains = mutableListOf(),
            onRemove = { domain ->
                WhitelistPrefs.removeDomain(this, domain)
                refreshList()
            }
        )
        binding.rvWhitelist.layoutManager = LinearLayoutManager(this)
        binding.rvWhitelist.adapter = adapter

        binding.fabAddDomain.setOnClickListener { showAddDomainDialog() }

        refreshList()
    }

    private fun refreshList() {
        val domains = WhitelistPrefs.getTrustedDomains(this).sorted()
        adapter.setDomains(domains)
        binding.tvEmpty.visibility = if (domains.isEmpty()) View.VISIBLE else View.GONE
        binding.rvWhitelist.visibility = if (domains.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showAddDomainDialog() {
        val input = EditText(this).apply {
            hint = getString(R.string.whitelist_add_hint)
            setSingleLine()
            setPadding(48, 24, 48, 24)
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.whitelist_add_title))
            .setView(input)
            .setPositiveButton(getString(R.string.whitelist_add_confirm)) { _, _ ->
                val domain = input.text.toString().trim()
                if (domain.isNotEmpty()) {
                    WhitelistPrefs.addDomain(this, domain)
                    refreshList()
                }
            }
            .setNegativeButton(getString(R.string.history_clear_no), null)
            .show()
    }

    // ── Adapter ────────────────────────────────────────────────────────────────

    private inner class WhitelistAdapter(
        private val domains: MutableList<String>,
        private val onRemove: (String) -> Unit
    ) : RecyclerView.Adapter<WhitelistAdapter.ViewHolder>() {

        fun setDomains(newDomains: List<String>) {
            domains.clear()
            domains.addAll(newDomains)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_whitelist, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val domain = domains[position]
            holder.tvDomain.text = domain
            holder.btnRemove.setOnClickListener {
                AlertDialog.Builder(this@WhitelistActivity)
                    .setMessage(getString(R.string.whitelist_remove_confirm, domain))
                    .setPositiveButton(getString(R.string.whitelist_remove_yes)) { _, _ ->
                        onRemove(domain)
                    }
                    .setNegativeButton(getString(R.string.history_clear_no), null)
                    .show()
            }
        }

        override fun getItemCount() = domains.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvDomain: TextView = view.findViewById(R.id.tvDomain)
            val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
        }
    }
}
