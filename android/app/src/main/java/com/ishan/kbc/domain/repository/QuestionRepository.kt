package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.Category

interface QuestionRepository {
    suspend fun categories(): Result<List<Category>>
}

