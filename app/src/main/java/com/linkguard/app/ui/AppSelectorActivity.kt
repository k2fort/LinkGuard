package com.linkguard.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkguard.app.R
import com.linkguard.app.databinding.ActivityAppSelectorBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppSelectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSelectorBinding
    private val viewModel: AppSelectorViewModel by viewModels()
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_app_selector)

        adapter = AppListAdapter { packageName -> viewModel.toggleMonitoring(packageName) }

        binding.recyclerViewApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewApps.adapter = adapter

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.recyclerViewApps.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.apps.collectLatest { apps ->
                adapter.submitList(apps)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

private class AppListAdapter(
    private val onToggle: (String) -> Unit
) : ListAdapter<AppInfo, AppListAdapter.AppViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view, onToggle)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AppViewHolder(
        itemView: View,
        private val onToggle: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val name: TextView = itemView.findViewById(R.id.tvAppName)
        private val packageNameView: TextView = itemView.findViewById(R.id.tvPackageName)
        private val checkbox: CheckBox = itemView.findViewById(R.id.cbMonitor)

        fun bind(app: AppInfo) {
            icon.setImageDrawable(app.icon)
            name.text = app.appName
            packageNameView.text = app.packageName
            checkbox.isChecked = app.isMonitored

            // Toggle on row click or checkbox click
            itemView.setOnClickListener { onToggle(app.packageName) }
            checkbox.setOnClickListener { onToggle(app.packageName) }
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }
}
