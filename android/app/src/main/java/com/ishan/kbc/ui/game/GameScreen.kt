package com.ishan.kbc.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.GameStatus
import com.ishan.kbc.ui.components.LifelineBar
import com.ishan.kbc.ui.components.PrizeLadder
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.SafeZone
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

private const val TIMER_WARN = 5

@Composable
fun GameScreen(
    onExit: (status: GameStatus, prize: Int, score: Int, level: Int) -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { if (state.gameId == null) viewModel.startGame() }

    LaunchedEffect(state.status) {
        when (state.status) {
            GameStatus.Lost -> {
                kotlinx.coroutines.delay(1500)
                onExit(GameStatus.Lost, state.prize, state.score, state.level)
            }
            GameStatus.Quit -> {
                onExit(GameStatus.Quit, state.prize, state.score, state.level)
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            PrizeLadder(
                currentLevel = state.level,
                safeZones = setOf(5, 10),
                modifier = Modifier.width(108.dp).fillMaxSize(),
            )
            Column(modifier = Modifier.fillMaxSize().padding(start = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            stringResource(R.string.game_level, state.level, state.total),
                            color = Gold,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            stringResource(R.string.game_prize, state.prize),
                            color = if (state.safeZone) SafeZone else OnSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    CountdownTimer(seconds = state.timeRemaining)
                }

                Spacer(Modifier.height(8.dp))

                state.question?.let { q ->
                    HexQuestionCard(text = q.text)
                    Spacer(Modifier.height(8.dp))

                    q.options.forEachIndexed { i, opt ->
                        val isEliminated = i in state.eliminatedOptions
                        ThreeDAnswerButton(
                            label = ('A' + i).toString(),
                            text = opt,
                            isEliminated = isEliminated,
                            isCorrect = state.revealedCorrectOption == i,
                            isWrong = state.lastAnswerCorrect == false && state.selectedOption == i,
                            enabled = state.status == GameStatus.InProgress && state.lastAnswerCorrect == null && !state.answered,
                            onClick = { viewModel.selectOption(i) },
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                LifelineBar(
                    available = state.lifelinesRemaining,
                    onUse = viewModel::useLifeline,
                )

                state.audiencePoll?.let { poll ->
                    Text(
                        "Audience: " + poll.entries.joinToString { (i, p) -> "${('A' + i)}=$p%" },
                        color = OnSurface,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                state.expertAnswer?.let { Text("Expert suggests: ${'A' + it}", color = OnSurface, style = MaterialTheme.typography.bodySmall) }
                state.phoneAFriendAnswer?.let { Text("Friend suggests: ${'A' + it}", color = OnSurface, style = MaterialTheme.typography.bodySmall) }

                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.lastAnswerCorrect == true && state.status == GameStatus.InProgress) {
                        Button(
                            onClick = viewModel::nextQuestion,
                            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = OnPrimary),
                        ) {
                            Text(stringResource(R.string.cta_continue), fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(onClick = { showQuitDialog = true }) {
                        Text(stringResource(R.string.cta_quit))
                    }
                }
            }
        }

        if (showQuitDialog) {
            QuitConfirmation(
                prize = state.prize,
                level = state.level,
                onConfirm = { viewModel.quit(); showQuitDialog = false },
                onDismiss = { showQuitDialog = false },
            )
        }

        if (state.status == GameStatus.Won) {
            WinnerOverlay(
                prize = state.prize,
                score = state.score,
                onExit = { onExit(GameStatus.Won, state.prize, state.score, state.level) },
            )
        }
    }
}

@Composable
private fun CountdownTimer(seconds: Int) {
    val fraction = seconds / 30f
    val isWarning = seconds <= TIMER_WARN
    val color = if (isWarning) Error else Gold
    val animFraction by animateFloatAsState(targetValue = fraction, animationSpec = tween(300), label = "timer")

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
        CircularProgressIndicator(
            progress = { animFraction },
            modifier = Modifier.size(52.dp),
            color = color,
            strokeWidth = 4.dp,
            trackColor = SurfaceContainerHighest,
        )
        Text(
            text = "$seconds",
            color = color,
            fontSize = if (isWarning) 18.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = com.ishan.kbc.ui.theme.JetBrainsMonoFont,
        )
    }
}

@Composable
private fun HexQuestionCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(SurfaceContainerHigh, SurfaceContainerLow),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                )
            )
            .drawBehind {
                val s = size
                val path = Path().apply {
                    val h = s.height; val w = s.width
                    val inset = 16f
                    moveTo(inset, 0f)
                    lineTo(w - inset, 0f)
                    lineTo(w, h * 0.45f)
                    lineTo(w - inset, h)
                    lineTo(inset, h)
                    lineTo(0f, h * 0.45f)
                    close()
                }
                clipPath(path) {
                    drawRect(color = Color.Transparent)
                    // border
                    drawPath(path, Gold.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                }
            }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = OnSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ThreeDAnswerButton(
    label: String,
    text: String,
    isEliminated: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = when {
        isCorrect -> SafeZone
        isWrong -> Error.copy(alpha = 0.3f)
        isEliminated -> SurfaceContainerHighest.copy(alpha = 0.3f)
        else -> SurfaceContainerHigh
    }
    val contentColor = when {
        isCorrect -> Color.Black
        else -> OnSurface
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                if (!isEliminated) {
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.3f),
                        cornerRadius = CornerRadius(12f, 12f),
                        topLeft = Offset(0f, 3f),
                        size = Size(size.width, size.height),
                    )
                }
            }
            .then(
                if (isEliminated) {
                    Modifier.background(SurfaceContainerHighest.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                } else {
                    Modifier.background(
                        Brush.verticalGradient(
                            colors = if (isCorrect) listOf(SafeZone, SafeZone.copy(alpha = 0.7f))
                            else listOf(bgColor, bgColor.copy(alpha = 0.7f)),
                        ),
                        RoundedCornerShape(12.dp)
                    )
                }
            )
            .clickable(enabled = enabled && !isEliminated, onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isCorrect) Gold else Primary.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    color = if (isCorrect) OnPrimary else OnSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (isEliminated) "" else text,
                color = if (isEliminated) contentColor.copy(alpha = 0.2f) else contentColor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (!isEliminated) FontWeight.Medium else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun QuitConfirmation(
    prize: Int,
    level: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerHigh,
        title = {
            Text(stringResource(R.string.quit_title), color = OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.quit_body, prize, level),
                    color = OnSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.quit_warning),
                    color = Gold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Error, contentColor = Color.White),
            ) {
                Text(stringResource(R.string.cta_quit_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cta_cancel), color = OnSurface)
            }
        },
    )
}

@Composable
private fun WinnerOverlay(
    prize: Int,
    score: Int,
    onExit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.msg_crorepati),
                color = Gold,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                fontFamily = com.ishan.kbc.ui.theme.SoraFont,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.winner_prize, prize),
                color = Gold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.winner_score, score),
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = OnPrimary),
                modifier = Modifier.fillMaxWidth(0.6f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    stringResource(R.string.cta_continue),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}
