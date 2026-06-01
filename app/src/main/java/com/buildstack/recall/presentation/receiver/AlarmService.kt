package com.buildstack.recall.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.buildstack.recall.MainActivity
import com.buildstack.recall.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.buildstack.recall.data.local.datastore.PreferencesManager
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var mediaPlayer: MediaPlayer? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        val title = intent?.getStringExtra("EXTRA_TITLE") ?: "Recall Reminder"
        val desc = intent?.getStringExtra("EXTRA_DESC") ?: "It's time!"
        val notificationId = intent?.getIntExtra("EXTRA_ID", 1) ?: 1

        playAlarmMusic()

        serviceScope.launch {
            val snoozeDuration = preferencesManager.snoozeDuration.first()
            showForegroundNotification(title, desc, notificationId, snoozeDuration)
        }

        return START_STICKY
    }

    private fun playAlarmMusic() {
        if (mediaPlayer != null) return
        
        try {
            val uri = Uri.parse("android.resource://$packageName/${R.raw.alarm_music}")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showForegroundNotification(title: String, desc: String, notificationId: Int, snoozeDuration: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "RECALL_ALARM_CHANNEL_ID"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recall Alarms",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for Recall Alarms"
            notificationManager.createNotificationChannel(channel)
        }

        // Open App Intent
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Intent
        val snoozeIntent = Intent(this, SnoozeReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_DESC", desc)
            putExtra("EXTRA_ID", notificationId)
            putExtra("EXTRA_SNOOZE_DURATION", snoozeDuration)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss Intent
        val dismissIntent = Intent(this, DismissReceiver::class.java).apply {
            putExtra("EXTRA_ID", notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId + 1, // different request code
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(openAppPendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .addAction(0, "Snooze ($snoozeDuration min)", snoozePendingIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(notificationId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        serviceJob.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    companion object {
        const val ACTION_STOP = "ACTION_STOP"
    }
}
