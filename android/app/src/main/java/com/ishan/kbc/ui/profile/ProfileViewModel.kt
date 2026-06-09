package com.ishan.kbc.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats
import com.ishan.kbc.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val profile: UserProfile? = null,
    val stats: UserStats? = null,
    val editing: Boolean = false,
    val draftDisplayName: String = "",
    val draftAvatarUrl: String = "",
    val saving: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val (p, s) = coroutineScope {
                    val pa = async { repository.me() }
                    val sa = async { repository.stats() }
                    pa.await() to sa.await()
                }
                p.onSuccess { prof ->
                    s.onSuccess { st ->
                        _state.update { it.copy(loading = false, profile = prof, stats = st) }
                    }.onFailure { e -> _state.update { it.copy(loading = false, profile = prof, error = e.message) } }
                }.onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun beginEdit() {
        val p = _state.value.profile ?: return
        _state.update {
            it.copy(
                editing = true,
                draftDisplayName = p.displayName.orEmpty(),
                draftAvatarUrl = p.avatarUrl.orEmpty(),
                message = null,
            )
        }
    }

    fun cancelEdit() = _state.update { it.copy(editing = false, message = null) }

    fun onDisplayNameChange(v: String) = _state.update { it.copy(draftDisplayName = v) }
    fun onAvatarChange(v: String) = _state.update { it.copy(draftAvatarUrl = v) }

    fun save() {
        if (_state.value.saving) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            repository.update(
                displayName = _state.value.draftDisplayName.takeIf { it.isNotBlank() },
                avatarUrl = _state.value.draftAvatarUrl.takeIf { it.isNotBlank() },
            )
                .onSuccess { prof ->
                    _state.update {
                        it.copy(
                            saving = false,
                            editing = false,
                            profile = prof,
                            message = "OK",
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(saving = false, error = e.message) } }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
