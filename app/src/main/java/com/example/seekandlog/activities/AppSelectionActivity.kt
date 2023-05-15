package com.example.seekandlog.activities

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process.myUid
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seekandlog.R
import com.example.seekandlog.adapters.SelectableAppsAdapter
import com.example.seekandlog.databinding.AppSelectionActivityBinding
import com.example.seekandlog.interfaces.IListApps
import com.example.seekandlog.viewmodels.AppSelectionViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration


class AppSelectionActivity : AppCompatActivity(),
    SelectableAppsAdapter.OnAppSelectionChangedCallback {

    companion object {
        fun buildIntent(context: Context) = Intent(context, AppSelectionActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private val binding: AppSelectionActivityBinding
            by lazy { AppSelectionActivityBinding.inflate(layoutInflater) }

    private val appsViewModel: AppSelectionViewModel by viewModels()
    private val appsAdapter = SelectableAppsAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.select_apps_to_monitor)
        setContentView(binding.root)

        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvApps.addItemDecoration(MaterialDividerItemDecoration(this, LinearLayout.VERTICAL))
        binding.rvApps.adapter = appsAdapter

        appsViewModel.appList.observe(this) {
            appsAdapter.apps = it
        }

        appsViewModel.selectedApps.observe(this) {
            binding.startLogBtn.isVisible = it.isNotEmpty()
        }

        appsAdapter.onAppSelectionListener = this

        binding.startLogBtn.setOnClickListener {
            if (checkUsageStatsPermission())
                startActivity(
                    AppMonitorActivity.buildIntent(this, appsViewModel.selectedApps.value)
                )
            else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_not_allowed)
                    .setMessage(R.string.permission_not_allowed_msg)
                    .show()
            }
        }

        loadAppList()
    }

    override fun onStart() {
        super.onStart()

        // clear file before start logging
        appsViewModel.clearFile()
    }

    // Get app list from specific class into on demand module
    // (cannot access directly cause this module doesn't have dependency)
    private fun loadAppList() {
        try {
            val listAppsClass = (Class.forName(AppSelectionViewModel.FEATURE_LIST_APPS_CLASS_NAME)
                .newInstance() as? IListApps)
            listAppsClass?.getApps(packageManager)?.let { appsViewModel.setAppList(it) }
        } catch (ex: Exception) {
            ex.printStackTrace()
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.error_load_apps)
                .show()
        }
    }

    // Check PACKAGE_USAGE_STATS permission. It cannot be requested (it's a special permission)
    // need to be handled by AppOpsManager
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(APP_OPS_SERVICE) as? AppOpsManager
        val mode =
            appOpsManager?.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, myUid(), packageName)

        return if (mode == AppOpsManager.MODE_DEFAULT)
            checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) === PackageManager.PERMISSION_GRANTED
        else mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onAppSelectionChanged() {
        appsViewModel.updateSelectedApps()
    }

}