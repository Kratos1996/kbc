package com.ishan.kbc.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val emailOrUsername: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onEmailOrUsername(v: String) = _state.update { it.copy(emailOrUsername = v, error = null) }
    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onUsername(v: String) = _state.update { it.copy(username = v, error = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onDisplayName(v: String) = _state.update { it.copy(displayName = v, error = null) }

    fun login() {
        val s = _state.value
        if (s.emailOrUsername.isBlank() || s.password.length < 8) {
            _state.update { it.copy(error = "Enter valid credentials") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.login(s.emailOrUsername.trim(), s.password)
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Login failed") } }
        }
    }

    fun register() {
        val s = _state.value
        if (s.email.isBlank() || s.username.isBlank() || s.password.length < 8) {
            _state.update { it.copy(error = "Fill all fields (password ≥ 8)") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.register(s.email.trim(), s.username.trim(), s.password, s.displayName.takeIf { it.isNotBlank() })
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") } }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun consumeSuccess() = _state.update { it.copy(success = false) }
}
