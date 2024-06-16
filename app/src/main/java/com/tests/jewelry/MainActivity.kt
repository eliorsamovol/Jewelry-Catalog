package com.tests.jewelry

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import com.tests.jewelry.databinding.ActivityMainBinding
import androidx.core.content.ContextCompat
import android.os.Build
import android.view.View
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.content.Context
import androidx.work.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var airplaneModeReceiver: AirplaneModeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        } else {
            // For API levels below 21, you can modify the color of the toolbar to create a similar effect
            supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.status_bar_color_fallback))
        }

        // Optional: Set status bar icons to light or dark for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Call LogUtils.writeLog multiple times
        LogUtils.writeLog(this, "Sample log message 1")
        LogUtils.writeLog(this, "Sample log message 2")
        LogUtils.writeLog(this, "Sample log message 3")

        // List log files to verify they exist
        listLogFiles(this)

        scheduleCleanupWorker(this)

        airplaneModeReceiver = AirplaneModeReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(airplaneModeReceiver, intentFilter)
    }
    fun scheduleCleanupWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()

        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(2, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "CleanupWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            cleanupRequest
        )
    }

    private fun listLogFiles(context: Context) {
        val logDir = File(context.filesDir, "logs")
        if (logDir.exists()) {
            val logFiles = logDir.listFiles()
            logFiles?.forEach {
                Log.d("LogFiles", "Log file: ${it.name}")
            }
        } else {
            Log.d("LogFiles", "Log directory does not exist")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airplaneModeReceiver)
    }
}