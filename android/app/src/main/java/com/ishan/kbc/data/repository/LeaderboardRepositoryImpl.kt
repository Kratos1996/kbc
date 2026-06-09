package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.domain.model.LeaderboardEntry
import com.ishan.kbc.domain.repository.LeaderboardRepository
import com.ishan.kbc.domain.repository.LeaderboardScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : LeaderboardRepository {

    override suspend fun fetch(scope: LeaderboardScope): Result<List<LeaderboardEntry>> = runCatching {
        api.leaderboard(scope = scope.apiValue).map {
            LeaderboardEntry(
                rank = it.rank,
                userId = it.userId,
                username = it.username,
                displayName = it.displayName,
                avatarUrl = it.avatarUrl,
                totalScore = it.totalScore,
                bestScore = it.bestScore,
            )
        }
    }
}
