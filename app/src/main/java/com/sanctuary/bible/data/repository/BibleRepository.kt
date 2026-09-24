package com.sanctuary.bible.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanctuary.bible.data.local.BookmarkEntity
import com.sanctuary.bible.data.local.HighlightEntity
import com.sanctuary.bible.data.local.NoteEntity
import com.sanctuary.bible.data.local.ReadingPositionEntity
import com.sanctuary.bible.data.local.SanctuaryDao
import com.sanctuary.bible.data.model.Bookmark
import com.sanctuary.bible.data.model.Highlight
import com.sanctuary.bible.data.model.JsonBook
import com.sanctuary.bible.data.model.Note
import com.sanctuary.bible.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BibleRepository(
    private val context: Context,
    private val sanctuaryDao: SanctuaryDao
) {
    private var cachedBibleData: List<JsonBook>? = null

    suspend fun loadBibleData(): List<JsonBook> = withContext(Dispatchers.IO) {
        cachedBibleData?.let { return@withContext it }
        val jsonString = context.assets.open("en_kjv.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<JsonBook>>() {}.type
        val data: List<JsonBook>? = Gson().fromJson(jsonString, type)
        val result = data ?: emptyList()
        cachedBibleData = result
        result
    }

    suspend fun getChapterVerses(bookName: String, chapter: Int): List<Verse> = withContext(Dispatchers.IO) {
        val data = loadBibleData()
        val book = data.find { it.name.equals(bookName, ignoreCase = true) } ?: return@withContext emptyList()
        if (chapter < 1 || chapter > book.chapters.size) return@withContext emptyList()

        val rawVerses = book.chapters[chapter - 1]
        rawVerses.mapIndexed { index, rawText ->
            val cleanedText = rawText.replace(Regex("\\{(.*?)\\}"), "$1")
            Verse(
                bookName = book.name ?: bookName,
                chapter = chapter,
                verseNumber = index + 1,
                text = cleanedText
            )
        }
    }

    val latestNote: Flow<Note?> = sanctuaryDao.getLatestNote().map { entity ->
        entity?.let { Note(it.id, it.bookName, it.chapter, it.verse, it.noteText, it.createdAt, it.updatedAt) }
    }

    val bookmarks: Flow<List<Bookmark>> = sanctuaryDao.getAllBookmarks().map { list ->
        list.map { Bookmark(it.id, it.bookName, it.chapter, it.verse, it.createdAt) }
    }

    suspend fun saveBookmark(bookName: String, chapter: Int, verse: Int) = withContext(Dispatchers.IO) {
        sanctuaryDao.insertBookmark(
            BookmarkEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse
            )
        )
    }

    suspend fun saveNote(bookName: String, chapter: Int, verse: Int, text: String) = withContext(Dispatchers.IO) {
        sanctuaryDao.insertNote(
            NoteEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                noteText = text
            )
        )
    }

    suspend fun saveHighlight(bookName: String, chapter: Int, verse: Int, colorHex: String) = withContext(Dispatchers.IO) {
        sanctuaryDao.insertHighlight(
            HighlightEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                colorHex = colorHex
            )
        )
    }

    suspend fun saveReadingPosition(bookName: String, chapter: Int, verse: Int) = withContext(Dispatchers.IO) {
        sanctuaryDao.saveReadingPosition(
            ReadingPositionEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse
            )
        )
    }

    fun getReadingPosition(bookName: String, chapter: Int): Flow<ReadingPositionEntity?> {
        return sanctuaryDao.getReadingPosition(bookName, chapter)
    }

    suspend fun getReadingPositionSync(bookName: String, chapter: Int): ReadingPositionEntity? = withContext(Dispatchers.IO) {
        sanctuaryDao.getReadingPositionSync(bookName, chapter)
    }

    suspend fun clearReadingPosition(bookName: String, chapter: Int) = withContext(Dispatchers.IO) {
        sanctuaryDao.clearReadingPosition(bookName, chapter)
    }
}
