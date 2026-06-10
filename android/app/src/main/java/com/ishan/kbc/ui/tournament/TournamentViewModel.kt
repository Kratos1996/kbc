package com.ishan.kbc.ui.tournament

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TournamentState(
    val days: Int = 2,
    val hours: Int = 14,
    val minutes: Int = 45,
    val seconds: Int = 12,
    val selectedEntry: EntryMethod = EntryMethod.RadiancePoints,
    val isRegistering: Boolean = false,
    val isRegistered: Boolean = false,
)

enum class EntryMethod { RadiancePoints, GoldPass }

@HiltViewModel
class TournamentViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TournamentState())
    val state: StateFlow<TournamentState> = _state.asStateFlow()

    private var timer: CountDownTimer? = null

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    fun startCountdown() {
        timer?.cancel()
        var totalSec = _state.let { it.days * 86400 + it.hours * 3600 + it.minutes * 60 + it.seconds }
        timer = object : CountDownTimer((totalSec * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val rem = millisUntilFinished / 1000
                _state.update {
                    it.copy(
                        days = (rem / 86400).toInt(),
                        hours = ((rem % 86400) / 3600).toInt(),
                        minutes = ((rem % 3600) / 60).toInt(),
                        seconds = (rem % 60).toInt(),
                    )
                }
            }
            override fun onFinish() {}
        }.start()
    }

    fun selectEntry(method: EntryMethod) {
        _state.update { it.copy(selectedEntry = method) }
    }

    fun register() {
        if (_state.value.isRegistered) return
        _state.update { it.copy(isRegistering = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _state.update { it.copy(isRegistering = false, isRegistered = true) }
        }
    }
}
