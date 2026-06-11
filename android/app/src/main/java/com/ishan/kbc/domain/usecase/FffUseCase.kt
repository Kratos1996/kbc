package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.FffQuestion
import com.ishan.kbc.domain.repository.FffRepository
import javax.inject.Inject

class FffUseCase @Inject constructor(
    private val fffRepository: FffRepository,
) {
    suspend fun getRandomQuestion(): Result<FffQuestion> = fffRepository.getRandomQuestion()
}
