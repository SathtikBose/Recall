package com.buildstack.recall.data.mapper

import com.buildstack.recall.data.local.entity.ReminderEntity
import com.buildstack.recall.domain.model.PriorityLevel
import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.model.RepeatType

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        reminderDate = reminderDate,
        reminderTime = reminderTime,
        priority = PriorityLevel.valueOf(priority),
        category = category,
        repeatType = RepeatType.valueOf(repeatType),
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        notificationId = notificationId
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        reminderDate = reminderDate,
        reminderTime = reminderTime,
        priority = priority.name,
        category = category,
        repeatType = repeatType.name,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        notificationId = notificationId
    )
}
