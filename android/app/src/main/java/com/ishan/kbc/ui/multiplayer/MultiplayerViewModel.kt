package com.ishan.kbc.ui.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.model.MpMatch
import com.ishan.kbc.domain.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MpTab { Create, Join }

data class MultiplayerUiState(
    val tab: MpTab = MpTab.Create,
    val loading: Boolean = false,
    val error: String? = null,
    val createdMatch: MpMatch? = null,
    val joinedMatch: MpMatch? = null,
    val joinCode: String = "",
)

@HiltViewModel
class MultiplayerViewModel @Inject constructor(
    private val repository: MultiplayerRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MultiplayerUiState())
    val state: StateFlow<MultiplayerUiState> = _state.asStateFlow()

    fun setTab(tab: MpTab) {
        _state.update { it.copy(tab = tab, error = null) }
    }

    fun onJoinCodeChanged(v: String) {
        _state.update { it.copy(joinCode = v.trim().uppercase(), error = null) }
    }

    fun create() {
        if (_state.value.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            repository.createAsync()
                .onSuccess { match -> _state.update { it.copy(loading = false, createdMatch = match) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun join() {
        val code = _state.value.joinCode
        if (code.isBlank() || _state.value.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            repository.joinAsync(code)
                .onSuccess { match -> _state.update { it.copy(loading = false, joinedMatch = match) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }
}
