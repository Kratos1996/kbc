package com.ishan.kbc.ui.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.analytics.AnalyticsManager
import com.ishan.kbc.domain.model.DailySubmitResult
import com.ishan.kbc.domain.model.Question
import com.ishan.kbc.domain.usecase.DailyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val alreadySubmitted: Boolean = false,
    val previousScore: Int? = null,
    val dailyId: String? = null,
    val bonusCoins: Int = 0,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, Int> = emptyMap(),
    val submitting: Boolean = false,
    val result: DailySubmitResult? = null,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isFinished: Boolean get() = currentIndex >= questions.size && questions.isNotEmpty()
}

@HiltViewModel
class DailyViewModel @Inject constructor(
    private val dailyUseCase: DailyUseCase,
    private val analytics: AnalyticsManager,
) : ViewModel() {

    private val _state = MutableStateFlow(DailyUiState())
    val state: StateFlow<DailyUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true, error = null, result = null) }
        viewModelScope.launch {
            dailyUseCase.submissionToday()
                .onSuccess { sub ->
                    if (sub != null) {
                        _state.update {
                            it.copy(
                                loading = false,
                                alreadySubmitted = true,
                                previousScore = sub.score,
                            )
                        }
                        return@launch
                    }
                    dailyUseCase.today()
                        .onSuccess { challenge ->
                            _state.update {
                                it.copy(
                                    loading = false,
                                    dailyId = challenge.id,
                                    bonusCoins = challenge.bonusCoins,
                                    questions = challenge.questions,
                                    currentIndex = 0,
                                    answers = emptyMap(),
                                )
                            }
                        }
                        .onFailure { e ->
                            _state.update { it.copy(loading = false, error = e.message) }
                        }
                }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun selectOption(index: Int) {
        val s = _state.value
        val q = s.currentQuestion ?: return
        if (s.answers.containsKey(q.id)) return
        _state.update { it.copy(answers = it.answers + (q.id to index)) }
    }

    fun next() {
        val s = _state.value
        if (s.currentIndex < s.questions.size - 1) {
            _state.update { it.copy(currentIndex = it.currentIndex + 1) }
        } else {
            submit()
        }
    }

    fun submit() {
        val s = _state.value
        if (s.submitting || s.questions.isEmpty()) return
        val dailyId = s.dailyId ?: return
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, error = null) }
            dailyUseCase.submit(dailyId, s.answers.toList())
                .onSuccess { res ->
                    analytics.dailyCompleted(res.score)
                    _state.update {
                        it.copy(
                            submitting = false,
                            result = res,
                            currentIndex = s.questions.size,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(submitting = false, error = e.message) } }
        }
    }
}
