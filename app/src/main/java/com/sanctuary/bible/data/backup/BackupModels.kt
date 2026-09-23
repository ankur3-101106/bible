package com.sanctuary.bible.data.backup

import com.sanctuary.bible.data.local.BookmarkEntity
import com.sanctuary.bible.data.local.HighlightEntity
import com.sanctuary.bible.data.local.NoteEntity
import com.sanctuary.bible.data.local.PlanDayEntity
import com.sanctuary.bible.data.local.PlanEntity

data class SanctuaryBackup(
    val backupVersion: Int = 1,
    val createdAt: String,
    val appVersion: String = "1.0",
    val plans: List<PlanEntity> = emptyList(),
    val planDays: List<PlanDayEntity> = emptyList(),
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val highlights: List<HighlightEntity> = emptyList()
)
