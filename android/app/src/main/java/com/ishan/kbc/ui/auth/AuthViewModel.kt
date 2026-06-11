package com.ishan.kbc.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthTab { Login, SignUp }

data class AuthUiState(
    val tab: AuthTab = AuthTab.Login,
    val emailOrUsername: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isPasswordVisible: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun setTab(tab: AuthTab) = _state.update { it.copy(tab = tab, error = null) }

    fun onEmailOrUsername(v: String) = _state.update { it.copy(emailOrUsername = v, error = null) }
    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onUsername(v: String) = _state.update { it.copy(username = v, error = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onDisplayName(v: String) = _state.update { it.copy(displayName = v, error = null) }

    fun togglePasswordVisibility() = _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun login() {
        val s = _state.value
        if (s.emailOrUsername.isBlank() || s.password.length < 8) {
            _state.update { it.copy(error = "Enter valid credentials") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authUseCase.login(s.emailOrUsername.trim(), s.password)
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
            authUseCase.register(s.email.trim(), s.username.trim(), s.password, s.displayName.takeIf { it.isNotBlank() })
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") } }
        }
    }

    fun loginWithGoogle() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authUseCase.login("google_stub", "google_stub")
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Google sign-in failed") } }
        }
    }

    fun loginWithFacebook() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authUseCase.login("facebook_stub", "facebook_stub")
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Facebook sign-in failed") } }
        }
    }

    fun sendOtp() {
        val s = _state.value
        if (s.email.isBlank() && s.emailOrUsername.isBlank()) {
            _state.update { it.copy(error = "Enter email or phone number") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authUseCase.login(s.email.ifBlank { s.emailOrUsername }.trim(), "otp_stub")
                .onSuccess { _state.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "OTP send failed") } }
        }
    }

    fun forgotPassword() {
        val s = _state.value
        if (s.email.isBlank() && s.emailOrUsername.isBlank()) {
            _state.update { it.copy(error = "Enter your email first") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _state.update { it.copy(isLoading = false, error = "Password reset link sent (stub)") }
        }
    }

    fun logout() {
        viewModelScope.launch { authUseCase.logout() }
    }

    fun consumeSuccess() = _state.update { it.copy(success = false) }
}
