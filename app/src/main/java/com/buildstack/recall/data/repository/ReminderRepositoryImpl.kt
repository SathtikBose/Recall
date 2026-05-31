package com.buildstack.recall.data.repository

import com.buildstack.recall.data.local.dao.ReminderDao
import com.buildstack.recall.data.mapper.toDomain
import com.buildstack.recall.data.mapper.toEntity
import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val dao: ReminderDao
) : ReminderRepository {
    override suspend fun insertReminder(reminder: Reminder) {
        dao.insertReminder(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        dao.updateReminder(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        dao.deleteReminder(reminder.toEntity())
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return dao.getAllReminders().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return dao.getReminderById(id)?.toDomain()
    }

    override fun getCompletedReminders(): Flow<List<Reminder>> {
        return dao.getCompletedReminders().map { list -> list.map { it.toDomain() } }
    }

    override fun searchReminders(query: String): Flow<List<Reminder>> {
        return dao.searchReminders(query).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun markCompleted(id: Int) {
        dao.markCompleted(id, System.currentTimeMillis())
    }

    override suspend fun restoreReminder(id: Int) {
        dao.restoreReminder(id, System.currentTimeMillis())
    }
}
