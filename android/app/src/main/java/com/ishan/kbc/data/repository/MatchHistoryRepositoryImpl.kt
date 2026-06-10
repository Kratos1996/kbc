package com.ishan.kbc.data.repository

import com.ishan.kbc.domain.model.MatchEntry
import com.ishan.kbc.domain.repository.MatchHistoryData
import com.ishan.kbc.domain.repository.MatchHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchHistoryRepositoryImpl @Inject constructor() : MatchHistoryRepository {

    override suspend fun getHistory(): Result<MatchHistoryData> {
        return Result.success(
            MatchHistoryData(
                matches = listOf(
                    MatchEntry("RA-8821", "Oct 24, 2023", "Space & Science", 10000, true, badge = "Victory"),
                    MatchEntry("RA-8790", "Oct 22, 2023", "World History", 2500, true, badge = "Victory"),
                    MatchEntry("RA-8755", "Oct 20, 2023", "Pop Culture", 0, false, badge = "Eliminated"),
                    MatchEntry("RA-8702", "Oct 15, 2023", "Grand Championship Finale", 50000, true,
                        isHighlight = true,
                        description = "You reached the final round in the \"Universal Knowledge\" tier. A legendary performance.",
                        badge = "Top 1%",
                    ),
                    MatchEntry("RA-8654", "Oct 12, 2023", "Literature & Arts", 5000, true, badge = "Victory"),
                    MatchEntry("RA-8601", "Oct 08, 2023", "Sports & Entertainment", 0, false, badge = "Eliminated"),
                ),
                winRate = 0.68f,
                totalEarnings = 72500,
            ),
        )
    }
}
