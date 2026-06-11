package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats
import com.ishan.kbc.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend fun me(): Result<UserProfile> = profileRepository.me()
    suspend fun stats(): Result<UserStats> = profileRepository.stats()
    suspend fun update(displayName: String?, avatarUrl: String?): Result<UserProfile> =
        profileRepository.update(displayName, avatarUrl)
}
