package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.domain.model.Category
import com.ishan.kbc.domain.model.Question
import com.ishan.kbc.domain.repository.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : QuestionRepository {

    override suspend fun categories(): Result<List<Category>> = runCatching {
        api.categories().map { Category(it.id, it.name, it.slug, it.icon, it.sortOrder) }
    }
}
