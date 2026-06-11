package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.repository.AchievementData
import com.ishan.kbc.domain.repository.AchievementRepository
import javax.inject.Inject

class AchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
) {
    suspend fun getAchievements(): Result<AchievementData> = achievementRepository.getAchievements()
}
