package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.DailySubmissionToday
import com.ishan.kbc.domain.model.DailySubmitResult
import com.ishan.kbc.domain.model.Question

data class DailyChallenge(
    val id: String,
    val date: String,
    val bonusCoins: Int,
    val questions: List<Question>,
)

interface DailyRepository {
    suspend fun today(): Result<DailyChallenge>
    suspend fun submissionToday(): Result<DailySubmissionToday?>
    suspend fun submit(dailyId: String, answers: List<Pair<String, Int>>): Result<DailySubmitResult>
}
