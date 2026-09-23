package com.sanctuary.bible.domain

import com.sanctuary.bible.data.model.PlanDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlanningEngineTest {

    @Test
    fun testGeneratePlan_CompleteBible_AllChaptersAccountedFor() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 12, 31)

        val planDays = PlanningEngine.generatePlan("test_plan", startDate, endDate)

        val totalAssignedChapters = planDays.sumOf { it.chapters.size }
        assertEquals(1189, totalAssignedChapters)

        val flattenedAssigned = planDays.flatMap { it.chapters }
        val uniqueAssigned = flattenedAssigned.toSet()

        assertEquals(1189, uniqueAssigned.size)
        assertEquals(PlanningEngine.ALL_CHAPTERS_FLAT, flattenedAssigned)
    }

    @Test
    fun testGeneratePlan_OldTestament_ExactScope() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 6, 30)
        val otChapters = PlanningEngine.ALL_CHAPTERS_FLAT.subList(0, 929)

        val planDays = PlanningEngine.generatePlan("ot_plan", startDate, endDate, otChapters)

        val totalAssigned = planDays.sumOf { it.chapters.size }
        assertEquals(929, totalAssigned)
        assertEquals("Genesis 1", planDays.first().chapters.first())
        assertEquals("Malachi 4", planDays.last().chapters.last())
        assertEquals(otChapters, planDays.flatMap { it.chapters })
    }

    @Test
    fun testGeneratePlan_NewTestament_ExactScope() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 3, 31)
        val ntChapters = PlanningEngine.ALL_CHAPTERS_FLAT.subList(929, 1189)

        val planDays = PlanningEngine.generatePlan("nt_plan", startDate, endDate, ntChapters)

        val totalAssigned = planDays.sumOf { it.chapters.size }
        assertEquals(260, totalAssigned)
        assertEquals("Matthew 1", planDays.first().chapters.first())
        assertEquals("Revelation 22", planDays.last().chapters.last())
        assertEquals(ntChapters, planDays.flatMap { it.chapters })
    }

    @Test
    fun testGeneratePlan_GospelsAndActs_ExactScope() {
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 2, 28)
        val gospelsChapters = PlanningEngine.ALL_CHAPTERS_FLAT.subList(929, 1046)

        val planDays = PlanningEngine.generatePlan("gospels_plan", startDate, endDate, gospelsChapters)

        val totalAssigned = planDays.sumOf { it.chapters.size }
        assertEquals(117, totalAssigned)
        assertEquals("Matthew 1", planDays.first().chapters.first())
        assertEquals("Acts 28", planDays.last().chapters.last())
    }

    @Test
    fun testFormatReadingString() {
        val single = listOf("Genesis 1")
        assertEquals("Genesis 1", PlanningEngine.formatReadingString(single))

        val sameBook = listOf("Genesis 1", "Genesis 2", "Genesis 3")
        assertEquals("Genesis 1–3", PlanningEngine.formatReadingString(sameBook))

        val multiBook = listOf("Genesis 50", "Exodus 1", "Exodus 2")
        assertEquals("Genesis 50 to Exodus 2", PlanningEngine.formatReadingString(multiBook))
    }

    @Test
    fun testCalculateStreak() {
        val today = LocalDate.of(2025, 5, 10)
        val days = listOf(
            PlanDay("d1", "p1", today.minusDays(2), "May 8", listOf("Genesis 1"), "Genesis 1", completed = true),
            PlanDay("d2", "p1", today.minusDays(1), "May 9", listOf("Genesis 2"), "Genesis 2", completed = true),
            PlanDay("d3", "p1", today, "May 10", listOf("Genesis 3"), "Genesis 3", completed = true)
        )

        val streak = PlanningEngine.calculateStreak(days, today)
        assertEquals(3, streak)
    }

    @Test
    fun testCalculateLongestStreak() {
        val baseDate = LocalDate.of(2025, 1, 1)
        val days = listOf(
            PlanDay("d1", "p1", baseDate, "Jan 1", listOf("Genesis 1"), "G1", completed = true),
            PlanDay("d2", "p1", baseDate.plusDays(1), "Jan 2", listOf("Genesis 2"), "G2", completed = true),
            PlanDay("d3", "p1", baseDate.plusDays(2), "Jan 3", listOf("Genesis 3"), "G3", completed = true),
            PlanDay("d4", "p1", baseDate.plusDays(3), "Jan 4", listOf("Genesis 4"), "G4", completed = false), // Gap
            PlanDay("d5", "p1", baseDate.plusDays(4), "Jan 5", listOf("Genesis 5"), "G5", completed = true),
            PlanDay("d6", "p1", baseDate.plusDays(5), "Jan 6", listOf("Genesis 6"), "G6", completed = true)
        )

        val longest = PlanningEngine.calculateLongestStreak(days)
        assertEquals(3, longest)
    }

    @Test
    fun testRedistributePlan_PreservesCompletedAndReallocatesUnread() {
        val startDate = LocalDate.of(2025, 1, 1)
        val originalEndDate = LocalDate.of(2025, 1, 10)

        val initialDays = PlanningEngine.generatePlan("p1", startDate, originalEndDate)

        val modifiedDays = initialDays.mapIndexed { index, day ->
            if (index < 2) day.copy(completed = true) else day
        }

        val today = LocalDate.of(2025, 1, 5)
        val redistributed = PlanningEngine.redistributePlan(modifiedDays, today, originalEndDate)

        assertTrue(redistributed[0].completed)
        assertTrue(redistributed[1].completed)

        val totalChapters = redistributed.sumOf { it.chapters.size }
        assertEquals(1189, totalChapters)

        val flattened = redistributed.flatMap { it.chapters }
        assertEquals(PlanningEngine.ALL_CHAPTERS_FLAT, flattened)
    }
}
