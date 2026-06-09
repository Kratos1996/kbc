package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.UpdateProfileRequest
import com.ishan.kbc.data.remote.dto.UserProfileDto
import com.ishan.kbc.data.remote.dto.UserStatsDto
import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats
import com.ishan.kbc.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : ProfileRepository {

    override suspend fun me(): Result<UserProfile> = runCatching {
        api.userProfile().toDomain()
    }

    override suspend fun stats(): Result<UserStats> = runCatching {
        api.userStats().toDomain()
    }

    override suspend fun update(displayName: String?, avatarUrl: String?): Result<UserProfile> = runCatching {
        api.updateProfile(UpdateProfileRequest(displayName = displayName, avatarUrl = avatarUrl)).toDomain()
    }
}

private fun UserProfileDto.toDomain() = UserProfile(
    id = id,
    email = email,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    coins = coins,
    isPremium = isPremium,
    premiumUntil = premiumUntil,
    createdAt = createdAt,
)

private fun UserStatsDto.toDomain() = UserStats(
    gamesPlayed = gamesPlayed,
    gamesWon = gamesWon,
    bestScore = bestScore,
    totalScore = totalScore,
)
