package com.ishan.kbc.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.model.LeaderboardEntry
import com.ishan.kbc.domain.repository.LeaderboardRepository
import com.ishan.kbc.domain.repository.LeaderboardScope
import com.ishan.kbc.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val scope: LeaderboardScope = LeaderboardScope.Global,
    val loading: Boolean = false,
    val error: String? = null,
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String? = null,
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardUiState())
    val state: StateFlow<LeaderboardUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { profileRepository.me().onSuccess { _state.update { s -> s.copy(currentUserId = it.id) } } }
        load(LeaderboardScope.Global)
    }

    fun load(scope: LeaderboardScope) {
        _state.update { it.copy(scope = scope, loading = true, error = null) }
        viewModelScope.launch {
            leaderboardRepository.fetch(scope)
                .onSuccess { entries -> _state.update { it.copy(loading = false, entries = entries) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }
}
