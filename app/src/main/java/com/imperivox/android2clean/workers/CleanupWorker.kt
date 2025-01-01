package com.imperivox.android2clean.workers

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.imperivox.android2clean.R
import com.imperivox.android2clean.data.repository.CleanerRepository
import java.util.concurrent.TimeUnit

class CleanupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val cleanerRepository = CleanerRepository(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cleaning in progress")
            .setProgress(0, 0, true)
            .build()

        setForeground(ForegroundInfo(NOTIFICATION_ID, notification))

        return try {
            var totalCleaned = 0L
            cleanerRepository.scanJunkFiles()
                .collect { junkFiles ->
                    val selectedFiles = junkFiles.filter { it.isSelected }
                    if (cleanerRepository.cleanJunkFiles(selectedFiles)) {
                        totalCleaned += selectedFiles.sumOf { it.size }
                    }
                }

            // Show completion notification
            val completionNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Cleanup completed")
                .setContentText("Cleaned ${formatSize(totalCleaned)}")
                .build()

            notificationManager.notify(NOTIFICATION_ID, completionNotification)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cleanup Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }

    companion object {
        private const val CHANNEL_ID = "cleanup_channel"
        private const val NOTIFICATION_ID = 1

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "scheduled_cleanup",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}