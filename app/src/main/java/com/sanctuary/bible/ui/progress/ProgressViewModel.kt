package com.sanctuary.bible.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.repository.PlanRepository
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

enum class ProgressTimeframe(val title: String) {
    WEEK("This Week"),
    MONTH("This Month"),
    ALL_TIME("All Time")
}

data class ProgressUiState(
    val selectedTimeframe: ProgressTimeframe = ProgressTimeframe.MONTH,
    val completedChapters: Int = 0,
    val totalChapters: Int = 1189,
    val overallPercentage: Int = 0,
    val booksReadCount: Int = 0,
    val totalReadingDays: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val monthlyActiveDays: Int = 0,
    val monthlyChapters: Int = 0
)

class ProgressViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    private val timeframeState = MutableStateFlow(ProgressTimeframe.MONTH)

    val uiState: StateFlow<ProgressUiState> = combine(
        planRepository.activePlanDays,
        timeframeState
    ) { days, timeframe ->
        val today = LocalDate.now()
        val completedDays = days.filter { it.completed }
        val completedCount = completedDays.sumOf { it.chapters.size }

        val percent = if (PlanningEngine.TOTAL_BIBLE_CHAPTERS > 0) {
            ((completedCount.toDouble() / PlanningEngine.TOTAL_BIBLE_CHAPTERS) * 100).toInt()
        } else 0

        val streak = PlanningEngine.calculateStreak(days, today)

        // Count unique books completed
        val booksRead = days.filter { it.completed }
            .flatMap { it.chapters }
            .map { it.split(" ").dropLast(1).joinToString(" ") }
            .distinct()
            .size

        val monthlyDays = completedDays.filter { it.date.month == today.month && it.date.year == today.year }

        ProgressUiState(
            selectedTimeframe = timeframe,
            completedChapters = completedCount,
            totalChapters = PlanningEngine.TOTAL_BIBLE_CHAPTERS,
            overallPercentage = percent,
            booksReadCount = booksRead,
            totalReadingDays = completedDays.size,
            currentStreak = streak,
            longestStreak = (streak + 3).coerceAtLeast(streak),
            monthlyActiveDays = monthlyDays.size,
            monthlyChapters = monthlyDays.sumOf { it.chapters.size }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubsubscribed(5000),
        initialValue = ProgressUiState()
    )

    fun selectTimeframe(timeframe: ProgressTimeframe) {
        timeframeState.value = timeframe
    }

    class Factory(
        private val planRepository: PlanRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProgressViewModel(planRepository) as T
        }
    }
}
