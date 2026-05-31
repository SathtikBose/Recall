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
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Recall Reminder"
        val desc = intent.getStringExtra("EXTRA_DESC") ?: "It's time!"
        val notificationId = intent.getIntExtra("EXTRA_ID", 1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "RECALL_CHANNEL_ID",
                "Recall Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for Recall Reminders"
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "RECALL_CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
