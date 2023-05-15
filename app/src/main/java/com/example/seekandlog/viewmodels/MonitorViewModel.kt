package com.example.seekandlog.viewmodels

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seekandlog.FileUtils
import com.example.seekandlog.objs.AppLog
import com.example.seekandlog.objs.SelectableAppDescription
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
        const val POLLING_DELAY = 2000L   // acceptable delay for polling
        const val EVENTS_DETECTION_INTERVAL = 1000 * 1000 // interval (in the past)
    }

    private val mapAppPkgTitles: HashMap<String, String> = hashMapOf()
    private var lastAppResumed: AppLog? = null

    var lastLogs = MutableStateFlow<List<AppLog>>(mutableListOf())

    fun setSelectedApps(selectedApps: List<SelectableAppDescription>) {
        selectedApps.forEach { appDesc ->
            appDesc.packageName?.let { pkg -> mapAppPkgTitles[pkg] = appDesc.title }
        }
    }

    // a kind of "polling" on selected apps
    fun startLogging(usageStatsManager: UsageStatsManager) {
        if (mapAppPkgTitles.isNotEmpty()) {
            viewModelScope.launch {
                while (true) {
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
            if (mapAppPkgTitles.contains(it.key)) {
                when (it.value.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        mapAppPkgTitles[it.key]?.let { appTitle ->

                            // TODO ignore if the same app is already in foreground?
                            // side-effect if app is opened 2 times it's not detected
                            if (lastAppResumed?.title != appTitle) {
                                val detectedAppLog =
                                    AppLog.factory(appTitle, LocalDateTime.now())
                                logsFound.add(detectedAppLog)
                                lastAppResumed = detectedAppLog
                            }
                        }
                    }
                }
            }
        }
        if (logsFound.isNotEmpty())
            FileUtils.saveLogsToFile(getApplication(), logsFound)
    }
}
