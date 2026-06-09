package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.LeaderboardEntry

enum class LeaderboardScope(val apiValue: String, val label: String) {
    Global("global", "Global"),
    Weekly("weekly", "Weekly"),
    Monthly("monthly", "Monthly"),
    Friends("friends", "Friends"),
}

interface LeaderboardRepository {
    suspend fun fetch(scope: LeaderboardScope): Result<List<LeaderboardEntry>>
}
