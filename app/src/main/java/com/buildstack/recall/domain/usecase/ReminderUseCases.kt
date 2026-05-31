package com.buildstack.recall.domain.usecase

import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class GetReminders(private val repository: ReminderRepository) {
    operator fun invoke(): Flow<List<Reminder>> {
        return repository.getAllReminders()
    }
}

class AddReminder(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) {
        repository.insertReminder(reminder)
    }
}

class UpdateReminder(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) {
        repository.updateReminder(reminder)
    }
}

class DeleteReminder(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) {
        repository.deleteReminder(reminder)
    }
}

class MarkReminderCompleted(private val repository: ReminderRepository) {
    suspend operator fun invoke(id: Int) {
        repository.markCompleted(id)
    }
}

data class ReminderUseCases(
    val getReminders: GetReminders,
    val addReminder: AddReminder,
    val updateReminder: UpdateReminder,
    val deleteReminder: DeleteReminder,
    val markReminderCompleted: MarkReminderCompleted
)
