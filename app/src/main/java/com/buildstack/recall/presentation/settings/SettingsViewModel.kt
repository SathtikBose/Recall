package com.buildstack.recall.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.recall.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import com.buildstack.recall.data.worker.DailySummaryWorker
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val is24HourFormat: StateFlow<Boolean> = preferencesManager.is24HourFormat
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggle24HourFormat(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.set24HourFormat(enabled)
        }
    }

    val isDailySummaryEnabled: StateFlow<Boolean> = preferencesManager.isDailySummaryEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val dailySummaryTime: StateFlow<String> = preferencesManager.dailySummaryTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "08:00"
        )

    fun toggleDailySummary(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDailySummaryEnabled(enabled)
            if (enabled) {
                scheduleDailySummary(dailySummaryTime.value)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork("DailySummary")
            }
        }
    }

    val snoozeDuration: StateFlow<Int> = preferencesManager.snoozeDuration
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 5
        )

    fun updateSnoozeDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesManager.setSnoozeDuration(minutes)
        }
    }

    fun updateDailySummaryTime(time: String) {
        viewModelScope.launch {
            preferencesManager.setDailySummaryTime(time)
            if (isDailySummaryEnabled.value) {
                scheduleDailySummary(time)
            }
        }
    }

    private fun scheduleDailySummary(time: String) {
        val parts = time.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: 8
        val minute = parts[1].toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailySummary",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
