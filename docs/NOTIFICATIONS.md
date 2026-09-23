# Sanctuary Daily Notifications & Background Scheduling

## Architecture Overview

Sanctuary uses Android's **WorkManager** framework (`androidx.work.WorkManager`) to schedule daily Scripture reading reminders reliably across application restarts, device reboots, and background state changes.

### Components
1. **`DailyReminderWorker.kt`**:
   * A `CoroutineWorker` that executes lightweight state evaluation.
   * Queries `SanctuaryDatabase` via `PlanRepository`.
   * Evaluates current active plan status and today's reading completion.
   * If today's reading is complete or no active plan exists, skips notification.
   * If reading is incomplete or overdue, posts notification with real assignment text (e.g., `"Today's Bible Reading: Genesis 42–45 (~18 min)"`).
2. **`NotificationScheduler.kt`**:
   * Uses `PeriodicWorkRequestBuilder` with 24-hour interval.
   * Enqueues unique work with `ExistingPeriodicWorkPolicy.UPDATE` to prevent duplicate workers.
3. **`BootReceiver.kt`**:
   * Listens for `android.intent.action.BOOT_COMPLETED` broadcast and reschedules `NotificationScheduler`.
4. **Android 13+ Permission Handling**:
   * Requests `POST_NOTIFICATIONS` runtime permission.
