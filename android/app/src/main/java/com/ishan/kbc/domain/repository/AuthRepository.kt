package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, username: String, password: String, displayName: String?): Result<AuthUser>
    suspend fun login(emailOrUsername: String, password: String): Result<AuthUser>
    suspend fun logout()
    val currentUser: Flow<AuthUser?>
    val isAuthenticated: Flow<Boolean>
}
