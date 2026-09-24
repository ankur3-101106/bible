package com.sanctuary.bible.data.model

import java.time.LocalDate

data class JsonBook(
    val abbrev: String,
    val name: String?,
    val chapters: List<List<String>>
)

data class BibleBookMeta(
    val name: String,
    val chapterCount: Int,
    val abbrev: String
)

data class Verse(
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String
)

data class Plan(
    val id: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isActive: Boolean
)

data class PlanDay(
    val id: String,
    val planId: String,
    val date: LocalDate,
    val displayDate: String,
    val chapters: List<String>,
    val readingString: String,
    val completed: Boolean
)

data class Bookmark(
    val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val createdAt: Long = System.currentTimeMillis()
)

data class Note(
    val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Highlight(
    val id: Long = 0,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val colorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)
