package com.tests.jewelry

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
class CleanupWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            val logsDir = File(applicationContext.filesDir, "logs")
            if(logsDir.exists()) {
                logsDir.listFiles()?.forEach { file ->
                    if(file.isFile && file.name.endsWith(".log")) {
                        file.delete()
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}