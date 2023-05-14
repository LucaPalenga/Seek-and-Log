package com.example.seekandlog.objs

import java.time.LocalDateTime

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
    }
}
