package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.MatchEntry

data class MatchHistoryData(
    val matches: List<MatchEntry>,
    val winRate: Float,
    val totalEarnings: Int,
)

interface MatchHistoryRepository {
    suspend fun getHistory(): Result<MatchHistoryData>
}
