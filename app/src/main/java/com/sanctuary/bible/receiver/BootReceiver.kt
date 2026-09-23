package com.sanctuary.bible.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sanctuary.bible.worker.NotificationScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.scheduleDailyReminder(context)
        }
    }
}
