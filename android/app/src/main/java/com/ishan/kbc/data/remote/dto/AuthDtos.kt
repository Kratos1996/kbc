package com.ishan.kbc.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    @Json(name = "email") val email: String? = null,
    @Json(name = "username") val username: String? = null,
    @Json(name = "emailOrUsername") val emailOrUsername: String? = null,
    @Json(name = "password") val password: String,
    @Json(name = "displayName") val displayName: String? = null,
)

@JsonClass(generateAdapter = true)
data class AuthUserDto(
    val id: String,
    val email: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val coins: Int,
    val isPremium: Boolean,
    val premiumUntil: String?,
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val user: AuthUserDto,
    val accessToken: String,
    val refreshToken: String,
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(val refreshToken: String)

@JsonClass(generateAdapter = true)
data class ErrorBody(val error: ErrorPayload?)

@JsonClass(generateAdapter = true)
data class ErrorPayload(val code: String, val message: String)
