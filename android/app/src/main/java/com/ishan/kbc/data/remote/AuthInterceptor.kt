package com.ishan.kbc.data.remote

import com.ishan.kbc.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val prefs: PreferencesManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Skip auth header for auth endpoints
        val path = request.url.encodedPath
        if (path.contains("/auth/")) return chain.proceed(request)
        val token = runBlocking { prefs.authToken.first() }
        return if (token.isNullOrBlank()) {
            chain.proceed(request)
        } else {
            chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
        }
    }
}
