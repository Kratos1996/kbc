package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.Achievement
import com.ishan.kbc.domain.model.Milestone

data class AchievementData(
    val achievements: List<Achievement>,
    val milestones: List<Milestone>,
    val totalMastery: Float,
)

interface AchievementRepository {
    suspend fun getAchievements(): Result<AchievementData>
}
