package com.sanctuary.bible.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SanctuaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)

    @Query("SELECT * FROM plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<PlanEntity?>

    @Query("SELECT * FROM plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlanSync(): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanDays(days: List<PlanDayEntity>)

    @Query("SELECT * FROM plan_days WHERE planId = :planId ORDER BY dateIso ASC")
    fun getPlanDays(planId: String): Flow<List<PlanDayEntity>>

    @Query("SELECT * FROM plan_days WHERE planId = :planId ORDER BY dateIso ASC")
    suspend fun getPlanDaysSync(planId: String): List<PlanDayEntity>

    @Query("UPDATE plan_days SET completed = :completed WHERE id = :dayId")
    suspend fun updatePlanDayCompletion(dayId: String, completed: Boolean)

    @Query("DELETE FROM plan_days WHERE planId = :planId")
    suspend fun deletePlanDays(planId: String)

    @Query("DELETE FROM plans")
    suspend fun deleteAllPlans()

    // Bookmarks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    suspend fun getAllBookmarksSync(): List<BookmarkEntity>

    @Query("DELETE FROM bookmarks WHERE bookName = :bookName AND chapter = :chapter AND verse = :verse")
    suspend fun deleteBookmark(bookName: String, chapter: Int, verse: Int)

    // Notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    suspend fun getAllNotesSync(): List<NoteEntity>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestNote(): Flow<NoteEntity?>

    // Highlights
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity)

    @Query("SELECT * FROM highlights WHERE bookName = :bookName AND chapter = :chapter")
    fun getHighlightsForChapter(bookName: String, chapter: Int): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights ORDER BY createdAt DESC")
    suspend fun getAllHighlightsSync(): List<HighlightEntity>

    // Reading Positions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingPosition(position: ReadingPositionEntity)

    @Query("SELECT * FROM reading_positions WHERE bookName = :bookName AND chapter = :chapter LIMIT 1")
    fun getReadingPosition(bookName: String, chapter: Int): Flow<ReadingPositionEntity?>

    @Query("SELECT * FROM reading_positions WHERE bookName = :bookName AND chapter = :chapter LIMIT 1")
    suspend fun getReadingPositionSync(bookName: String, chapter: Int): ReadingPositionEntity?

    @Query("DELETE FROM reading_positions WHERE bookName = :bookName AND chapter = :chapter")
    suspend fun clearReadingPosition(bookName: String, chapter: Int)

    @Query("SELECT * FROM reading_positions ORDER BY updatedAt DESC")
    fun getAllReadingPositions(): Flow<List<ReadingPositionEntity>>

    @Query("SELECT * FROM reading_positions ORDER BY updatedAt DESC")
    suspend fun getAllReadingPositionsSync(): List<ReadingPositionEntity>

    // Completed Chapters
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedChapter(completedChapter: CompletedChapterEntity)

    @Query("SELECT * FROM completed_chapters")
    fun getAllCompletedChapters(): Flow<List<CompletedChapterEntity>>

    @Query("SELECT * FROM completed_chapters")
    suspend fun getAllCompletedChaptersSync(): List<CompletedChapterEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM completed_chapters WHERE chapterRef = :chapterRef)")
    suspend fun isChapterCompleted(chapterRef: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM completed_chapters WHERE chapterRef = :chapterRef)")
    fun isChapterCompletedFlow(chapterRef: String): Flow<Boolean>

    @Query("DELETE FROM completed_chapters WHERE chapterRef = :chapterRef")
    suspend fun deleteCompletedChapter(chapterRef: String)

    @Query("DELETE FROM completed_chapters")
    suspend fun deleteAllCompletedChapters()
}
