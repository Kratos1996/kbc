package com.ishan.kbc.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: String,
    val name: String,
    val slug: String,
    val icon: String?,
    val sortOrder: Int,
)

@JsonClass(generateAdapter = true)
data class QuestionDto(
    val id: String,
    val text: String,
    val options: List<String>,
    val difficulty: Int,
    val categoryId: String,
    val categoryName: String,
    val correctOption: Int? = null,
    val explanation: String? = null,
)

@JsonClass(generateAdapter = true)
data class StartGameRequest(
    val mode: String,
    val categoryId: String? = null,
    val totalQuestions: Int? = null,
)

@JsonClass(generateAdapter = true)
data class StartGameResponse(
    val gameId: String,
    val total: Int,
    val level: Int,
    val prize: Int,
    val safeZone: Boolean,
    val question: QuestionDto,
)

@JsonClass(generateAdapter = true)
data class CurrentQuestionResponse(
    val gameId: String,
    val level: Int,
    val prize: Int,
    val safeZone: Boolean,
    val question: QuestionDto,
)

@JsonClass(generateAdapter = true)
data class LifelineRequest(
    val type: String,
    val questionId: String,
)

@JsonClass(generateAdapter = true)
data class LifelineResponse(
    val eliminatedOptions: List<Int>? = null,
    val poll: Map<String, Int>? = null,
    val suggested: Int? = null,
    val question: QuestionDto? = null,
)

@JsonClass(generateAdapter = true)
data class AnswerRequest(
    val questionId: String,
    val chosenOption: Int,
    val timeMs: Int,
)

@JsonClass(generateAdapter = true)
data class AnswerResponse(
    val correct: Boolean,
    val correctOption: Int? = null,
    val explanation: String? = null,
    val finalScore: Int? = null,
    val score: Int? = null,
    val gameStatus: String,
)

@JsonClass(generateAdapter = true)
data class DailyResponse(
    val id: String,
    val date: String,
    val bonusCoins: Int,
    val questions: List<QuestionDto>,
)

@JsonClass(generateAdapter = true)
data class LeaderboardEntryDto(
    val rank: Int,
    val userId: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val totalScore: Int,
    val bestScore: Int,
)

@JsonClass(generateAdapter = true)
data class DailyAnswerDto(
    val questionId: String,
    val chosenOption: Int,
)

@JsonClass(generateAdapter = true)
data class DailySubmitRequest(
    val dailyId: String,
    val answers: List<DailyAnswerDto>,
)

@JsonClass(generateAdapter = true)
data class DailySubmitResponse(
    val score: Int,
    val correct: Int,
    val total: Int,
    val bonusAwarded: Boolean,
)

@JsonClass(generateAdapter = true)
data class DailySubmissionTodayDto(
    val dailyId: String,
    val score: Int,
    val submittedAt: String,
)

@JsonClass(generateAdapter = true)
data class UserProfileDto(
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

@JsonClass(generateAdapter = true)
data class UserStatsDto(
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestScore: Int,
    val totalScore: Int,
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val displayName: String? = null,
    val avatarUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: String,
    val type: String,
    val coins: Int? = null,
    val tier: String? = null,
)

@JsonClass(generateAdapter = true)
data class VerifyPurchaseRequest(
    val productId: String,
    val purchaseToken: String,
)

@JsonClass(generateAdapter = true)
data class VerifyPurchaseResponse(
    val alreadyProcessed: Boolean,
    val productId: String,
)

@JsonClass(generateAdapter = true)
data class MpCreateResponse(
    val matchId: String,
    val code: String,
)

@JsonClass(generateAdapter = true)
data class MpJoinRequest(val code: String)

@JsonClass(generateAdapter = true)
data class MpPlayerDto(
    val id: String,
    val matchId: String,
    val userId: String,
    val score: Int,
    val rank: Int?,
    val joinedAt: String,
    val user: MpPlayerUserDto? = null,
)

@JsonClass(generateAdapter = true)
data class MpPlayerUserDto(
    val id: String,
    val username: String,
    val displayName: String?,
)

@JsonClass(generateAdapter = true)
data class MpMatchDto(
    val id: String,
    val mode: String,
    val status: String,
    val inviteCode: String?,
    val startedAt: String?,
    val endedAt: String?,
    val createdAt: String,
    val players: List<MpPlayerDto>,
)
