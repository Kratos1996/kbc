package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.FffQuestion

interface FffRepository {
    suspend fun getRandomQuestion(): Result<FffQuestion>
}
