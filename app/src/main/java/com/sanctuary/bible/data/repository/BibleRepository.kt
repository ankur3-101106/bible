package com.sanctuary.bible.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanctuary.bible.data.local.BookmarkEntity
import com.sanctuary.bible.data.local.HighlightEntity
import com.sanctuary.bible.data.local.NoteEntity
import com.sanctuary.bible.data.local.SanctuaryDao
import com.sanctuary.bible.data.model.Bookmark
import com.sanctuary.bible.data.model.Highlight
import com.sanctuary.bible.data.model.Note
import com.sanctuary.bible.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private data class JsonBook(
    val abbrev: String,
    val name: String?,
    val chapters: List<List<String>>
)

class BibleRepository(
    private val context: Context,
    private val sanctuaryDao: SanctuaryDao
) {
    private var cachedBibleData: List<JsonBook>? = null

    suspend fun loadBibleData(): List<JsonBook> = withContext(Dispatchers.IO) {
        cachedBibleData?.let { return@withContext it }
        val jsonString = context.assets.open("en_kjv.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<JsonBook>>() {}.type
        val data: List<JsonBook> = Gson().fromJson(jsonString, type)
        cachedBibleData = data
        data
    }

    suspend fun getChapterVerses(bookName: String, chapter: Int): List<Verse> {
        val data = loadBibleData()
        val book = data.find { it.name.equals(bookName, ignoreCase = true) } ?: return emptyList()
        if (chapter < 1 || chapter > book.chapters.size) return emptyList()

        val rawVerses = book.chapters[chapter - 1]
        return rawVerses.mapIndexed { index, rawText ->
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

    suspend fun saveBookmark(bookName: String, chapter: Int, verse: Int) {
        sanctuaryDao.insertBookmark(
            BookmarkEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse
            )
        )
    }

    suspend fun saveNote(bookName: String, chapter: Int, verse: Int, text: String) {
        sanctuaryDao.insertNote(
            NoteEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                noteText = text
            )
        )
    }

    suspend fun saveHighlight(bookName: String, chapter: Int, verse: Int, colorHex: String) {
        sanctuaryDao.insertHighlight(
            HighlightEntity(
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                colorHex = colorHex
            )
        )
    }
}
