package com.tests.jewelry

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.util.concurrent.TimeUnit
import android.util.Log
class CleanupWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("CleanupWorker", "Cleanup work is being executed")

        val directory = File(applicationContext.filesDir, "logs")
        val currentTime = System.currentTimeMillis()

        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                // Define the condition for old files, e.g., files older than 7 days
                val fileAge = currentTime - file.lastModified()
                val maxAgeMillis = TimeUnit.MINUTES.toMillis(2)

                if (fileAge > maxAgeMillis) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("CleanupWorker", "Deleted old log file: ${file.name}")
                    } else {
                        Log.d("CleanupWorker", "Failed to delete old log file: ${file.name}")
                    }
                }
            }
        } else {
            Log.d("CleanupWorker", "Log directory does not exist")
        }

        return Result.success()
    }
}