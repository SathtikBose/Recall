package com.buildstack.recall.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.buildstack.recall.domain.repository.ReminderRepository
import com.buildstack.recall.domain.scheduler.AlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val reminders = repository.getAllReminders().first()
            val now = System.currentTimeMillis()
            
            reminders.filter { !it.isCompleted }.forEach { reminder ->
                alarmScheduler.schedule(reminder)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
