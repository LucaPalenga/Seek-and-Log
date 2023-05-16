package com.example.seekandlog.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.seekandlog.FileUtils
import com.example.seekandlog.R
import com.example.seekandlog.interfaces.IListApps
import com.example.seekandlog.objs.SelectableApp
import com.example.seekandlog.objs.SelectableAppDescription

/**
 * view model used by AppSelectionActivity
 * it manages the list of apps and the selected ones
 */
class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val FEATURE_LIST_APPS_CLASS_NAME = "com.example.seekappfeature.ListApps"
    }

    val appList = MutableLiveData<List<SelectableApp>>()
    val selectedApps = MutableLiveData<List<SelectableAppDescription>>()

    init {
        loadAppList(application.packageManager)
    }

    fun updateSelectedApps() {
        selectedApps.value =
            appList.value?.filter { it.selected }?.map { it.description } ?: listOf()
    }

    fun clearFile() {
        FileUtils.clearFile(getApplication())
    }

    // Get app list from specific class into on demand module
    // (cannot access directly cause this module doesn't have dependency)
    private fun loadAppList(packageManager: PackageManager) {
        try {
            val listAppsClass = (Class.forName(AppSelectionViewModel.FEATURE_LIST_APPS_CLASS_NAME)
                .newInstance() as? IListApps)
            listAppsClass?.getApps(packageManager, true)?.let { appList.value = it }
        } catch (ex: Exception) {
            ex.printStackTrace()
            AlertDialog.Builder(getApplication())
                .setTitle(R.string.error)
                .setMessage(R.string.error_load_apps)
                .show()
        }
    }
}