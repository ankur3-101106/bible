package com.sanctuary.bible.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.repository.PlanRepository
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class PlanScope(val title: String, val chapters: List<String>) {
    COMPLETE_BIBLE("Complete the Bible", PlanningEngine.ALL_CHAPTERS_FLAT),
    OLD_TESTAMENT("Old Testament", PlanningEngine.ALL_CHAPTERS_FLAT.subList(0, 929)),
    NEW_TESTAMENT("New Testament", PlanningEngine.ALL_CHAPTERS_FLAT.subList(929, 1189)),
    GOSPELS("Gospels & Acts", PlanningEngine.ALL_CHAPTERS_FLAT.subList(929, 1046))
}

data class OnboardingUiState(
    val selectedScope: PlanScope = PlanScope.COMPLETE_BIBLE,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(364),
    val totalChapters: Int = 1189,
    val dailyPaceAverage: Double = 3.26,
    val isGenerating: Boolean = false,
    val planCreated: Boolean = false
)

class OnboardingViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectScope(scope: PlanScope) {
        _uiState.update { state ->
            val total = scope.chapters.size
            val days = java.time.temporal.ChronoUnit.DAYS.between(state.startDate, state.endDate).toInt() + 1
            val pace = if (days > 0) total.toDouble() / days else total.toDouble()
            state.copy(
                selectedScope = scope,
                totalChapters = total,
                dailyPaceAverage = pace
            )
        }
    }

    fun updateDates(startDate: LocalDate, endDate: LocalDate) {
        _uiState.update { state ->
            val total = state.selectedScope.chapters.size
            val days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            val pace = if (days > 0) total.toDouble() / days else total.toDouble()
            state.copy(
                startDate = startDate,
                endDate = endDate,
                dailyPaceAverage = pace
            )
        }
    }

    fun generatePlan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            val state = _uiState.value
            planRepository.createPlan(
                startDate = state.startDate,
                endDate = state.endDate,
                title = state.selectedScope.title,
                chapters = state.selectedScope.chapters
            )
            _uiState.update { it.copy(isGenerating = false, planCreated = true) }
        }
    }

    class Factory(
        private val planRepository: PlanRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(planRepository) as T
        }
    }
}
