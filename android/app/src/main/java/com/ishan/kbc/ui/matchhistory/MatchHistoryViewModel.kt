package com.ishan.kbc.ui.matchhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.model.MatchEntry
import com.ishan.kbc.domain.usecase.MatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchHistoryState(
    val matches: List<MatchEntry> = emptyList(),
    val winRate: Float = 0f,
    val totalEarnings: Int = 0,
    val loading: Boolean = true,
)

@HiltViewModel
class MatchHistoryViewModel @Inject constructor(
    private val matchHistoryUseCase: MatchHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MatchHistoryState())
    val state: StateFlow<MatchHistoryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            matchHistoryUseCase.getHistory()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            matches = data.matches,
                            winRate = data.winRate,
                            totalEarnings = data.totalEarnings,
                            loading = false,
                        )
                    }
                }
        }
    }
}
