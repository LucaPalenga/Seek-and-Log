package com.example.seekandlog.objs

import com.example.seekandlog.FileUtils
import com.example.seekandlog.Values
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

        fun factory(fileLine: String): AppLog? {
            val logString = fileLine
                .split(FileUtils.LOG_VALUES_SEPARATOR)
                .filter { it != Values.EMPTY_STRING }

            if (logString.isNotEmpty())
                return AppLog(
                    logString[StringIndexes.TITLE_INDEX.ordinal].trim(),
                    logString[StringIndexes.DATE_INDEX.ordinal].trim(),
                    logString[StringIndexes.TIME_INDEX.ordinal].trim()
                )
            return null
        }
    }

    fun getStringForFile(): String {
        val rt = StringBuilder()

        StringIndexes.values().forEach {
            val field = when (it) {
                StringIndexes.TITLE_INDEX -> title.trim()
                StringIndexes.DATE_INDEX -> date.trim()
                StringIndexes.TIME_INDEX -> time.trim()
            }
            rt.append(field).append(FileUtils.LOG_VALUES_SEPARATOR)
        }

        if (rt.lastOrNull() == (FileUtils.LOG_VALUES_SEPARATOR)) {
            rt.deleteCharAt(rt.lastIndex)
        }
        rt.append(FileUtils.LOGS_LINE_SEPARATOR)

        return rt.toString()
    }
}

enum class StringIndexes { TITLE_INDEX, DATE_INDEX, TIME_INDEX }
