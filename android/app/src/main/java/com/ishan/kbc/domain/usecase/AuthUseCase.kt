package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.AuthUser
import com.ishan.kbc.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend fun login(emailOrUsername: String, password: String): Result<AuthUser> =
        authRepository.login(emailOrUsername, password)

    suspend fun register(email: String, username: String, password: String, displayName: String?): Result<AuthUser> =
        authRepository.register(email, username, password, displayName)

    suspend fun logout() = authRepository.logout()
}
