package com.sanctuary.bible.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey val id: String,
    val title: String,
    val startDateIso: String,
    val endDateIso: String,
    val isActive: Boolean
)

@Entity(tableName = "plan_days")
data class PlanDayEntity(
    @PrimaryKey val id: String,
    val planId: String,
    val dateIso: String,
    val displayDate: String,
    val chaptersJson: String,
    val readingString: String,
    val completed: Boolean
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val colorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)
