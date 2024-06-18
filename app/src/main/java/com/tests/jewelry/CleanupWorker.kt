package com.tests.jewelry

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.util.concurrent.TimeUnit
import android.util.Log
class CleanupWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        private const val TAG = "CleanupWorker"
        private const val CLEANUP_INTERVAL = 24L
    }
    override fun doWork(): Result {
        Log.d(TAG, "Cleanup work is being executed")

        // Clean up log files older than 24 hours
        val logDirectory = File(applicationContext.filesDir, "logs")
        val logFiles = logDirectory.listFiles { _, name -> name.endsWith(".txt") }
        val currentTime = System.currentTimeMillis()

        try {
            for (file in logFiles) {
                val fileCreationTime = file.lastModified()

                if (currentTime - fileCreationTime > TimeUnit.HOURS.toMillis(24)) {
                    file.delete()
                    Log.d(TAG, "Deleted log file: ${file.name}")
                } else {
                    Log.d(TAG, "Keeping log file: ${file.name}")
                }
            }
            Log.d(TAG, "Cleanup work is completed")

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up logs", e)
            return Result.failure()
        }
    }
}