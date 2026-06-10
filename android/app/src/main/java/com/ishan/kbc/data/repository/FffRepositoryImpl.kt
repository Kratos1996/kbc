package com.ishan.kbc.data.repository

import com.ishan.kbc.domain.model.FffQuestion
import com.ishan.kbc.domain.model.sampleFffQuestions
import com.ishan.kbc.domain.repository.FffRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FffRepositoryImpl @Inject constructor() : FffRepository {
    override suspend fun getRandomQuestion(): Result<FffQuestion> {
        return Result.success(sampleFffQuestions().random())
    }
}
