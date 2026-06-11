package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.repository.MatchHistoryData
import com.ishan.kbc.domain.repository.MatchHistoryRepository
import javax.inject.Inject

class MatchHistoryUseCase @Inject constructor(
    private val matchHistoryRepository: MatchHistoryRepository,
) {
    suspend fun getHistory(): Result<MatchHistoryData> = matchHistoryRepository.getHistory()
}
