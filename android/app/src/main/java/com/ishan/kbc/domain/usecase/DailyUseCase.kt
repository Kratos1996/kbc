package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.DailySubmitResult
import com.ishan.kbc.domain.model.DailySubmissionToday
import com.ishan.kbc.domain.repository.DailyChallenge
import com.ishan.kbc.domain.repository.DailyRepository
import javax.inject.Inject

class DailyUseCase @Inject constructor(
    private val dailyRepository: DailyRepository,
) {
    suspend fun today(): Result<DailyChallenge> = dailyRepository.today()
    suspend fun submissionToday(): Result<DailySubmissionToday?> = dailyRepository.submissionToday()
    suspend fun submit(dailyId: String, answers: List<Pair<String, Int>>): Result<DailySubmitResult> =
        dailyRepository.submit(dailyId, answers)
}
