package com.buildstack.recall.presentation.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE")
        val desc = intent.getStringExtra("EXTRA_DESC")
        val notificationId = intent.getIntExtra("EXTRA_ID", 1)
        val snoozeDuration = intent.getIntExtra("EXTRA_SNOOZE_DURATION", 5)

        // Stop the alarm service
        val stopIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP
        }
        context.startService(stopIntent)

        // Schedule new alarm for snooze duration
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val rescheduleIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_DESC", desc)
            putExtra("EXTRA_ID", notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            rescheduleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (snoozeDuration * 60 * 1000)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
