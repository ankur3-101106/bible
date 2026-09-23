package com.sanctuary.bible.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanctuary.bible.data.local.PlanDayEntity
import com.sanctuary.bible.data.local.PlanEntity
import com.sanctuary.bible.data.local.SanctuaryDao
import com.sanctuary.bible.data.model.Plan
import com.sanctuary.bible.data.model.PlanDay
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val activePlanDays: Flow<List<PlanDay>> = activePlan.flatMapLatest { plan ->
        if (plan == null) flowOf(emptyList())
        else {
            sanctuaryDao.getPlanDays(plan.id).map { entities ->
                entities.map { entity ->
                    val type = object : TypeToken<List<String>>() {}.type
                    val chapters: List<String> = gson.fromJson(entity.chaptersJson, type)
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
            }
        }
    }

    suspend fun createPlan(startDate: LocalDate, endDate: LocalDate, title: String = "Complete the Bible"): Plan {
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

        val planDays = PlanningEngine.generatePlan(planId, startDate, endDate)
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

        return Plan(planId, title, startDate, endDate, true)
    }

    suspend fun toggleDayCompleted(dayId: String, completed: Boolean) {
        sanctuaryDao.updatePlanDayCompletion(dayId, completed)
    }

    suspend fun redistributeActivePlan(today: LocalDate = LocalDate.now()) {
        val activePlanEntity = sanctuaryDao.getActivePlanSync() ?: return
        val currentDays = sanctuaryDao.getPlanDaysSync(activePlanEntity.id).map { entity ->
            val type = object : TypeToken<List<String>>() {}.type
            val chapters: List<String> = gson.fromJson(entity.chaptersJson, type)
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

    suspend fun ensureDefaultPlanExists() {
        val active = sanctuaryDao.getActivePlanSync()
        if (active == null) {
            val today = LocalDate.now()
            val nextYear = today.plusDays(364)
            createPlan(today, nextYear)
        }
    }
}
