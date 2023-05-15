package com.example.seekandlog.objs

import com.example.seekandlog.FileUtils
import java.time.LocalDateTime

/**
 * This class represents a log
 * @title the app title
 * @date/time the date/time when the app use is detected
 */
data class AppLog(
    val title: String,
    val date: String,
    val time: String
) {
    companion object {
        fun factory(packageName: String, dateTime: LocalDateTime): AppLog {
            return AppLog(
                packageName,
                dateTime.toLocalDate().toString(),
                dateTime.toLocalTime().toString()
            )
        }

        fun factory(fileLine: String): AppLog {
            val logString = fileLine.split(FileUtils.LOG_VALUES_SEPARATOR)
            return AppLog(
                logString[0].trim(),   // title
                logString[1].trim(),   // date
                logString[2].trim()    // time
            )
        }
    }

    fun getStringForFile() = "$title, $date, $time\n"
}
