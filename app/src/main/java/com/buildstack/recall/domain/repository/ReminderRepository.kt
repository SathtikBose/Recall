package com.buildstack.recall.domain.repository

import com.buildstack.recall.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Int): Reminder?
    fun getCompletedReminders(): Flow<List<Reminder>>
    fun searchReminders(query: String): Flow<List<Reminder>>
    fun getAllCategories(): Flow<List<String>>
    suspend fun getRemindersByDate(date: String): List<Reminder>
    suspend fun markCompleted(id: Int)
    suspend fun restoreReminder(id: Int)
}
