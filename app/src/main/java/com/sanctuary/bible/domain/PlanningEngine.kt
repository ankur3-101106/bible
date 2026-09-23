package com.sanctuary.bible.domain

import com.sanctuary.bible.data.model.BibleBookMeta
import com.sanctuary.bible.data.model.PlanDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

object PlanningEngine {

    val BIBLE_BOOKS = listOf(
        BibleBookMeta("Genesis", 50, "gn"), BibleBookMeta("Exodus", 40, "ex"),
        BibleBookMeta("Leviticus", 27, "lv"), BibleBookMeta("Numbers", 36, "nu"),
        BibleBookMeta("Deuteronomy", 34, "dt"), BibleBookMeta("Joshua", 24, "jos"),
        BibleBookMeta("Judges", 21, "judg"), BibleBookMeta("Ruth", 4, "ru"),
        BibleBookMeta("1 Samuel", 31, "1sa"), BibleBookMeta("2 Samuel", 24, "2sa"),
        BibleBookMeta("1 Kings", 22, "1ki"), BibleBookMeta("2 Kings", 25, "2ki"),
        BibleBookMeta("1 Chronicles", 29, "1ch"), BibleBookMeta("2 Chronicles", 36, "2ch"),
        BibleBookMeta("Ezra", 10, "ezr"), BibleBookMeta("Nehemiah", 13, "neh"),
        BibleBookMeta("Esther", 10, "est"), BibleBookMeta("Job", 42, "job"),
        BibleBookMeta("Psalms", 150, "ps"), BibleBookMeta("Proverbs", 31, "pr"),
        BibleBookMeta("Ecclesiastes", 12, "ec"), BibleBookMeta("Song of Solomon", 8, "so"),
        BibleBookMeta("Isaiah", 66, "is"), BibleBookMeta("Jeremiah", 52, "je"),
        BibleBookMeta("Lamentations", 5, "la"), BibleBookMeta("Ezekiel", 48, "ez"),
        BibleBookMeta("Daniel", 12, "da"), BibleBookMeta("Hosea", 14, "ho"),
        BibleBookMeta("Joel", 3, "jl"), BibleBookMeta("Amos", 9, "am"),
        BibleBookMeta("Obadiah", 1, "ob"), BibleBookMeta("Jonah", 4, "jon"),
        BibleBookMeta("Micah", 7, "mi"), BibleBookMeta("Nahum", 3, "na"),
        BibleBookMeta("Habakkuk", 3, "hab"), BibleBookMeta("Zephaniah", 3, "zep"),
        BibleBookMeta("Haggai", 2, "hag"), BibleBookMeta("Zechariah", 14, "zec"),
        BibleBookMeta("Malachi", 4, "mal"), BibleBookMeta("Matthew", 28, "mt"),
        BibleBookMeta("Mark", 16, "mr"), BibleBookMeta("Luke", 24, "lk"),
        BibleBookMeta("John", 21, "jn"), BibleBookMeta("Acts", 28, "ac"),
        BibleBookMeta("Romans", 16, "ro"), BibleBookMeta("1 Corinthians", 16, "1co"),
        BibleBookMeta("2 Corinthians", 13, "2co"), BibleBookMeta("Galatians", 6, "ga"),
        BibleBookMeta("Ephesians", 6, "ep"), BibleBookMeta("Philippians", 4, "ph"),
        BibleBookMeta("Colossians", 4, "co"), BibleBookMeta("1 Thessalonians", 5, "1th"),
        BibleBookMeta("2 Thessalonians", 3, "2th"), BibleBookMeta("1 Timothy", 6, "1ti"),
        BibleBookMeta("2 Timothy", 4, "2ti"), BibleBookMeta("Titus", 3, "ti"),
        BibleBookMeta("Philemon", 1, "phm"), BibleBookMeta("Hebrews", 13, "he"),
        BibleBookMeta("James", 5, "jas"), BibleBookMeta("1 Peter", 5, "1pe"),
        BibleBookMeta("2 Peter", 3, "2pe"), BibleBookMeta("1 John", 5, "1jo"),
        BibleBookMeta("2 John", 1, "2jo"), BibleBookMeta("3 John", 1, "3jo"),
        BibleBookMeta("Jude", 1, "jud"), BibleBookMeta("Revelation", 22, "re")
    )

