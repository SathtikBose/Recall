package com.buildstack.recall.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val reminderDate: String,
    val reminderTime: String,
    val priority: String,
    val category: String,
    val repeatType: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val notificationId: Int
)
