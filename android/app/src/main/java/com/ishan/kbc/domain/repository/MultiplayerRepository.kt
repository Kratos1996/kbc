package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.MpMatch

interface MultiplayerRepository {
    suspend fun createAsync(): Result<MpMatch>
    suspend fun joinAsync(code: String): Result<MpMatch>
    suspend fun get(id: String): Result<MpMatch>
}
