package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.LeaderboardEntry
import com.ishan.kbc.domain.repository.LeaderboardRepository
import com.ishan.kbc.domain.repository.LeaderboardScope
import javax.inject.Inject

class LeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
) {
    suspend fun fetch(scope: LeaderboardScope): Result<List<LeaderboardEntry>> =
        leaderboardRepository.fetch(scope)
}
