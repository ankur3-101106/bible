package com.sanctuary.bible.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_NAME = "sanctuary_daily_reminder_work"

    fun scheduleDailyReminder(context: Context, hour: Int = 8, minute: Int = 0) {
        val now = LocalDateTime.now()
        var scheduledTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute))
        if (now.isAfter(scheduledTime)) {
            scheduledTime = scheduledTime.plusDays(1)
        }

        val initialDelayMinutes = Duration.between(now, scheduledTime).toMinutes().coerceAtLeast(1)

        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
