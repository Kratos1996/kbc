package com.ishan.kbc.data.remote

import com.ishan.kbc.data.local.PreferencesManager
import com.ishan.kbc.data.remote.dto.RefreshRequest
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val prefs: PreferencesManager,
    private val apiProvider: Provider<KbcApi>,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null
        val refresh = runBlocking { prefs.refreshToken.first() } ?: return null
        return try {
            val newAuth = runBlocking { apiProvider.get().refresh(RefreshRequest(refresh)) }
            runBlocking {
                prefs.saveSession(
                    accessToken = newAuth.accessToken,
                    refreshToken = newAuth.refreshToken,
                    username = newAuth.user.username,
                    displayName = newAuth.user.displayName,
                )
            }
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newAuth.accessToken}")
                .build()
        } catch (e: Exception) {
            runBlocking { prefs.clear() }
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response.priorResponse
        var count = 1
        while (r != null) { count++; r = r.priorResponse }
        return count
    }
}
