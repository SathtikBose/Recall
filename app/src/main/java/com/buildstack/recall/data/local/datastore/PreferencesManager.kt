package com.buildstack.recall.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recall_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val IS_24_HOUR_FORMAT = booleanPreferencesKey("is_24_hour_format")
        val IS_DAILY_SUMMARY_ENABLED = booleanPreferencesKey("is_daily_summary_enabled")
        val DAILY_SUMMARY_TIME = androidx.datastore.preferences.core.stringPreferencesKey("daily_summary_time")
        val SNOOZE_DURATION = androidx.datastore.preferences.core.intPreferencesKey("snooze_duration")
    }

    val is24HourFormat: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_24_HOUR_FORMAT] ?: false
    }

    val isDailySummaryEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DAILY_SUMMARY_ENABLED] ?: false
    }

    val dailySummaryTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DAILY_SUMMARY_TIME] ?: "08:00"
    }

    suspend fun set24HourFormat(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_24_HOUR_FORMAT] = enabled
        }
    }

    suspend fun setDailySummaryEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DAILY_SUMMARY_ENABLED] = enabled
        }
    }

    suspend fun setDailySummaryTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_SUMMARY_TIME] = time
        }
    }

    val snoozeDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SNOOZE_DURATION] ?: 5
    }

    suspend fun setSnoozeDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[SNOOZE_DURATION] = minutes
        }
    }
}
