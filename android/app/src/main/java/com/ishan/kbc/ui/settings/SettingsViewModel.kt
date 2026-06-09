package com.ishan.kbc.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val signOutRequested: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                prefs.soundEnabled,
                prefs.musicEnabled,
                prefs.hapticsEnabled,
                prefs.notificationsEnabled,
            ) { s, m, h, n -> SettingsUiState(s, m, h, n) }
                .collect { latest -> _state.value = latest }
        }
    }

    fun setSound(v: Boolean) { _state.update { it.copy(soundEnabled = v) }; viewModelScope.launch { prefs.setSoundEnabled(v) } }
    fun setMusic(v: Boolean) { _state.update { it.copy(musicEnabled = v) }; viewModelScope.launch { prefs.setMusicEnabled(v) } }
    fun setHaptics(v: Boolean) { _state.update { it.copy(hapticsEnabled = v) }; viewModelScope.launch { prefs.setHapticsEnabled(v) } }
    fun setNotifications(v: Boolean) { _state.update { it.copy(notificationsEnabled = v) }; viewModelScope.launch { prefs.setNotificationsEnabled(v) } }

    fun requestSignOut() = _state.update { it.copy(signOutRequested = true) }
    fun consumeSignOut() = _state.update { it.copy(signOutRequested = false) }
}
