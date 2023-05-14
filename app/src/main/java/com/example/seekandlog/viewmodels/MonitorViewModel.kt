package com.example.seekandlog.viewmodels

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.seekandlog.objs.AppLog
import com.example.seekandlog.objs.SelectableAppData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MonitorViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DELAY_BETWEEN_LOOPS = 3000L
        const val EVENTS_DETECTION_INTERVAL = 1000 * 1000
    }

    var selectedApps: List<SelectableAppData>? = null
    var lastLogs = MutableLiveData<List<AppLog>>()

    private val appPackageNames: List<String?>
        get() = selectedApps?.map { it.packageName } ?: listOf()

    // a kind of "polling" on selected apps
    fun startLogging(usageStatsManager: UsageStatsManager) {
        viewModelScope.launch {
            selectedApps?.get(0)?.let {
                while (true) {
                    detectForegroundApps(usageStatsManager)
                    delay(DELAY_BETWEEN_LOOPS)
                }
            }
        }
    }

    fun needMonitor(packageName: String): Boolean =
        selectedApps?.any { it.packageName == packageName } == true

    // extract events (using hashmap to avoid repetitions)
    private fun getEventsMap(usageStatsManager: UsageStatsManager): Map<String, UsageEvents.Event> {
        val rt = hashMapOf<String, UsageEvents.Event>()

        val interval = EVENTS_DETECTION_INTERVAL
        val end = System.currentTimeMillis()
        val begin = end - interval
        val usageEvents = usageStatsManager.queryEvents(begin, end);

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            rt[event.packageName] = event
        }

        return rt
    }

    private fun detectForegroundApps(usageStatsManager: UsageStatsManager) {
        val logsFound = mutableListOf<AppLog>()
        getEventsMap(usageStatsManager).forEach {
            if (appPackageNames.contains(it.key)) {
                when (it.value.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        getAppDataBy(it.key)?.let { appData ->
                            logsFound.add(AppLog.factory(appData.title, LocalDateTime.now()))
                        } ?: run {  // log anyway
                            logsFound.add(AppLog.factory(it.key, LocalDateTime.now()))
                        }
                    }
                }
            }
        }
        if (logsFound.isNotEmpty()) lastLogs.value = logsFound
    }

    private fun getAppDataBy(packageName: String): SelectableAppData? {
        var rt: SelectableAppData? = null
        selectedApps?.forEach {
            if (it.packageName == packageName) {
                rt = it
                return@forEach
            }
        }
        return rt
    }
}
