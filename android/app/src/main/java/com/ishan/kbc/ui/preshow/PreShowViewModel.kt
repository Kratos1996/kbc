package com.ishan.kbc.ui.preshow

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PreShowState(
    val secondsRemaining: Int = 342,
    val prizePool: Double = 254890.0,
    val prizeProgress: Float = 0.74f,
    val contestants: List<Contestant> = defaultContestants,
)

data class Contestant(
    val name: String,
    val badge: String,
    val description: String,
    val tier: String,
    val division: String,
)

private val defaultContestants = listOf(
    Contestant("Grandmaster_X", "ELITE", "Winstreak: 5", "ELITE", "D-III"),
    Contestant("Neon_Cipher", "LEGACY", "Top 1% Logic", "LEGACY", "D-I"),
    Contestant("OldMaster7", "PRO", "Veteran Badge", "PRO", "P-IV"),
)

@HiltViewModel
class PreShowViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PreShowState())
    val state: StateFlow<PreShowState> = _state.asStateFlow()

    private var timer: CountDownTimer? = null
    private var prizeTimer: CountDownTimer? = null

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        prizeTimer?.cancel()
    }

    fun start() {
        timer?.cancel()
        timer = object : CountDownTimer((_state.value.secondsRemaining * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _state.update { it.copy(secondsRemaining = (millisUntilFinished / 1000).toInt()) }
            }
            override fun onFinish() {
                _state.update { it.copy(secondsRemaining = 0) }
            }
        }.start()

        prizeTimer?.cancel()
        prizeTimer = object : CountDownTimer(Long.MAX_VALUE, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                _state.update {
                    val newProgress = (it.prizeProgress + 0.005f).coerceAtMost(1f)
                    it.copy(
                        prizeProgress = newProgress,
                        prizePool = it.prizePool + 1250.0,
                    )
                }
            }
            override fun onFinish() {}
        }.start()
    }
}
