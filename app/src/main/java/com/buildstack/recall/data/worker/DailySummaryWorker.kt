package com.buildstack.recall.data.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.buildstack.recall.R
import com.buildstack.recall.domain.repository.ReminderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class DailySummaryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ReminderRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val todaysReminders = repository.getRemindersByDate(todayStr)
        
        if (todaysReminders.isNotEmpty()) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val notification = NotificationCompat.Builder(context, "recall_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Summary")
                .setContentText("You have ${todaysReminders.size} reminder(s) due today.")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("You have ${todaysReminders.size} reminder(s) due today:\n" + 
                        todaysReminders.take(3).joinToString("\n") { "- ${it.title} at ${it.reminderTime}" } +
                        if (todaysReminders.size > 3) "\n...and ${todaysReminders.size - 3} more" else ""
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(9999, notification)
        }

        return Result.success()
    }
}
