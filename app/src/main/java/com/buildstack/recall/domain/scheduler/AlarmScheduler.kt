package com.buildstack.recall.domain.scheduler

import com.buildstack.recall.domain.model.Reminder

interface AlarmScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminder: Reminder)
}
