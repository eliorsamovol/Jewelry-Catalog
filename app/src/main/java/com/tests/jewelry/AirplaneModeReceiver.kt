package com.tests.jewelry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.FileObserver
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class AirplaneModeReceiver : BroadcastReceiver () {
    override fun onReceive(context: Context, intent: Intent?) { // Listener for airplane mode change
        if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            Log.d("AirplaneModeReceiver", "Airplane mode changed: $isAirplaneModeOn")
            playSound(context)
        }
    }

    private fun playSound(context: Context?){ // Play sound
        val mediaPlayer = MediaPlayer.create(context, R.raw.screenshot_sound)
        mediaPlayer.start()
    }
}