    val ALL_CHAPTERS_FLAT: List<String> = buildList {
        BIBLE_BOOKS.forEach { book ->
            for (i in 1..book.chapterCount) {
                add("${book.name} $i")
            }
        }
    }

    const val TOTAL_BIBLE_CHAPTERS = 1189

    private val displayDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

    fun generatePlan(
        planId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        chaptersToAssign: List<String> = ALL_CHAPTERS_FLAT
    ): List<PlanDay> {
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        if (totalDays <= 0) return emptyList()

        val plan = mutableListOf<PlanDay>()
        var chapterIndex = 0

        for (day in 0 until totalDays) {
            if (chapterIndex >= chaptersToAssign.size) break

            val remainingDays = totalDays - day
            val remainingChapters = chaptersToAssign.size - chapterIndex
            val chaptersTodayCount = ceil(remainingChapters.toDouble() / remainingDays).toInt()

            val chaptersToday = chaptersToAssign.subList(
                chapterIndex,
                (chapterIndex + chaptersTodayCount).coerceAtMost(chaptersToAssign.size)
            )

            val currentDate = startDate.plusDays(day.toLong())

            plan.add(
                PlanDay(
                    id = "day-${currentDate.toEpochDay()}",
                    planId = planId,
                    date = currentDate,
                    displayDate = currentDate.format(displayDateFormatter),
                    chapters = chaptersToday,
                    readingString = formatReadingString(chaptersToday),
                    completed = false
                )
            )

            chapterIndex += chaptersTodayCount
        }

        return plan
    }

    fun redistributePlan(
        planDays: List<PlanDay>,
        today: LocalDate,
        originalEndDate: LocalDate
    ): List<PlanDay> {
        if (planDays.isEmpty()) return emptyList()

        val endDate = if (today.isAfter(originalEndDate)) today.plusMonths(6) else originalEndDate

        val completedHistory = planDays.filter { it.completed }
        val unreadChapters = planDays.filter { !it.completed }.flatMap { it.chapters }

        if (unreadChapters.isEmpty()) return planDays

        val newFuturePlan = generatePlan(
            planId = planDays.first().planId,
            startDate = today,
            endDate = endDate,
            chaptersToAssign = unreadChapters
        )

        return (completedHistory + newFuturePlan).sortedBy { it.date }
    }

    fun formatReadingString(chapters: List<String>): String {
        if (chapters.isEmpty()) return ""
        if (chapters.size == 1) return chapters[0]

        fun splitRef(ref: String): Pair<String, String> {
            val parts = ref.split(" ")
            val chap = parts.last()
            val book = parts.dropLast(1).joinToString(" ")
            return Pair(book, chap)
        }

        val (firstBook, firstChap) = splitRef(chapters.first())
        val (lastBook, lastChap) = splitRef(chapters.last())

        return if (firstBook == lastBook) {
            "$firstBook $firstChap–$lastChap"
        } else {
            "${chapters.first()} to ${chapters.last()}"
        }
    }

    fun calculateStreak(planDays: List<PlanDay>, today: LocalDate = LocalDate.now()): Int {
        if (planDays.isEmpty()) return 0

        val sortedDays = planDays.sortedBy { it.date }
        var streak = 0
        var checkDate = today

        for (i in sortedDays.indices.reversed()) {
            val day = sortedDays[i]
            if (day.completed) {
                when {
                    day.date == checkDate -> {
                        streak++
                        checkDate = checkDate.minusDays(1)
                    }
                    streak == 0 && day.date == today -> {
                        streak++
                        checkDate = today.minusDays(1)
                    }
                    streak == 0 && day.date == today.minusDays(1) -> {
                        streak++
                        checkDate = today.minusDays(2)
                    }
                    streak > 0 && day.date == checkDate -> {
                        streak++
                        checkDate = checkDate.minusDays(1)
                    }
                }
            } else if (day.date.isBefore(today)) {
                if (streak > 0) break
            }
        }
        return streak
    }
}
