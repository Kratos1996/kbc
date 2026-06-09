package com.ishan.kbc.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.AnswerRequest
import com.ishan.kbc.data.remote.dto.StartGameRequest
import com.ishan.kbc.domain.model.GameState
import com.ishan.kbc.domain.model.GameStatus
import com.ishan.kbc.domain.model.LifelineType
import com.ishan.kbc.domain.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val api: KbcApi,
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun startGame() {
        viewModelScope.launch {
            runCatching { api.startGame(StartGameRequest(mode = "classic")) }
                .onSuccess { resp ->
                    _state.value = GameState(
                        gameId = resp.gameId,
                        level = resp.level,
                        total = resp.total,
                        prize = resp.prize,
                        safeZone = resp.safeZone,
                        question = resp.question.toDomain(),
                    )
                }
        }
    }

    fun selectOption(index: Int) {
        val s = _state.value
        val gid = s.gameId ?: return
        val q = s.question ?: return
        if (s.status != GameStatus.InProgress) return
        viewModelScope.launch {
            runCatching { api.answer(gid, AnswerRequest(q.id, index, timeMs = 0)) }
                .onSuccess { ans ->
                    _state.update {
                        it.copy(
                            lastAnswerCorrect = ans.correct,
                            revealedCorrectOption = ans.correctOption,
                            score = ans.score ?: it.score,
                            status = when (ans.gameStatus) {
                                "won" -> GameStatus.Won
                                "lost" -> GameStatus.Lost
                                else -> GameStatus.InProgress
                            },
                        )
                    }
                }
        }
    }

    fun nextQuestion() {
        val gid = _state.value.gameId ?: return
        viewModelScope.launch {
            runCatching { api.currentQuestion(gid) }
                .onSuccess { resp ->
                    _state.update {
                        it.copy(
                            level = resp.level,
                            prize = resp.prize,
                            safeZone = resp.safeZone,
                            question = resp.question.toDomain(),
                            lastAnswerCorrect = null,
                            revealedCorrectOption = null,
                            eliminatedOptions = emptySet(),
                            audiencePoll = null,
                            expertAnswer = null,
                            phoneAFriendAnswer = null,
                        )
                    }
                }
        }
    }

    fun useLifeline(type: LifelineType) {
        val s = _state.value
        if (type !in s.lifelinesRemaining) return
        val gid = s.gameId ?: return
        val qid = s.question?.id ?: return
        viewModelScope.launch {
            runCatching { api.useLifeline(gid, com.ishan.kbc.data.remote.dto.LifelineRequest(type.apiValue, qid)) }
                .onSuccess { resp ->
                    _state.update { current ->
                        val remaining = current.lifelinesRemaining - type
                        when (type) {
                            LifelineType.FiftyFifty -> current.copy(
                                eliminatedOptions = resp.eliminatedOptions?.toSet() ?: current.eliminatedOptions,
                                lifelinesRemaining = remaining,
                            )
                            LifelineType.Audience -> current.copy(
                                audiencePoll = resp.poll?.mapKeys { it.key.toIntOrNull() ?: 0 }
                                    ?.mapValues { it.value },
                                lifelinesRemaining = remaining,
                            )
                            LifelineType.Phone -> current.copy(
                                phoneAFriendAnswer = resp.suggested,
                                lifelinesRemaining = remaining,
                            )
                            LifelineType.Expert -> current.copy(
                                expertAnswer = resp.suggested,
                                lifelinesRemaining = remaining,
                            )
                            LifelineType.Flip -> {
                                val nq = resp.question
                                if (nq != null) {
                                    current.copy(
                                        question = Question(
                                            id = nq.id,
                                            text = nq.text,
                                            options = nq.options,
                                            difficulty = nq.difficulty,
                                            categoryId = nq.categoryId,
                                            categoryName = nq.categoryName,
                                        ),
                                        lifelinesRemaining = remaining,
                                    )
                                } else {
                                    current.copy(lifelinesRemaining = remaining)
                                }
                            }
                        }
                    }
                }
        }
    }

    fun quit() {
        val gid = _state.value.gameId ?: return
        viewModelScope.launch {
            runCatching { api.quit(gid) }
            _state.update { it.copy(status = GameStatus.Quit) }
        }
    }
}

private fun com.ishan.kbc.data.remote.dto.QuestionDto.toDomain() = Question(
    id = id, text = text, options = options, difficulty = difficulty,
    categoryId = categoryId, categoryName = categoryName,
)
