package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.Category
import com.ishan.kbc.domain.model.GameState
import com.ishan.kbc.domain.model.Question

interface QuestionRepository {
    suspend fun categories(): Result<List<Category>>
}

interface GameRepository {
    suspend fun startGame(mode: String, categoryId: String? = null, totalQuestions: Int? = null): Result<GameState>
    suspend fun getCurrentQuestion(gameId: String): Result<GameState>
    suspend fun answer(gameId: String, questionId: String, chosenOption: Int, timeMs: Int): Result<Pair<Boolean, Int?>>
    suspend fun quit(gameId: String): Result<Int>
}
