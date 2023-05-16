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
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seekandlog.R
import com.example.seekandlog.adapters.SelectableAppsAdapter
import com.example.seekandlog.databinding.AppSelectionActivityBinding
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

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                appsAdapter.filter(newText)
                return true
            }
        })

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
    }

    override fun onStart() {
        super.onStart()
        appsViewModel.clearFile()   // clear file before start logging
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