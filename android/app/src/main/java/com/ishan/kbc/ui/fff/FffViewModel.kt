package com.ishan.kbc.ui.fff

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.audio.SoundManager
import com.ishan.kbc.domain.model.FffItem
import com.ishan.kbc.domain.model.FffQuestion
import com.ishan.kbc.domain.model.FffResult
import com.ishan.kbc.domain.usecase.FffUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FffUiState(
    val question: FffQuestion? = null,
    val selectedOrder: List<Int> = emptyList(),
    val timeRemaining: Int = 30,
    val result: FffResult? = null,
    val finished: Boolean = false,
    val loading: Boolean = true,
)

@HiltViewModel
class FffViewModel @Inject constructor(
    private val soundManager: SoundManager,
    private val fffUseCase: FffUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FffUiState())
    val state: StateFlow<FffUiState> = _state.asStateFlow()

    private var timer: CountDownTimer? = null

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    fun start() {
        viewModelScope.launch {
            fffUseCase.getRandomQuestion()
                .onSuccess { q ->
                    _state.value = FffUiState(question = q, timeRemaining = q.timeLimitSec, loading = false)
                    startTimer(q.timeLimitSec)
                }
        }
    }

    fun selectItem(itemId: Int) {
        val s = _state.value
        if (s.result != null || s.finished) return
        if (itemId in s.selectedOrder) {
            _state.update { it.copy(selectedOrder = it.selectedOrder.filter { id -> id != itemId }) }
        } else if (s.selectedOrder.size < 4) {
            soundManager.playLock()
            _state.update { it.copy(selectedOrder = it.selectedOrder + itemId) }
        }
    }

    fun submit() {
        val s = _state.value
        val q = s.question ?: return
        if (s.selectedOrder.size != 4) return
        timer?.cancel()
        val elapsed = (q.timeLimitSec - s.timeRemaining).coerceAtLeast(0)
        val correct = s.selectedOrder == q.correctOrder
        val result = FffResult(
            correct = correct,
            correctOrder = q.correctOrder,
            userOrder = s.selectedOrder,
            timeTaken = elapsed,
        )
        if (correct) soundManager.playCorrect() else soundManager.playWrong()
        _state.update { it.copy(result = result, finished = true, timeRemaining = 0) }
    }

    fun retry() {
        timer?.cancel()
        start()
    }

    private fun startTimer(limitSec: Int) {
        timer?.cancel()
        timer = object : CountDownTimer((limitSec * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _state.update { it.copy(timeRemaining = (millisUntilFinished / 1000).toInt()) }
            }
            override fun onFinish() {
                soundManager.playWrong()
                val q = _state.value.question ?: return
                val result = FffResult(
                    correct = false, correctOrder = q.correctOrder,
                    userOrder = _state.value.selectedOrder, timeTaken = q.timeLimitSec,
                )
                _state.update { it.copy(result = result, finished = true, timeRemaining = 0) }
            }
        }.start()
    }
}
