package com.sanctuary.bible.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DateHandlingTest {

    @Test
    fun testLocalDateArithmetic_SpansMidnightCleanly() {
        val start = LocalDate.of(2026, 1, 1)
        val end = LocalDate.of(2026, 12, 31)

        val daysBetween = ChronoUnit.DAYS.between(start, end).toInt() + 1
        assertEquals(365, daysBetween)
    }

    @Test
    fun testLeapYearHandling() {
        val start = LocalDate.of(2028, 1, 1)
        val end = LocalDate.of(2028, 12, 31)

        val daysBetween = ChronoUnit.DAYS.between(start, end).toInt() + 1
        assertEquals(366, daysBetween)
    }
}
