package com.ishan.kbc.data.repository

import com.ishan.kbc.domain.model.Achievement
import com.ishan.kbc.domain.model.Milestone
import com.ishan.kbc.domain.repository.AchievementData
import com.ishan.kbc.domain.repository.AchievementRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor() : AchievementRepository {

    override suspend fun getAchievements(): Result<AchievementData> {
        return Result.success(
            AchievementData(
                achievements = listOf(
                    Achievement("fff", "Fastest Finger", "Answer 10 questions in under 3 seconds each.", "flash_on", true, "Oct 12, 2023", "Top 3.2%"),
                    Achievement("millionaire", "Millionaire", "Reach the top level of the ladder in a single session.", "payments", false),
                    Achievement("survivor", "Survivor", "Complete a game with zero lifelines remaining.", "shield", true, "Nov 04, 2023", "Top 8.1%"),
                    Achievement("scholar", "Scholar", "Answer 500 questions correctly across all game modes.", "auto_stories", true, "Dec 22, 2023", "Top 5.7%"),
                    Achievement("high_roller", "High Roller", "Win 1,000,000 coins in total.", "casino", false),
                    Achievement("grandmaster", "Grandmaster", "Reach Rank #1 on the Seasonal Ladder.", "military_tech", false),
                    Achievement("electrician", "Electrician", "Use the Bolt Lifeline 10 times in a row.", "electric_bolt", true, "Jan 15, 2024", "Top 12.3%"),
                    Achievement("prestige", "Prestige I", "Achieve Prestige Rank I.", "workspace_premium", false),
                    Achievement("world_traveler", "World Traveler", "Answer 50 geography questions correctly.", "public", true, "Feb 10, 2024", "Top 15.0%"),
                    Achievement("tournament_hero", "Tournament Hero", "Win 3 tournament events.", "emoji_events", false),
                ),
                milestones = listOf(
                    Milestone("Grand Slam", "Achieved by winning 5 consecutive games without using any lifelines.", "LEGENDARY", "primary", "+500 XP", "2h ago"),
                    Milestone("Unstoppable", "Reached level 15 on the ladder for the third time this week.", "ELITE", "gold", "Gold Skin", "1d ago"),
                    Milestone("History Buff", "Answered 20 history-related questions correctly in a row.", "UNCOMMON", "tertiary", "Badge Title", "3d ago"),
                ),
                totalMastery = 0.82f,
            ),
        )
    }
}
