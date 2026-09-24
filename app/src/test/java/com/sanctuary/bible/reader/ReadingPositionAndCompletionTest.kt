package com.sanctuary.bible.reader

import com.sanctuary.bible.data.local.ReadingPositionEntity
import com.sanctuary.bible.domain.PlanningEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingPositionAndCompletionTest {

    @Test
    fun testOneReadingPositionPerChapter() {
        val positionMap = mutableMapOf<Pair<String, Int>, ReadingPositionEntity>()

        fun savePos(bookName: String, chapter: Int, verse: Int) {
            val entity = ReadingPositionEntity(bookName, chapter, verse)
            positionMap[Pair(bookName, chapter)] = entity
        }

        savePos("Genesis", 1, 10)
        assertEquals(10, positionMap[Pair("Genesis", 1)]?.verse)

        // Replace with verse 24 in same chapter
        savePos("Genesis", 1, 24)
        assertEquals(1, positionMap.size)
        assertEquals(24, positionMap[Pair("Genesis", 1)]?.verse)

        // Save position in another chapter
        savePos("Genesis", 2, 5)
        assertEquals(2, positionMap.size)
        assertEquals(5, positionMap[Pair("Genesis", 2)]?.verse)
    }

    @Test
    fun testClearingReadingPositionOnChapterCompletion() {
        val positionMap = mutableMapOf<Pair<String, Int>, ReadingPositionEntity>()
        val completedChapters = mutableSetOf<String>()

        fun completeChapter(bookName: String, chapter: Int) {
            val ref = "$bookName $chapter"
            completedChapters.add(ref)
            positionMap.remove(Pair(bookName, chapter))
        }

        positionMap[Pair("Exodus", 20)] = ReadingPositionEntity("Exodus", 20, 12)
        assertEquals(12, positionMap[Pair("Exodus", 20)]?.verse)

        completeChapter("Exodus", 20)
        assertTrue(completedChapters.contains("Exodus 20"))
        assertNull(positionMap[Pair("Exodus", 20)])
    }

    @Test
    fun testPartialDayCompletionVsFullDayCompletion() {
        val dayChapters = listOf("Romans 1", "Romans 2", "Romans 3", "Romans 4")
        val completedSet = mutableSetOf<String>()

        fun isDayComplete(): Boolean = dayChapters.all { completedSet.contains(it) }

        // Complete 1 of 4 chapters
        completedSet.add("Romans 1")
        assertFalse(isDayComplete())
        assertEquals(1, completedSet.size)

        // Complete remaining chapters
        completedSet.add("Romans 2")
        completedSet.add("Romans 3")
        assertFalse(isDayComplete())

        completedSet.add("Romans 4")
        assertTrue(isDayComplete())
        assertEquals(4, completedSet.size)
    }

    @Test
    fun testIdempotentChapterCompletion() {
        val completedSet = mutableSetOf<String>()

        fun completeChapter(chapterRef: String) {
            completedSet.add(chapterRef)
        }

        completeChapter("Matthew 1")
        completeChapter("Matthew 1")
        completeChapter("Matthew 1")

        assertEquals(1, completedSet.size)
        assertTrue(completedSet.contains("Matthew 1"))
    }

    @Test
    fun testChapterNavigationBoundaries() {
        val genesis = PlanningEngine.BIBLE_BOOKS.find { it.name == "Genesis" }!!
        assertEquals(50, genesis.chapterCount)

        val revelation = PlanningEngine.BIBLE_BOOKS.find { it.name == "Revelation" }!!
        assertEquals(22, revelation.chapterCount)

        val malachi = PlanningEngine.BIBLE_BOOKS.find { it.name == "Malachi" }!!
        val malachiIndex = PlanningEngine.BIBLE_BOOKS.indexOf(malachi)
        val nextBookAfterMalachi = PlanningEngine.BIBLE_BOOKS[malachiIndex + 1]
        assertEquals("Matthew", nextBookAfterMalachi.name)
    }
}
