package com.sanctuary.bible.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.model.Note
import com.sanctuary.bible.data.model.Plan
import com.sanctuary.bible.data.model.PlanDay
import com.sanctuary.bible.data.model.Verse
import com.sanctuary.bible.data.repository.BibleRepository
import com.sanctuary.bible.data.repository.PlanRepository
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class ChapterCheckItem(
    val chapterRef: String,
    val isCompleted: Boolean,
    val isCurrentReading: Boolean,
    val summaryHint: String
)

data class HomeUiState(
    val greeting: String = "Good morning",
    val activePlan: Plan? = null,
    val todayPlanDay: PlanDay? = null,
    val todayChapterItems: List<ChapterCheckItem> = emptyList(),
    val completedChaptersCount: Int = 0,
    val totalChaptersCount: Int = 1189,
    val progressPercentage: Int = 0,
    val daysRemaining: Int = 0,
    val streakDays: Int = 0,
    val lastNote: Note? = null,
    val anchorVerse: Verse? = null,
    val pausedVerseRef: String = "Genesis 44:18 — Judah's intercession"
)

class HomeViewModel(
    private val planRepository: PlanRepository,
    private val bibleRepository: BibleRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            planRepository.ensureDefaultPlanExists()
        }
    }

    private val greetingState = MutableStateFlow(getGreetingByTime())

    val uiState: StateFlow<HomeUiState> = combine(
        planRepository.activePlan,
        planRepository.activePlanDays,
        bibleRepository.latestNote,
        greetingState
    ) { activePlan, planDays, latestNote, greeting ->
        val today = LocalDate.now()
        val todayDay = planDays.find { it.date == today }
            ?: planDays.find { !it.completed && !it.date.isBefore(today) }
            ?: planDays.firstOrNull()

        val totalChaptersInPlan = if (planDays.isNotEmpty()) planDays.sumOf { it.chapters.size } else PlanningEngine.TOTAL_BIBLE_CHAPTERS
        val completedCount = planDays.filter { it.completed }.sumOf { it.chapters.size }
        val progressPercent = if (totalChaptersInPlan > 0) {
            ((completedCount.toDouble() / totalChaptersInPlan) * 100).toInt()
        } else 0

        val daysLeft = if (activePlan != null) {
            ChronoUnit.DAYS.between(today, activePlan.endDate).coerceAtLeast(0).toInt()
        } else 0

        val streak = PlanningEngine.calculateStreak(planDays, today)

        val chapterItems = todayDay?.chapters?.mapIndexed { index, chapterRef ->
            val isComp = todayDay.completed
            val isCurrent = !isComp && index == 0
            ChapterCheckItem(
                chapterRef = chapterRef,
                isCompleted = isComp,
                isCurrentReading = isCurrent,
                summaryHint = getChapterSummaryHint(chapterRef)
            )
        } ?: emptyList()

        HomeUiState(
            greeting = greeting,
            activePlan = activePlan,
            todayPlanDay = todayDay,
            todayChapterItems = chapterItems,
            completedChaptersCount = completedCount,
            totalChaptersCount = totalChaptersInPlan,
            progressPercentage = progressPercent,
            daysRemaining = daysLeft,
            streakDays = streak,
            lastNote = latestNote,
            anchorVerse = Verse(
                bookName = "Genesis",
                chapter = 45,
                verseNumber = 5,
                text = "God sent me before you to preserve life... to save your lives by a great deliverance."
            ),
            pausedVerseRef = todayDay?.chapters?.firstOrNull()?.let { "$it — In progress" }
                ?: "Genesis 44:18 — Judah's intercession"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun toggleChapterCheck(chapterRef: String) {
        val currentDay = uiState.value.todayPlanDay ?: return
        viewModelScope.launch {
            planRepository.toggleDayCompleted(currentDay.id, !currentDay.completed)
        }
    }

    private fun getGreetingByTime(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun getChapterSummaryHint(chapterRef: String): String {
        return when {
            chapterRef.contains("Genesis 42") -> "Joseph's brothers travel to Egypt"
            chapterRef.contains("Genesis 43") -> "The second journey with Benjamin"
            chapterRef.contains("Genesis 44") -> "The silver cup & Judah's plea"
            chapterRef.contains("Genesis 45") -> "Joseph reveals his identity"
            else -> "Daily Scripture Reading"
        }
    }

    class Factory(
        private val planRepository: PlanRepository,
        private val bibleRepository: BibleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(planRepository, bibleRepository) as T
        }
    }
}
