package com.example.seekandlog.activities

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seekandlog.R
import com.example.seekandlog.adapters.AppLogsAdapter
import com.example.seekandlog.adapters.MonitoredAppsAdapter
import com.example.seekandlog.databinding.AppMonitorActivityBinding
import com.example.seekandlog.objs.SelectableAppInfo
import com.example.seekandlog.objs.SelectableAppsWrapper
import com.example.seekandlog.viewmodels.MonitorViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch


class AppMonitorActivity : AppCompatActivity() {

    companion object {
        const val SELECTABLE_APPS_TAG = "selectableApps"

        fun buildIntent(context: Context, apps: List<SelectableAppInfo>?) =
            Intent(context, AppMonitorActivity::class.java).apply {
                apps?.let {
                    val bundle = Bundle()
                    bundle.putParcelable(SELECTABLE_APPS_TAG, SelectableAppsWrapper(apps))
                    putExtras(bundle)
                }
            }
    }

    private val binding: AppMonitorActivityBinding
            by lazy { AppMonitorActivityBinding.inflate(layoutInflater) }
    private val logsAdapter = AppLogsAdapter()
    private val monitoredAppsAdapter = MonitoredAppsAdapter()

    private val viewModel: MonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.monitor_activity_title)
        setContentView(binding.root)

        (intent.extras?.getParcelable(SELECTABLE_APPS_TAG) as? SelectableAppsWrapper)
            ?.apps?.let {
                viewModel.setSelectedApps(it)
            }

        binding.rvMonitoredApps.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMonitoredApps.adapter = monitoredAppsAdapter

        binding.rvLogs.layoutManager = LinearLayoutManager(this)
        binding.rvLogs.addItemDecoration(MaterialDividerItemDecoration(this, LinearLayout.VERTICAL))
        binding.rvLogs.adapter = logsAdapter

        viewModel.monitoredApps.observe(this) {
            monitoredAppsAdapter.monitoredApps = it
        }

        viewModel.stopMonitoring.observe(this) { stopped ->
            if (stopped) {
                binding.stopStartBtn.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.play)
                )
                Toast.makeText(this, R.string.monitor_stopped, Toast.LENGTH_SHORT).show()
            } else {
                binding.stopStartBtn.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pause)
                )
                Toast.makeText(this, R.string.monitor_started, Toast.LENGTH_SHORT).show()
                startMonitor()
            }
        }

        binding.stopStartBtn.setOnClickListener { viewModel.toggleMonitoring() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastLogs.collect {
                    logsAdapter.logs = it.toMutableList()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateLogs()
    }

    private fun startMonitor() {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as? UsageStatsManager
        usageStatsManager?.let {
            viewModel.startLogging(it)
        } ?: run {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.usage_stats_service_error)
                .show()
        }
    }
}