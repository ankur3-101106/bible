package com.sanctuary.bible.backup

import com.sanctuary.bible.data.backup.BackupManager
import com.sanctuary.bible.data.backup.SanctuaryBackup
import com.sanctuary.bible.data.local.PlanDayEntity
import com.sanctuary.bible.data.local.PlanEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupManagerTest {

    @Test
    fun testValidBackup_PassesValidation() {
        val backup = SanctuaryBackup(
            backupVersion = 1,
            createdAt = "2026-06-01T20:00:00Z",
            appVersion = "1.0",
            plans = listOf(
                PlanEntity("p1", "Complete the Bible", "2026-01-01", "2026-12-31", true)
            ),
            planDays = listOf(
                PlanDayEntity("d1", "p1", "2026-01-01", "Jan 1", "[\"Genesis 1\"]", "Genesis 1", true)
            )
        )

        // Mock context and db are not needed for pure data validation
        val dummyManager = BackupManager(org.mockito.kotlin.mock(), org.mockito.kotlin.mock())
        val result = dummyManager.validateBackup(backup)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testUnsupportedVersion_FailsValidation() {
        val backup = SanctuaryBackup(
            backupVersion = 99,
            createdAt = "2026-06-01T20:00:00Z",
            appVersion = "2.0"
        )

        val dummyManager = BackupManager(org.mockito.kotlin.mock(), org.mockito.kotlin.mock())
        val result = dummyManager.validateBackup(backup)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Unsupported backup version"))
    }

    @Test
    fun testCorruptedDateFormat_FailsValidation() {
        val backup = SanctuaryBackup(
            backupVersion = 1,
            createdAt = "2026-06-01T20:00:00Z",
            planDays = listOf(
                PlanDayEntity("d1", "p1", "INVALID_DATE", "Jan 1", "[\"Genesis 1\"]", "Genesis 1", false)
            )
        )

        val dummyManager = BackupManager(org.mockito.kotlin.mock(), org.mockito.kotlin.mock())
        val result = dummyManager.validateBackup(backup)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Corrupted date format"))
    }
}
