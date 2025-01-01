package com.imperivox.android2clean

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager

class CleanerApplication : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        WorkManager.initialize(this, workManagerConfiguration)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val cleanupChannel = NotificationChannel(
                CLEANUP_CHANNEL_ID,
                "Cleanup Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about cleanup operations"
            }

            val scheduledChannel = NotificationChannel(
                SCHEDULED_CHANNEL_ID,
                "Scheduled Cleanups",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for scheduled cleanup reminders"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(cleanupChannel, scheduledChannel))
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    companion object {
        const val CLEANUP_CHANNEL_ID = "cleanup_channel"
        const val SCHEDULED_CHANNEL_ID = "scheduled_channel"
    }
}