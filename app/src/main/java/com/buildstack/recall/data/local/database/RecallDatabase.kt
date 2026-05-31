package com.buildstack.recall.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buildstack.recall.data.local.dao.ReminderDao
import com.buildstack.recall.data.local.entity.ReminderEntity

@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecallDatabase : RoomDatabase() {
    abstract val reminderDao: ReminderDao
}
