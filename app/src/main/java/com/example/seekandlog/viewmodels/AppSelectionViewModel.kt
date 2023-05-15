package com.example.seekandlog.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.seekandlog.FileUtils
import com.example.seekandlog.objs.SelectableApp
import com.example.seekandlog.objs.SelectableAppData

class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {

    val appList = MutableLiveData<List<SelectableApp>>()
    val selectedApps = MutableLiveData<List<SelectableAppData>>()

    fun setAppList(apps: List<SelectableApp>) {
        appList.value = apps
    }

    fun updateSelectedApps() {
        selectedApps.value = appList.value?.filter { it.selected }?.map { it.data } ?: listOf()
    }

    fun clearFile() {
        FileUtils.clearFile(getApplication())
    }

    companion object {
        const val FEATURE_LIST_APPS_CLASS_NAME = "com.example.seekappfeature.ListApps"
    }
}