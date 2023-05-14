package com.example.seekandlog.workers

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class LogWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        const val DATA_ARRAY_TAG = "dataArray"
    }

    private var arrayPkgNames: Array<String>? = arrayOf()

    override suspend fun doWork(): Result {

        arrayPkgNames = inputData.getStringArray(DATA_ARRAY_TAG)
        Log.d("APPLOG", "array - ${printArray()} ")

        return withContext(Dispatchers.IO) {

            while (true) {
                Log.d("APPLOG", "timer - ${LocalDateTime.now().toLocalTime()} ")

                Thread.sleep(3000)

            }

            Result.success()
        }
    }

    private fun printArray(): String {
        val rt = StringBuilder()
        arrayPkgNames?.forEach {
            rt.append("$it ")
        }
        return rt.toString()
    }

    private fun checkAppsRunning(context: Context) {
        Log.d("APPLOG", "check running on $arrayPkgNames ")

        arrayPkgNames?.forEach {
            if (isAppRunning(context, it))
                Log.d("APPLOG", "Is RUNNING! $it ")
        }
    }

    private fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }
}