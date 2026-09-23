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

    @Query("DELETE FROM bookmarks WHERE bookName = :bookName AND chapter = :chapter AND verse = :verse")
    suspend fun deleteBookmark(bookName: String, chapter: Int, verse: Int)

    // Notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestNote(): Flow<NoteEntity?>

    // Highlights
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity)

    @Query("SELECT * FROM highlights WHERE bookName = :bookName AND chapter = :chapter")
    fun getHighlightsForChapter(bookName: String, chapter: Int): Flow<List<HighlightEntity>>
}
