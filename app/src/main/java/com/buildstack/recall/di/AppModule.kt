package com.buildstack.recall.di

import android.app.Application
import androidx.room.Room
import com.buildstack.recall.data.local.database.RecallDatabase
import com.buildstack.recall.data.repository.ReminderRepositoryImpl
import com.buildstack.recall.domain.repository.ReminderRepository
import com.buildstack.recall.domain.usecase.AddReminder
import com.buildstack.recall.domain.usecase.DeleteReminder
import com.buildstack.recall.domain.usecase.GetReminders
import com.buildstack.recall.domain.usecase.MarkReminderCompleted
import com.buildstack.recall.domain.usecase.ReminderUseCases
import com.buildstack.recall.domain.usecase.UpdateReminder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecallDatabase(app: Application): RecallDatabase {
        return Room.databaseBuilder(
            app,
            RecallDatabase::class.java,
            "recall_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(db: RecallDatabase): ReminderRepository {
        return ReminderRepositoryImpl(db.reminderDao)
    }

    @Provides
    @Singleton
    fun provideReminderUseCases(repository: ReminderRepository): ReminderUseCases {
        return ReminderUseCases(
            getReminders = GetReminders(repository),
            addReminder = AddReminder(repository),
            updateReminder = UpdateReminder(repository),
            deleteReminder = DeleteReminder(repository),
            markReminderCompleted = MarkReminderCompleted(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(app: Application): com.buildstack.recall.domain.scheduler.AlarmScheduler {
        return com.buildstack.recall.data.scheduler.AlarmSchedulerImpl(app)
    }
}
