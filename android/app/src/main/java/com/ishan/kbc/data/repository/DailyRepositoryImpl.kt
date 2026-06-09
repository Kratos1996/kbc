package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.DailyAnswerDto
import com.ishan.kbc.data.remote.dto.DailySubmitRequest
import com.ishan.kbc.data.remote.dto.QuestionDto
import com.ishan.kbc.domain.model.DailySubmissionToday
import com.ishan.kbc.domain.model.DailySubmitResult
import com.ishan.kbc.domain.model.Question
import com.ishan.kbc.domain.repository.DailyChallenge
import com.ishan.kbc.domain.repository.DailyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : DailyRepository {

    override suspend fun today(): Result<DailyChallenge> = runCatching {
        val resp = api.daily()
        DailyChallenge(
            id = resp.id,
            date = resp.date,
            bonusCoins = resp.bonusCoins,
            questions = resp.questions.map { it.toDomain() },
        )
    }

    override suspend fun submissionToday(): Result<DailySubmissionToday?> = runCatching {
        api.dailySubmissionToday()?.let {
            DailySubmissionToday(it.dailyId, it.score, it.submittedAt)
        }
    }

    override suspend fun submit(
        dailyId: String,
        answers: List<Pair<String, Int>>,
    ): Result<DailySubmitResult> = runCatching {
        val req = DailySubmitRequest(
            dailyId = dailyId,
            answers = answers.map { (qid, opt) -> DailyAnswerDto(qid, opt) },
        )
        val r = api.dailySubmit(req)
        DailySubmitResult(r.score, r.correct, r.total, r.bonusAwarded)
    }
}

private fun QuestionDto.toDomain() = Question(
    id = id,
    text = text,
    options = options,
    difficulty = difficulty,
    categoryId = categoryId,
    categoryName = categoryName,
)
