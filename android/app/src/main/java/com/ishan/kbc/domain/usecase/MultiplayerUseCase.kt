package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.MpMatch
import com.ishan.kbc.domain.repository.MultiplayerRepository
import javax.inject.Inject

class MultiplayerUseCase @Inject constructor(
    private val multiplayerRepository: MultiplayerRepository,
) {
    suspend fun createAsync(): Result<MpMatch> = multiplayerRepository.createAsync()
    suspend fun joinAsync(code: String): Result<MpMatch> = multiplayerRepository.joinAsync(code)
}
