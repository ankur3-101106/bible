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
        planRepository.completedChapterRefs,
        timeframeState
    ) { days, completedSet, timeframe ->
        val today = LocalDate.now()
        val totalChaptersInPlan = if (days.isNotEmpty()) days.sumOf { it.chapters.size } else PlanningEngine.TOTAL_BIBLE_CHAPTERS

        val filteredDays = when (timeframe) {
            ProgressTimeframe.WEEK -> days.filter { !it.date.isBefore(today.minusDays(6)) && !it.date.isAfter(today) }
            ProgressTimeframe.MONTH -> days.filter { it.date.month == today.month && it.date.year == today.year }
            ProgressTimeframe.ALL_TIME -> days
        }

        val completedCount = filteredDays.flatMap { it.chapters }.count { completedSet.contains(it) }

        val percent = if (totalChaptersInPlan > 0) {
            ((completedCount.toDouble() / totalChaptersInPlan) * 100).toInt()
        } else 0

        val currentStreak = PlanningEngine.calculateStreak(days, today)
        val longestStreak = PlanningEngine.calculateLongestStreak(days)

        val booksRead = completedSet
            .map { it.split(" ").dropLast(1).joinToString(" ") }
            .distinct()
            .filter { it.isNotBlank() }
            .size

        val monthlyDays = days.filter { it.completed && it.date.month == today.month && it.date.year == today.year }

        ProgressUiState(
            selectedTimeframe = timeframe,
            completedChapters = completedCount,
            totalChapters = totalChaptersInPlan,
            overallPercentage = percent,
            booksReadCount = booksRead,
            totalReadingDays = days.count { it.completed },
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            monthlyActiveDays = monthlyDays.size,
            monthlyChapters = monthlyDays.flatMap { it.chapters }.count { completedSet.contains(it) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
