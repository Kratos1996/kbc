package com.ishan.kbc.data.remote

import com.ishan.kbc.data.remote.dto.AnswerRequest
import com.ishan.kbc.data.remote.dto.AnswerResponse
import com.ishan.kbc.data.remote.dto.AuthRequest
import com.ishan.kbc.data.remote.dto.AuthResponse
import com.ishan.kbc.data.remote.dto.AuthUserDto
import com.ishan.kbc.data.remote.dto.CategoryDto
import com.ishan.kbc.data.remote.dto.CurrentQuestionResponse
import com.ishan.kbc.data.remote.dto.DailyResponse
import com.ishan.kbc.data.remote.dto.DailySubmissionTodayDto
import com.ishan.kbc.data.remote.dto.DailySubmitRequest
import com.ishan.kbc.data.remote.dto.DailySubmitResponse
import com.ishan.kbc.data.remote.dto.LeaderboardEntryDto
import com.ishan.kbc.data.remote.dto.LifelineRequest
import com.ishan.kbc.data.remote.dto.LifelineResponse
import com.ishan.kbc.data.remote.dto.MpCreateResponse
import com.ishan.kbc.data.remote.dto.MpJoinRequest
import com.ishan.kbc.data.remote.dto.MpMatchDto
import com.ishan.kbc.data.remote.dto.ProductDto
import com.ishan.kbc.data.remote.dto.QuestionDto
import com.ishan.kbc.data.remote.dto.RefreshRequest
import com.ishan.kbc.data.remote.dto.StartGameRequest
import com.ishan.kbc.data.remote.dto.StartGameResponse
import com.ishan.kbc.data.remote.dto.UpdateProfileRequest
import com.ishan.kbc.data.remote.dto.UserProfileDto
import com.ishan.kbc.data.remote.dto.UserStatsDto
import com.ishan.kbc.data.remote.dto.VerifyPurchaseRequest
import com.ishan.kbc.data.remote.dto.VerifyPurchaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface KbcApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: AuthRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): AuthResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: RefreshRequest)

    @GET("api/v1/auth/me")
    suspend fun me(): AuthUserDto

    @GET("api/v1/categories")
    suspend fun categories(): List<CategoryDto>

    @GET("api/v1/questions/next")
    suspend fun nextQuestion(
        @Query("level") level: Int,
        @Query("categoryId") categoryId: String? = null,
    ): QuestionDto

    @POST("api/v1/games")
    suspend fun startGame(@Body body: StartGameRequest): StartGameResponse

    @POST("api/v1/games/{id}/answer")
    suspend fun answer(@Path("id") gameId: String, @Body body: AnswerRequest): AnswerResponse

    @GET("api/v1/games/{id}/question")
    suspend fun currentQuestion(@Path("id") gameId: String): CurrentQuestionResponse

    @POST("api/v1/games/{id}/lifeline")
    suspend fun useLifeline(
        @Path("id") gameId: String,
        @Body body: LifelineRequest,
    ): LifelineResponse

    @POST("api/v1/games/{id}/quit")
    suspend fun quit(@Path("id") gameId: String)

    @GET("api/v1/daily/today")
    suspend fun daily(): DailyResponse

    @POST("api/v1/daily/submit")
    suspend fun dailySubmit(@Body body: DailySubmitRequest): DailySubmitResponse

    @GET("api/v1/daily/submissions/today")
    suspend fun dailySubmissionToday(): DailySubmissionTodayDto?

    @GET("api/v1/leaderboard")
    suspend fun leaderboard(
        @Query("scope") scope: String = "global",
        @Query("limit") limit: Int = 50,
    ): List<LeaderboardEntryDto>

    @GET("api/v1/users/me")
    suspend fun userProfile(): UserProfileDto

    @GET("api/v1/users/me/stats")
    suspend fun userStats(): UserStatsDto

    @PATCH("api/v1/users/me")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserProfileDto

    @GET("api/v1/billing/products")
    suspend fun products(): List<ProductDto>

    @POST("api/v1/billing/verify")
    suspend fun verifyPurchase(@Body body: VerifyPurchaseRequest): VerifyPurchaseResponse

    @POST("api/v1/multiplayer/async/create")
    suspend fun mpCreateAsync(): MpCreateResponse

    @POST("api/v1/multiplayer/async/join")
    suspend fun mpJoinAsync(@Body body: MpJoinRequest): MpMatchDto

    @GET("api/v1/multiplayer/{id}")
    suspend fun mpGet(@Path("id") id: String): MpMatchDto
}
