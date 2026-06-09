package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.MpJoinRequest
import com.ishan.kbc.data.remote.dto.MpMatchDto
import com.ishan.kbc.data.remote.dto.MpPlayerDto
import com.ishan.kbc.domain.model.MpMatch
import com.ishan.kbc.domain.model.MpPlayer
import com.ishan.kbc.domain.repository.MultiplayerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiplayerRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : MultiplayerRepository {

    override suspend fun createAsync(): Result<MpMatch> = runCatching {
        val resp = api.mpCreateAsync()
        MpMatch(
            id = resp.matchId,
            mode = "async",
            status = "waiting",
            inviteCode = resp.code,
            players = emptyList(),
        )
    }

    override suspend fun joinAsync(code: String): Result<MpMatch> = runCatching {
        api.mpJoinAsync(MpJoinRequest(code)).toDomain()
    }

    override suspend fun get(id: String): Result<MpMatch> = runCatching {
        api.mpGet(id).toDomain()
    }
}

private fun MpMatchDto.toDomain(): MpMatch = MpMatch(
    id = id,
    mode = mode,
    status = status,
    inviteCode = inviteCode,
    players = players.map { it.toDomain() },
)

private fun MpPlayerDto.toDomain(): MpPlayer = MpPlayer(
    userId = userId,
    username = user?.username ?: userId,
    displayName = user?.displayName,
    score = score,
    rank = rank,
)
