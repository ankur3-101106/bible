package com.sanctuary.bible.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.model.Plan
import com.sanctuary.bible.data.model.PlanDay
import com.sanctuary.bible.data.repository.PlanRepository
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PlanUiState(
    val activePlan: Plan? = null,
    val planDays: List<PlanDay> = emptyList(),
    val completedChapters: Int = 0,
    val totalChapters: Int = 1189,
    val progressPercentage: Int = 0,
    val totalDurationDays: Int = 0,
    val averagePaceChapters: Int = 0,
    val isAheadOfPace: Boolean = true,
    val hasMissedDays: Boolean = false,
    val isRedistributing: Boolean = false,
    val showAdjustDialog: Boolean = false
)

class PlanViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    val uiState: StateFlow<PlanUiState> = combine(
        planRepository.activePlan,
        planRepository.activePlanDays
    ) { plan, days ->
        val today = LocalDate.now()
        val totalChaptersInPlan = if (days.isNotEmpty()) days.sumOf { it.chapters.size } else PlanningEngine.TOTAL_BIBLE_CHAPTERS
        val completedCount = days.filter { it.completed }.sumOf { it.chapters.size }
        val percent = if (totalChaptersInPlan > 0) {
            ((completedCount.toDouble() / totalChaptersInPlan) * 100).toInt()
        } else 0

        val totalDays = days.size
        val pace = if (totalDays > 0) (totalChaptersInPlan / totalDays) else 0

        val missed = days.any { !it.completed && it.date.isBefore(today) }

        PlanUiState(
            activePlan = plan,
            planDays = days,
            completedChapters = completedCount,
            totalChapters = totalChaptersInPlan,
            progressPercentage = percent,
            totalDurationDays = totalDays,
            averagePaceChapters = pace,
            isAheadOfPace = !missed,
            hasMissedDays = missed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanUiState()
    )

    fun toggleDayCompleted(dayId: String, completed: Boolean) {
        viewModelScope.launch {
            planRepository.toggleDayCompleted(dayId, completed)
        }
    }

    fun openAdjustDialog() {
        _dialogState.value = true
    }

    fun closeAdjustDialog() {
        _dialogState.value = false
    }

    private val _dialogState = MutableStateFlow(false)
    val showAdjustDialog: StateFlow<Boolean> = _dialogState

    fun redistributePlan() {
        viewModelScope.launch {
            closeAdjustDialog()
            planRepository.redistributeActivePlan()
        }
    }

    class Factory(
        private val planRepository: PlanRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlanViewModel(planRepository) as T
        }
    }
}
