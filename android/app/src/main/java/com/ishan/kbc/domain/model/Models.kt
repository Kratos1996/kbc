package com.ishan.kbc.domain.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val difficulty: Int,
    val categoryId: String,
    val categoryName: String,
    val correctOption: Int? = null,
    val explanation: String? = null,
)

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val icon: String?,
    val sortOrder: Int,
)

enum class GameStatus { InProgress, Won, Lost, Quit }

data class GameState(
    val gameId: String? = null,
    val level: Int = 1,
    val total: Int = 15,
    val prize: Int = 0,
    val safeZone: Boolean = false,
    val question: Question? = null,
    val status: GameStatus = GameStatus.InProgress,
    val score: Int = 0,
    val lastAnswerCorrect: Boolean? = null,
    val revealedCorrectOption: Int? = null,
    val lifelinesRemaining: Set<LifelineType> = LifelineType.values().toSet(),
    val eliminatedOptions: Set<Int> = emptySet(),
    val audiencePoll: Map<Int, Int>? = null,
    val expertAnswer: Int? = null,
    val phoneAFriendAnswer: Int? = null,
)

enum class LifelineType(val apiValue: String) {
    FiftyFifty("fifty_fifty"),
    Audience("audience"),
    Phone("phone"),
    Expert("expert"),
    Flip("flip"),
}

data class AuthUser(
    val id: String,
    val email: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val coins: Int,
    val isPremium: Boolean,
    val premiumUntil: String?,
)

data class UserProfile(
    val id: String,
    val email: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val coins: Int,
    val isPremium: Boolean,
    val premiumUntil: String?,
    val createdAt: String,
)

data class UserStats(
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestScore: Int,
    val totalScore: Int,
)

data class DailySubmissionToday(
    val dailyId: String,
    val score: Int,
    val submittedAt: String,
)

data class DailySubmitResult(
    val score: Int,
    val correct: Int,
    val total: Int,
    val bonusAwarded: Boolean,
)

data class Product(
    val id: String,
    val type: String,
    val coins: Int?,
    val tier: String?,
)

data class MpPlayer(
    val userId: String,
    val username: String,
    val displayName: String?,
    val score: Int,
    val rank: Int?,
)

data class MpMatch(
    val id: String,
    val mode: String,
    val status: String,
    val inviteCode: String?,
    val players: List<MpPlayer>,
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val totalScore: Int,
    val bestScore: Int,
)
