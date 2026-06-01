package com.buildstack.recall.presentation.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.recall.domain.model.PriorityLevel
import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.model.RepeatType
import com.buildstack.recall.domain.usecase.ReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditState(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val reminderDate: String = "",
    val reminderTime: String = "08:00:00",
    val priority: PriorityLevel = PriorityLevel.LOW,
    val category: String = "Personal",
    val repeatType: RepeatType = RepeatType.NONE,
    val isCompleted: Boolean = false,
    val notificationId: Int = 0
)


@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val useCases: ReminderUseCases,
    private val alarmScheduler: com.buildstack.recall.domain.scheduler.AlarmScheduler,
    private val preferencesManager: com.buildstack.recall.data.local.datastore.PreferencesManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditState())
    val state: StateFlow<AddEditState> = _state.asStateFlow()

    val is24HourFormat: StateFlow<Boolean> = preferencesManager.is24HourFormat
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        savedStateHandle.get<Int>("id")?.let { id ->
            if (id != -1) {
                viewModelScope.launch {
                    useCases.getReminderById(id)?.let { reminder ->
                        _state.value = AddEditState(
                            id = reminder.id,
                            title = reminder.title,
                            description = reminder.description,
                            reminderDate = reminder.reminderDate,
                            reminderTime = reminder.reminderTime,
                            priority = reminder.priority,
                            category = reminder.category,
                            repeatType = reminder.repeatType,
                            isCompleted = reminder.isCompleted,
                            notificationId = reminder.notificationId
                        )
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateCategory(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun updateDate(date: String) {
        _state.update { it.copy(reminderDate = date) }
    }

    fun updateTime(time: String) {
        _state.update { it.copy(reminderTime = time) }
    }

    fun updatePriority(priority: PriorityLevel) {
        _state.update { it.copy(priority = priority) }
    }

    fun saveReminder(onSaved: () -> Unit) {
        val currentState = state.value
        if (currentState.title.isBlank() || currentState.reminderDate.isBlank() || currentState.reminderTime.isBlank()) {
            return // Add validation handling in real app
        }

        viewModelScope.launch {
            val reminder = Reminder(
                id = currentState.id ?: 0,
                title = currentState.title,
                description = currentState.description,
                reminderDate = currentState.reminderDate,
                reminderTime = currentState.reminderTime,
                priority = currentState.priority,
                category = currentState.category,
                repeatType = currentState.repeatType,
                isCompleted = currentState.isCompleted,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                notificationId = currentState.notificationId.takeIf { it != 0 } ?: System.currentTimeMillis().toInt()
            )

            if (currentState.id != null) {
                useCases.updateReminder(reminder)
            } else {
                useCases.addReminder(reminder)
            }
            
            // Schedule the alarm for this reminder
            alarmScheduler.schedule(reminder)
            
            onSaved()
        }
    }
}
