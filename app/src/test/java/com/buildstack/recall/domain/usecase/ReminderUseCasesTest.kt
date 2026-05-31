package com.buildstack.recall.domain.usecase

import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import com.buildstack.recall.domain.model.PriorityLevel
import com.buildstack.recall.domain.model.RepeatType

class FakeReminderRepository : ReminderRepository {

    private val reminders = mutableListOf<Reminder>()

    override suspend fun insertReminder(reminder: Reminder) {
        reminders.add(reminder)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder
        }
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminders.remove(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return flowOf(reminders)
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminders.find { it.id == id }
    }

    override fun getCompletedReminders(): Flow<List<Reminder>> {
        return flowOf(reminders.filter { it.isCompleted })
    }

    override fun searchReminders(query: String): Flow<List<Reminder>> {
        return flowOf(reminders.filter { it.title.contains(query, ignoreCase = true) })
    }

    override fun getAllCategories(): Flow<List<String>> {
        return flowOf(reminders.map { it.category }.distinct())
    }

    override suspend fun getRemindersByDate(date: String): List<Reminder> {
        return reminders.filter { it.reminderDate == date }
    }

    override suspend fun markCompleted(id: Int) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            reminders[index] = reminders[index].copy(isCompleted = true)
        }
    }

    override suspend fun restoreReminder(id: Int) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            reminders[index] = reminders[index].copy(isCompleted = false)
        }
    }
}

class ReminderUseCasesTest {

    private lateinit var fakeRepository: FakeReminderRepository
    private lateinit var getReminderById: GetReminderById
    private lateinit var addReminder: AddReminder

    @Before
    fun setUp() {
        fakeRepository = FakeReminderRepository()
        getReminderById = GetReminderById(fakeRepository)
        addReminder = AddReminder(fakeRepository)
    }

    @Test
    fun `Add reminder, retrieves correct reminder`() = runBlocking {
        val reminder = Reminder(
            id = 1,
            title = "Test Reminder",
            description = "Description",
            reminderDate = "2026-05-31",
            reminderTime = "12:00",
            priority = PriorityLevel.MEDIUM,
            category = "Work",
            repeatType = RepeatType.NONE,
            isCompleted = false,
            createdAt = 1000L,
            updatedAt = 1000L,
            notificationId = 1234
        )
        addReminder(reminder)

        val retrievedReminder = getReminderById(1)

        assertEquals(reminder, retrievedReminder)
    }
}
