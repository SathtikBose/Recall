package com.buildstack.recall.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.buildstack.recall.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY reminderDate ASC, reminderTime ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchReminders(query: String): Flow<List<ReminderEntity>>

    @Query("UPDATE reminders SET isCompleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun markCompleted(id: Int, timestamp: Long)

    @Query("UPDATE reminders SET isCompleted = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreReminder(id: Int, timestamp: Long)

    @Query("SELECT DISTINCT category FROM reminders WHERE category IS NOT NULL AND category != ''")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM reminders WHERE reminderDate = :date AND isCompleted = 0 ORDER BY reminderTime ASC")
    suspend fun getRemindersByDate(date: String): List<ReminderEntity>
}
