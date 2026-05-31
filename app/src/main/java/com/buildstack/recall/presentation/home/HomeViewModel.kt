package com.buildstack.recall.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.recall.domain.model.Reminder
import com.buildstack.recall.domain.usecase.ReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: ReminderUseCases
) : ViewModel() {

    val reminders: StateFlow<List<Reminder>> = useCases.getReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun markCompleted(id: Int) {
        viewModelScope.launch {
            useCases.markReminderCompleted(id)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            useCases.deleteReminder(reminder)
        }
    }
}
