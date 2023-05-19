package com.example.seekandlog.viewmodels

import android.app.Application
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.seekandlog.FileUtils
import com.example.seekandlog.ListAppsProvider
import com.example.seekandlog.R
import com.example.seekandlog.interfaces.IListApps
import com.example.seekandlog.objs.SelectableApp
import com.example.seekandlog.objs.SelectableAppInfo

/**
 * view model used by AppSelectionActivity
 * it manages the list of apps and the selected ones
 */
class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private var listApps: IListApps? = ListAppsProvider.provide()

    val appList = MutableLiveData<List<SelectableApp>>()
    val selectedApps = MutableLiveData<List<SelectableAppInfo>>()

    init {
        // Load apps from provided dependency
        listApps?.getApps(application.packageManager, true)?.let {
            appList.value = it
        } ?: run {
            AlertDialog.Builder(getApplication())
                .setTitle(R.string.error)
                .setMessage(R.string.error_load_apps)
                .show()
        }
    }

    fun updateSelectedApps() {
        selectedApps.value =
            appList.value?.filter { it.selected }?.map { it.info } ?: listOf()
    }

    fun clearFile() {
        FileUtils.clearFile(getApplication())
    }
}