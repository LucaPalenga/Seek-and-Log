package com.example.seekandlog.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.seekandlog.FileUtils
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

    fun setAppList(apps: List<SelectableApp>) {
        appList.value = apps
    }

    fun updateSelectedApps() {
        selectedApps.value =
            appList.value?.filter { it.selected }?.map { it.description } ?: listOf()
    }

    fun clearFile() {
        FileUtils.clearFile(getApplication())
    }
}