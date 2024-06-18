package com.tests.jewelry

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

object LogUtils {
    private const val LOG_DIRECTORY = "logs"
    private const val TAG = "LogUtils"

    fun writeLog(context: Context, message: String) {
        val logDir = File(context.filesDir, LOG_DIRECTORY)
        if (!logDir.exists()) {
            val createdLogDir = logDir.mkdirs()
            Log.d(TAG, "Log directory created: $createdLogDir")
        } else {
            Log.d(TAG, "Log directory exists")
        }

        val logFile = File(logDir, "log_${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}.txt")
        if (logFile.exists()) {
            Log.d(TAG, "Log file path: $logFile")
        } else {
            val createdLogFile = logFile.createNewFile()
            Log.d(TAG, "Log file created: $createdLogFile")
        }
        try {
            val fileWriter = FileWriter(logFile, true)
            fileWriter.appendLine(
                "${
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                }: $message"
            )
            fileWriter.flush()
            fileWriter.close()
            Log.d(TAG, "Log written successfully")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Error writing log file", e)
        }
    }
}