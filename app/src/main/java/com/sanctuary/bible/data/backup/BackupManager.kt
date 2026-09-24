package com.sanctuary.bible.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sanctuary.bible.data.local.SanctuaryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class BackupManager(
    private val context: Context,
    private val database: SanctuaryDatabase
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun createBackup(): SanctuaryBackup = withContext(Dispatchers.IO) {
        val dao = database.sanctuaryDao()
        val plan = dao.getActivePlanSync()
        val plans = if (plan != null) listOf(plan) else emptyList()
        val planDays = if (plan != null) dao.getPlanDaysSync(plan.id) else emptyList()
        val bookmarks = dao.getAllBookmarksSync()
        val notes = dao.getAllNotesSync()
        val highlights = dao.getAllHighlightsSync()
        val positions = dao.getAllReadingPositionsSync()
        val completed = dao.getAllCompletedChaptersSync()

        SanctuaryBackup(
            backupVersion = 1,
            createdAt = Instant.now().toString(),
            appVersion = "1.0",
            plans = plans,
            planDays = planDays,
            bookmarks = bookmarks,
            notes = notes,
            highlights = highlights,
            readingPositions = positions,
            completedChapters = completed
        )
    }

    suspend fun exportBackupToUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backup = createBackup()
            val jsonString = gson.toJson(backup)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
            } ?: return@withContext Result.failure(Exception("Could not open file for writing"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackupFromUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext Result.failure(Exception("Could not read backup file"))

            val backup = gson.fromJson(jsonString, SanctuaryBackup::class.java)
                ?: return@withContext Result.failure(Exception("Invalid JSON format"))

            val validation = validateBackup(backup)
            if (validation.isFailure) {
                return@withContext Result.failure(validation.exceptionOrNull()!!)
            }

            // Perform transactional database replacement
            database.withTransaction {
                val dao = database.sanctuaryDao()
                dao.deleteAllPlans()

                backup.plans.forEach { dao.insertPlan(it) }
                dao.insertPlanDays(backup.planDays)
                backup.bookmarks.forEach { dao.insertBookmark(it) }
                backup.notes.forEach { dao.insertNote(it) }
                backup.highlights.forEach { dao.insertHighlight(it) }
                backup.readingPositions.forEach { dao.saveReadingPosition(it) }
                backup.completedChapters.forEach { dao.insertCompletedChapter(it) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun validateBackup(backup: SanctuaryBackup): Result<Unit> {
        if (backup.backupVersion > 1) {
            return Result.failure(Exception("Unsupported backup version: ${backup.backupVersion}"))
        }

        // Validate date formats in plan days
        for (day in backup.planDays) {
            try {
                LocalDate.parse(day.dateIso)
            } catch (e: Exception) {
                return Result.failure(Exception("Corrupted date format in backup: ${day.dateIso}"))
            }
        }

        return Result.success(Unit)
    }
}
