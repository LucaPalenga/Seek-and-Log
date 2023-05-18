package com.example.seekandlog

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import com.example.seekandlog.objs.AppLog
import java.io.FileOutputStream


/**
 * Class for file managing
 * read and write app logs on a file stored into internal storage
 * (data/data/com.example.seekandlog/files)
 */
object FileUtils {

    const val LOGS_LINE_SEPARATOR = '\n'
    const val LOG_VALUES_SEPARATOR = ','


    /**
     * Writes logs to file
     * each log separated by \n (1 line for each app log)
     * each log value separated by , (ex: AppTitle, DateOpening, TimeOpening)
     */
    fun saveLogsToFile(context: Context, appLogs: List<AppLog>) {
        try {
            val fOut: FileOutputStream = context.openFileOutput(Values.LOGS_FILE_NAME, MODE_APPEND)
            appLogs.forEach {
                fOut.write(it.getStringForFile().toByteArray())
            }
            fOut.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads logs from file
     * @return list of AppLog extracted
     */
    fun readLogsFromFile(context: Context): List<AppLog> {
        val rt = mutableListOf<AppLog>()

        try {
            val fileInput = context.openFileInput(Values.LOGS_FILE_NAME)
            var nextByteData: Int
            var buffer = Values.EMPTY_STRING
            while (fileInput.read().also { nextByteData = it } != -1) {
                buffer += nextByteData.toChar().toString()
            }

            buffer.split(LOGS_LINE_SEPARATOR).forEach {
                AppLog.factory(it)?.let { appLog -> rt.add(appLog) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rt
    }

    /**
     * Clear the log file
     */
    fun clearFile(context: Context) {
        val fOut = context.openFileOutput(Values.LOGS_FILE_NAME, MODE_PRIVATE)
        fOut.write(Values.EMPTY_STRING.toByteArray())
        fOut.close()
    }
}