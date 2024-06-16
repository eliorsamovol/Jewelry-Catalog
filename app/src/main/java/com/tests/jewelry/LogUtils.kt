package com.tests.jewelry

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {
    private const val LOG_DIR = "logs"
    private const val LOG_FILE = "log.txt"
    fun writeLog(context: Context, message: String) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val logDir = File(context.getExternalFilesDir(null), LOG_DIR)
            if (!logDir.exists()) {
                logDir.mkdirs()
            }

            val logFile = File(logDir, LOG_FILE)
            try {
                FileWriter(logFile, true).use { writer ->
                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    writer.append("$timestamp: $message\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}