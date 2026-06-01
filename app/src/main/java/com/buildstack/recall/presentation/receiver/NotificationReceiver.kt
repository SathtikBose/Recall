package com.buildstack.recall.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.buildstack.recall.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE")
        val desc = intent.getStringExtra("EXTRA_DESC")
        val notificationId = intent.getIntExtra("EXTRA_ID", 1)

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_DESC", desc)
            putExtra("EXTRA_ID", notificationId)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
