package com.example.seekandlog.viewmodels

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seekandlog.FileUtils
import com.example.seekandlog.objs.AppLog
import com.example.seekandlog.objs.SelectableAppData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MonitorViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val POLLING_DELAY = 2000L   // acceptable delay for polling
        const val EVENTS_DETECTION_INTERVAL = 1000 * 1000 // interval (in the past)
    }

    var selectedApps: List<SelectableAppData>? = null
    var lastLogs = MutableStateFlow<List<AppLog>>(mutableListOf())
    var lastAppInForeground: AppLog? = null

    private val appPackageNames: List<String?>
        get() = selectedApps?.map { it.packageName } ?: listOf()

    // a kind of "polling" on selected apps
    fun startLogging(usageStatsManager: UsageStatsManager) {
        viewModelScope.launch {
            selectedApps?.get(0)?.let {
                while (true) {
                    detectForegroundApps(usageStatsManager)
                    delay(POLLING_DELAY)
                }
            }
        }
    }

    fun getLogs() {
        lastLogs.value = FileUtils.readLogsFromFile(getApplication())
    }

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

    // save only when app is first time opening
    private fun detectForegroundApps(usageStatsManager: UsageStatsManager) {
        val logsFound = mutableListOf<AppLog>()
        getEventsMap(usageStatsManager).forEach {
            if (appPackageNames.contains(it.key)) {
                when (it.value.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        getAppDataBy(it.key)?.let { appData ->

                            // TODO ignore if the same app is already in foreground?
                            // side-effect if app is opened 2 times it's not detected
                            if (lastAppInForeground?.title != appData.title) {
                                val detectedAppLog =
                                    AppLog.factory(appData.title, LocalDateTime.now())
                                logsFound.add(detectedAppLog)
                                lastAppInForeground = detectedAppLog
                            }
                        }
                    }
                }
            }
        }
        if (logsFound.isNotEmpty())
            FileUtils.saveLogsToFile(getApplication(), logsFound)
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
