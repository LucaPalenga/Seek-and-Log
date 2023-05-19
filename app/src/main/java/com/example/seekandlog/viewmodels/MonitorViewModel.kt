package com.example.seekandlog.viewmodels

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.seekandlog.FileUtils
import com.example.seekandlog.objs.AppLog
import com.example.seekandlog.objs.SelectableAppInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * view model used by AppMonitorActivity
 * it uses an infinite loop to monitor the apps selected previously
 * and record writing logs into file when these apps come to foreground
 */
class MonitorViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val POLLING_DELAY = 500L   // acceptable delay for polling
        const val EVENTS_DETECTION_INTERVAL = 1000 // interval (in the past)
    }

    val monitoredApps = MutableLiveData<List<SelectableAppInfo>>()
    private val mapPkgApp: HashMap<String, SelectableAppInfo> = hashMapOf()
    private var lastAppResumed: AppLog? = null

    private val _stopMonitoring = MutableLiveData(false)
    val stopMonitoring: LiveData<Boolean>
        get() = _stopMonitoring

    var lastLogs = MutableStateFlow<List<AppLog>>(mutableListOf())

    fun setSelectedApps(selectedApps: List<SelectableAppInfo>) {
        monitoredApps.value = selectedApps
        selectedApps.forEach { appInfo ->
            appInfo.packageName?.let { pkg -> mapPkgApp[pkg] = appInfo }
        }
    }

    // a kind of "polling" on selected apps
    fun startLogging(usageStatsManager: UsageStatsManager) {
        if (mapPkgApp.isNotEmpty()) {
            viewModelScope.launch {
                while (stopMonitoring.value == false) {
                    detectForegroundApps(usageStatsManager)
                    delay(POLLING_DELAY)
                }
            }
        }
    }

    fun updateLogs() {
        lastLogs.value = FileUtils.readLogsFromFile(getApplication())
        // reset last app resumed to be able to log also the same app when come back to this activity
        lastAppResumed = null
    }

    // extract events (using hashmap to avoid repetitions)
    private fun getEventsMap(usageStatsManager: UsageStatsManager): Map<String, UsageEvents.Event> {
        val rt = hashMapOf<String, UsageEvents.Event>()

        val end = System.currentTimeMillis()
        val begin = end - EVENTS_DETECTION_INTERVAL
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
            if (mapPkgApp.contains(it.key)) {
                when (it.value.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        mapPkgApp[it.key]?.let { appInfo ->

                            if (lastAppResumed?.uid != appInfo.uid) {
                                val detectedAppLog =
                                    AppLog.factory(appInfo, LocalDateTime.now())
                                logsFound.add(detectedAppLog)
                                lastAppResumed = detectedAppLog
                            }
                        }
                    }
                }
            }
        }

        if (logsFound.isNotEmpty()) {
            FileUtils.saveLogsToFile(getApplication(), logsFound)
        }
    }

    fun toggleMonitoring() {
        _stopMonitoring.value = _stopMonitoring.value != true
    }
}
