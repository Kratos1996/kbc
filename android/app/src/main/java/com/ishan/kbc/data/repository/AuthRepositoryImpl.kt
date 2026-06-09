package com.ishan.kbc.data.repository

import com.ishan.kbc.data.local.PreferencesManager
import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.AuthRequest
import com.ishan.kbc.domain.model.AuthUser
import com.ishan.kbc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: KbcApi,
    private val prefs: PreferencesManager,
) : AuthRepository {

    override suspend fun register(
        email: String, username: String, password: String, displayName: String?,
    ): Result<AuthUser> = runCatching {
        val resp = api.register(AuthRequest(email = email, username = username, password = password, displayName = displayName))
        prefs.saveSession(resp.accessToken, resp.refreshToken, resp.user.username, resp.user.displayName)
        resp.user.toDomain()
    }

    override suspend fun login(emailOrUsername: String, password: String): Result<AuthUser> = runCatching {
        val resp = api.login(AuthRequest(emailOrUsername = emailOrUsername, password = password))
        prefs.saveSession(resp.accessToken, resp.refreshToken, resp.user.username, resp.user.displayName)
        resp.user.toDomain()
    }

    override suspend fun logout() {
        runCatching { api.logout(com.ishan.kbc.data.remote.dto.RefreshRequest("")) }
        prefs.clear()
    }

    override val currentUser: Flow<AuthUser?> = combine(prefs.username, prefs.displayName) { u, d -> u?.let { AuthUser(id = "", email = "", username = it, displayName = d, avatarUrl = null, coins = 0, isPremium = false, premiumUntil = null) } }
    override val isAuthenticated: Flow<Boolean> = prefs.authToken.map { !it.isNullOrBlank() }
}

private fun com.ishan.kbc.data.remote.dto.AuthUserDto.toDomain() = AuthUser(
    id = id, email = email, username = username, displayName = displayName, avatarUrl = avatarUrl,
    coins = coins, isPremium = isPremium, premiumUntil = premiumUntil,
)
