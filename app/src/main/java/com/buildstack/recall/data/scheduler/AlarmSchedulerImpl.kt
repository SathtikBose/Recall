package com.buildstack.recall.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.scheduler.AlarmScheduler
import com.buildstack.recall.presentation.receiver.NotificationReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(reminder: Reminder) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", reminder.title)
            putExtra("EXTRA_DESC", reminder.description)
            putExtra("EXTRA_ID", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeInMillis = calculateTimeInMillis(reminder.reminderDate, reminder.reminderTime)
        if (timeInMillis <= System.currentTimeMillis()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Need permission to schedule exact alarms
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    override fun cancel(reminder: Reminder) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun calculateTimeInMillis(dateStr: String, timeStr: String): Long {
        return try {
            val formatterDate = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
            val date = LocalDate.parse(dateStr, formatterDate)
            val time = LocalTime.parse(timeStr, formatterTime)
            val dateTime = LocalDateTime.of(date, time)
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: DateTimeParseException) {
            System.currentTimeMillis() // Default fallback to avoid crashes, shouldn't happen with validation
        }
    }
}
