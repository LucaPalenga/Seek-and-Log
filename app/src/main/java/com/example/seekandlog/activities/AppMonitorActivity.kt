package com.example.seekandlog.activities

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seekandlog.R
import com.example.seekandlog.adapters.AppLogsAdapter
import com.example.seekandlog.databinding.AppMonitorActivityBinding
import com.example.seekandlog.objs.SelectableAppData
import com.example.seekandlog.objs.SelectableAppsWrapper
import com.example.seekandlog.viewmodels.MonitorViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration


class AppMonitorActivity : AppCompatActivity() {

    companion object {
        const val SELECTABLE_APPS_TAG = "selectableApps"

        fun buildIntent(context: Context, apps: List<SelectableAppData>?) =
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

    private val viewModel: MonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.monitor_activity_title)
        setContentView(binding.root)

        val selectableAppsWrapper =
            intent.extras?.getParcelable(SELECTABLE_APPS_TAG) as? SelectableAppsWrapper
        viewModel.selectedApps = selectableAppsWrapper?.apps

        binding.rvLogs.layoutManager = LinearLayoutManager(this)
        binding.rvLogs.addItemDecoration(MaterialDividerItemDecoration(this, LinearLayout.VERTICAL))
        binding.rvLogs.adapter = logsAdapter

        viewModel.lastLogs.observe(this) {
            logsAdapter.addLogs(it)
        }

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