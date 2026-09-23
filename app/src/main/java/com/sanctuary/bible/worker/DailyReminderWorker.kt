package com.sanctuary.bible.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanctuary.bible.MainActivity
import com.sanctuary.bible.R
import com.sanctuary.bible.data.local.SanctuaryDatabase
import java.time.LocalDate

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "sanctuary_daily_reminder"
        const val CHANNEL_NAME = "Daily Scripture Reminders"
        const val NOTIFICATION_ID = 1001

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders for daily Bible reading portions"
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override suspend fun doWork(): Result {
        val database = SanctuaryDatabase.getDatabase(applicationContext)
        val planDao = database.sanctuaryDao()

        val activePlan = planDao.getActivePlanSync() ?: return Result.success()
        val planDays = planDao.getPlanDaysSync(activePlan.id)

        val today = LocalDate.now()
        val todayDay = planDays.find { it.dateIso == today.toString() }
            ?: planDays.find { !it.completed && LocalDate.parse(it.dateIso).isAfter(today) }

        if (todayDay == null || todayDay.completed) {
            return Result.success()
        }

        sendNotification(
            title = "Today's Bible Reading",
            content = "${todayDay.readingString} (~${(todayDay.chaptersJson.count { it == ',' } + 1) * 4} min)"
        )

        return Result.success()
    }

    private fun sendNotification(title: String, content: String) {
        createNotificationChannel(applicationContext)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "read")
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Sanctuary • $title")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
