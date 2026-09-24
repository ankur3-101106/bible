package com.sanctuary.bible.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanctuary.bible.data.local.CompletedChapterEntity
import com.sanctuary.bible.data.local.PlanDayEntity
import com.sanctuary.bible.data.local.PlanEntity
import com.sanctuary.bible.data.local.SanctuaryDao
import com.sanctuary.bible.data.model.Plan
import com.sanctuary.bible.data.model.PlanDay
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PlanRepository(
    private val sanctuaryDao: SanctuaryDao
) {
    private val gson = Gson()

    val activePlan: Flow<Plan?> = sanctuaryDao.getActivePlan().map { entity ->
        entity?.let {
            Plan(
                id = it.id,
                title = it.title,
                startDate = LocalDate.parse(it.startDateIso),
                endDate = LocalDate.parse(it.endDateIso),
                isActive = it.isActive
            )
        }
    }

    val completedChapterRefs: Flow<Set<String>> = sanctuaryDao.getAllCompletedChapters().map { list ->
        list.map { it.chapterRef }.toSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val activePlanDays: Flow<List<PlanDay>> = combine(
        activePlan,
        completedChapterRefs
    ) { plan, completedSet ->
        Pair(plan, completedSet)
    }.flatMapLatest { (plan, completedSet) ->
        if (plan == null) flowOf(emptyList())
        else {
            sanctuaryDao.getPlanDays(plan.id).map { entities ->
                entities.map { entity ->
                    val type = object : TypeToken<List<String>>() {}.type
                    val chapters: List<String> = gson.fromJson(entity.chaptersJson, type) ?: emptyList()
                    val isDayFullyCompleted = chapters.isNotEmpty() && chapters.all { completedSet.contains(it) }

                    if (isDayFullyCompleted && !entity.completed) {
                        sanctuaryDao.updatePlanDayCompletion(entity.id, true)
                    }

                    PlanDay(
                        id = entity.id,
                        planId = entity.planId,
                        date = LocalDate.parse(entity.dateIso),
                        displayDate = entity.displayDate,
                        chapters = chapters,
                        readingString = entity.readingString,
                        completed = isDayFullyCompleted || entity.completed
                    )
                }
            }
        }
    }

    suspend fun createPlan(
        startDate: LocalDate,
        endDate: LocalDate,
        title: String = "Complete the Bible",
        chapters: List<String> = PlanningEngine.ALL_CHAPTERS_FLAT
    ): Plan = withContext(Dispatchers.IO) {
        sanctuaryDao.deleteAllPlans()

        val planId = "plan_${System.currentTimeMillis()}"
        val planEntity = PlanEntity(
            id = planId,
            title = title,
            startDateIso = startDate.toString(),
            endDateIso = endDate.toString(),
            isActive = true
        )
        sanctuaryDao.insertPlan(planEntity)

        val planDays = PlanningEngine.generatePlan(
            planId = planId,
            startDate = startDate,
            endDate = endDate,
            chaptersToAssign = chapters
        )
        val dayEntities = planDays.map { day ->
            PlanDayEntity(
                id = day.id,
                planId = planId,
                dateIso = day.date.toString(),
                displayDate = day.displayDate,
                chaptersJson = gson.toJson(day.chapters),
                readingString = day.readingString,
                completed = day.completed
            )
        }
        sanctuaryDao.insertPlanDays(dayEntities)

        Plan(planId, title, startDate, endDate, true)
    }

    suspend fun toggleDayCompleted(dayId: String, completed: Boolean) = withContext(Dispatchers.IO) {
        sanctuaryDao.updatePlanDayCompletion(dayId, completed)
        val activePlanEntity = sanctuaryDao.getActivePlanSync() ?: return@withContext
        val dayEntities = sanctuaryDao.getPlanDaysSync(activePlanEntity.id)
        val targetDay = dayEntities.find { it.id == dayId } ?: return@withContext

        val type = object : TypeToken<List<String>>() {}.type
        val chapters: List<String> = gson.fromJson(targetDay.chaptersJson, type) ?: emptyList()

        for (chapterRef in chapters) {
            if (completed) {
                sanctuaryDao.insertCompletedChapter(CompletedChapterEntity(chapterRef))
            } else {
                sanctuaryDao.deleteCompletedChapter(chapterRef)
            }
        }
    }

    suspend fun isChapterCompleted(chapterRef: String): Boolean = withContext(Dispatchers.IO) {
        sanctuaryDao.isChapterCompleted(chapterRef)
    }

    suspend fun markChapterCompleted(chapterRef: String) = withContext(Dispatchers.IO) {
        sanctuaryDao.insertCompletedChapter(CompletedChapterEntity(chapterRef))

        val activePlanEntity = sanctuaryDao.getActivePlanSync() ?: return@withContext
        val dayEntities = sanctuaryDao.getPlanDaysSync(activePlanEntity.id)
        val allCompletedSet = sanctuaryDao.getAllCompletedChaptersSync().map { it.chapterRef }.toSet()

        for (day in dayEntities) {
            val type = object : TypeToken<List<String>>() {}.type
            val chapters: List<String> = gson.fromJson(day.chaptersJson, type) ?: emptyList()
            if (chapters.contains(chapterRef)) {
                val isFullyCompleted = chapters.all { allCompletedSet.contains(it) }
                if (isFullyCompleted && !day.completed) {
                    sanctuaryDao.updatePlanDayCompletion(day.id, true)
                }
                break
            }
        }
    }

    suspend fun unmarkChapterCompleted(chapterRef: String) = withContext(Dispatchers.IO) {
        sanctuaryDao.deleteCompletedChapter(chapterRef)
        val activePlanEntity = sanctuaryDao.getActivePlanSync() ?: return@withContext
        val dayEntities = sanctuaryDao.getPlanDaysSync(activePlanEntity.id)
        for (day in dayEntities) {
            val type = object : TypeToken<List<String>>() {}.type
            val chapters: List<String> = gson.fromJson(day.chaptersJson, type) ?: emptyList()
            if (chapters.contains(chapterRef)) {
                sanctuaryDao.updatePlanDayCompletion(day.id, false)
                break
            }
        }
    }

    suspend fun redistributeActivePlan(today: LocalDate = LocalDate.now()) = withContext(Dispatchers.IO) {
        val activePlanEntity = sanctuaryDao.getActivePlanSync() ?: return@withContext
        val currentDays = sanctuaryDao.getPlanDaysSync(activePlanEntity.id).map { entity ->
            val type = object : TypeToken<List<String>>() {}.type
            val chapters: List<String> = gson.fromJson(entity.chaptersJson, type) ?: emptyList()
            PlanDay(
                id = entity.id,
                planId = entity.planId,
                date = LocalDate.parse(entity.dateIso),
                displayDate = entity.displayDate,
                chapters = chapters,
                readingString = entity.readingString,
                completed = entity.completed
            )
        }

        val originalEndDate = LocalDate.parse(activePlanEntity.endDateIso)
        val updatedDays = PlanningEngine.redistributePlan(currentDays, today, originalEndDate)

        val newEndDate = updatedDays.maxOfOrNull { it.date } ?: originalEndDate
        sanctuaryDao.insertPlan(
            activePlanEntity.copy(endDateIso = newEndDate.toString())
        )

        sanctuaryDao.deletePlanDays(activePlanEntity.id)

        val updatedEntities = updatedDays.map { day ->
            PlanDayEntity(
                id = day.id,
                planId = day.planId,
                dateIso = day.date.toString(),
                displayDate = day.displayDate,
                chaptersJson = gson.toJson(day.chapters),
                readingString = day.readingString,
                completed = day.completed
            )
        }
        sanctuaryDao.insertPlanDays(updatedEntities)
    }

    suspend fun ensureDefaultPlanExists() = withContext(Dispatchers.IO) {
        val active = sanctuaryDao.getActivePlanSync()
        if (active == null) {
            val today = LocalDate.now()
            val nextYear = today.plusDays(364)
            createPlan(today, nextYear, "Complete the Bible", PlanningEngine.ALL_CHAPTERS_FLAT)
        }
    }
}
