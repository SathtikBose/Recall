package com.buildstack.recall.domain.model

data class Reminder(
    val id: Int = 0,
    val title: String,
    val description: String,
    val reminderDate: String,
    val reminderTime: String,
    val priority: PriorityLevel,
    val category: String,
    val repeatType: RepeatType,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val notificationId: Int
)

enum class PriorityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class RepeatType(val label: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}
