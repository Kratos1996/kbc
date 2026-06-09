package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats

interface ProfileRepository {
    suspend fun me(): Result<UserProfile>
    suspend fun stats(): Result<UserStats>
    suspend fun update(displayName: String?, avatarUrl: String?): Result<UserProfile>
}
