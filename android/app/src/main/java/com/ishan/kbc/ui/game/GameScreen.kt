package com.ishan.kbc.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.ishan.kbc.domain.model.LifelineType
import com.ishan.kbc.ui.components.FireworksAnimation
import com.ishan.kbc.ui.components.LiveCameraBackground
import com.ishan.kbc.ui.components.PrizeLadder
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.SafeZone
import com.ishan.kbc.ui.theme.SecondaryContainer
import com.ishan.kbc.ui.theme.SoraFont
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow
import kotlinx.coroutines.delay
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private const val TIMER_WARN = 5

@Composable
fun GameScreen(
    onExit: (status: GameStatus, prize: Int, score: Int, level: Int) -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }
    var pendingLifeline by remember { mutableStateOf<LifelineType?>(null) }
    var pendingAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    var showLifelineDrawer by remember { mutableStateOf(false) }
    val config = LocalConfiguration.current
    val isTablet = config.screenWidthDp >= 600
    val context = LocalContext.current
    val cameraPermissionGranted = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { /* result handled by LiveCameraBackground's own check */ }

    LaunchedEffect(Unit) {
        if (state.gameId == null) viewModel.startGame()
        if (!cameraPermissionGranted) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.status) {
        when (state.status) {
            GameStatus.Lost -> {
                delay(1500)
                onExit(GameStatus.Lost, state.prize, state.score, state.level)
            }
            GameStatus.Quit -> {
                onExit(GameStatus.Quit, state.prize, state.score, state.level)
            }
            else -> {}
        }
    }

    LaunchedEffect(state.lastAnswerCorrect) {
        if (state.lastAnswerCorrect == true) {
            showCelebration = true
            delay(2000)
            showCelebration = false
            if (state.status == GameStatus.InProgress) {
                viewModel.nextQuestion()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)),
    ) {
        if (isTablet) {
            // === TABLET LAYOUT ===
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                LiveCameraBackground()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                PrizeLadder(
                    currentLevel = state.level,
                    safeZones = setOf(5, 10),
                    modifier = Modifier.width(108.dp).fillMaxHeight(),
                )
                Column(modifier = Modifier.fillMaxSize().padding(start = 8.dp)) {
                    HudBar(
                        timeRemaining = state.timeRemaining,
                        prize = state.prize,
                    )
                    Spacer(Modifier.height(6.dp))
                    LifelineRow(
                        available = state.lifelinesRemaining,
                        onUse = { pendingLifeline = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    state.question?.let { q ->
                        HexQuestionCard(text = q.text)
                        Spacer(Modifier.height(8.dp))
                        q.options.forEachIndexed { i, opt ->
                            val isEliminated = i in state.eliminatedOptions
                            StitchAnswerButton(
                                label = ('A' + i).toString(),
                                text = opt,
                                isEliminated = isEliminated,
                                isCorrect = state.revealedCorrectOption == i,
                                isWrong = state.lastAnswerCorrect == false && state.selectedOption == i,
                                enabled = state.status == GameStatus.InProgress && state.lastAnswerCorrect == null && !state.answered,
                                onClick = {
                                    if (pendingAnswerIndex == null) {
                                        pendingAnswerIndex = i
                                    }
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    state.audiencePoll?.let { poll -> AudiencePollBar(poll = poll) }
                    state.expertAnswer?.let { Text("Expert suggests: ${'A' + it}", color = Gold, style = MaterialTheme.typography.labelSmall) }
                    state.phoneAFriendAnswer?.let { Text("Friend suggests: ${'A' + it}", color = Gold, style = MaterialTheme.typography.labelSmall) }
                }
            }
        } else {
            // === MOBILE LAYOUT ===
            Column(modifier = Modifier.fillMaxSize()) {
                // Camera strip with HUD bar overlaid on top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                ) {
                    LiveCameraBackground()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                    )
                    HudBarMobile(
                        timeRemaining = state.timeRemaining,
                        prize = state.prize,
                        onLifelineClick = { showLifelineDrawer = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp)
                            .padding(top = 4.dp),
                    )
                }

                // Scrollable content below camera
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                        .navigationBarsPadding()
                        .padding(top = 8.dp, bottom = 4.dp),
                ) {
                    state.question?.let { q ->
                        HexQuestionCard(text = q.text)
                        Spacer(Modifier.height(8.dp))
                        q.options.forEachIndexed { i, opt ->
                            val isEliminated = i in state.eliminatedOptions
                            StitchAnswerButton(
                                label = ('A' + i).toString(),
                                text = opt,
                                isEliminated = isEliminated,
                                isCorrect = state.revealedCorrectOption == i,
                                isWrong = state.lastAnswerCorrect == false && state.selectedOption == i,
                                enabled = state.status == GameStatus.InProgress && state.lastAnswerCorrect == null && !state.answered,
                                onClick = {
                                    if (pendingAnswerIndex == null) {
                                        pendingAnswerIndex = i
                                    }
                                },
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    PrizeLadder(
                        currentLevel = state.level,
                        safeZones = setOf(5, 10),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(4.dp))

                    state.audiencePoll?.let { poll -> AudiencePollBar(poll = poll) }
                    state.expertAnswer?.let { Text("Expert suggests: ${'A' + it}", color = Gold, style = MaterialTheme.typography.labelSmall) }
                    state.phoneAFriendAnswer?.let { Text("Friend suggests: ${'A' + it}", color = Gold, style = MaterialTheme.typography.labelSmall) }
                }
            }
        }

        // Lifeline drawer overlay
        AnimatedVisibility(
            visible = showLifelineDrawer,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showLifelineDrawer = false },
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(220.dp)
                        .background(SurfaceContainerHigh)
                        .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "LIFELINES",
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = SoraFont,
                        )
                        LifelineType.entries.forEach { type ->
                            val enabled = type in state.lifelinesRemaining
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (enabled) SurfaceContainerHighest.copy(alpha = 0.7f)
                                        else SurfaceContainerHighest.copy(alpha = 0.3f)
                                    )
                                    .border(
                                        1.dp,
                                        if (enabled) OnSurfaceVariant.copy(alpha = 0.2f) else OnSurfaceVariant.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp),
                                    )
                                    .then(if (enabled) Modifier.clickable {
                                        pendingLifeline = type
                                        showLifelineDrawer = false
                                    } else Modifier),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = lifelineLabel(type),
                                    color = if (enabled) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Error.copy(alpha = 0.15f))
                                .border(1.dp, Error.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { showQuitDialog = true; showLifelineDrawer = false },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "QUIT GAME",
                                color = Error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
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

        if (pendingLifeline != null) {
            LifelineConfirmDialog(
                type = pendingLifeline!!,
                onConfirm = {
                    viewModel.useLifeline(pendingLifeline!!)
                    pendingLifeline = null
                },
                onDismiss = { pendingLifeline = null },
            )
        }

        if (pendingAnswerIndex != null && state.status == GameStatus.InProgress) {
            AnswerConfirmDialog(
                label = ('A' + pendingAnswerIndex!!).toString(),
                text = state.question?.options?.get(pendingAnswerIndex!!) ?: "",
                onConfirm = {
                    viewModel.selectOption(pendingAnswerIndex!!)
                    pendingAnswerIndex = null
                },
                onDismiss = { pendingAnswerIndex = null },
            )
        }

        if (showCelebration && state.lastAnswerCorrect == true) {
            CorrectAnswerCelebration(
                prize = state.prize,
                onDismiss = { showCelebration = false },
            )
        }

        if (state.status == GameStatus.Won) {
            WinnerOverlay(
                prize = state.prize,
                score = state.score,
                onExit = { onExit(GameStatus.Won, state.prize, state.score, state.level) },
            )
        }

        if (state.status == GameStatus.Lost) {
            GameOverOverlay(
                prize = state.prize,
                onPlayAgain = { viewModel.startGame() },
                onHome = { onExit(GameStatus.Lost, state.prize, state.score, state.level) },
            )
        }
    }
}

@Composable
private fun HudBar(
    timeRemaining: Int,
    prize: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "RADIANCE ARENA",
                color = Primary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
            )
            CountdownTimer(seconds = timeRemaining)
            Text(
                text = formatPrize(prize),
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun HudBarMobile(
    timeRemaining: Int,
    prize: Int,
    onLifelineClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "RADIANCE ARENA",
                color = Primary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
            )
            CountdownTimer(seconds = timeRemaining)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = formatPrize(prize),
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.2f))
                        .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onLifelineClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "LL",
                        color = Primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerConfirmDialog(
    label: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerHigh,
        title = {
            Text(
                text = "Confirm Answer",
                color = OnSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = "You selected:",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary.copy(alpha = 0.1f))
                        .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Gold, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = OnPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = text,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "This action cannot be undone.",
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = OnPrimary),
            ) {
                Text("Lock In", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurface)
            }
        },
    )
}

@Composable
private fun CorrectAnswerCelebration(
    prize: Int,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        FireworksAnimation()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(listOf(Gold, GoldDark)),
                        RoundedCornerShape(20.dp),
                    )
                    .padding(horizontal = 32.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "CORRECT!",
                    color = OnPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    letterSpacing = 4.sp,
                    fontFamily = SoraFont,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "+\u20B9${formatPrize(prize)} CREDITED",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
private fun LifelineRow(
    available: Set<LifelineType>,
    onUse: (LifelineType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        LifelineType.entries.forEach { type ->
            val enabled = type in available
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (enabled) SurfaceContainerHigh.copy(alpha = 0.7f)
                        else SurfaceContainerHigh.copy(alpha = 0.3f)
                    )
                    .border(
                        1.dp,
                        if (enabled) OnSurfaceVariant.copy(alpha = 0.2f) else OnSurfaceVariant.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp),
                    )
                    .then(if (enabled) Modifier.clickable { onUse(type) } else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = lifelineLabel(type),
                    color = if (enabled) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Error.copy(alpha = 0.15f))
                .border(1.dp, Error.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable { onUse(LifelineType.FiftyFifty) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "QUIT",
                color = Error,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
            )
        }
    }
}

@Composable
private fun lifelineLabel(type: LifelineType): String = when (type) {
    LifelineType.FiftyFifty -> "50:50"
    LifelineType.Audience -> "AUD"
    LifelineType.Phone -> "PHN"
    LifelineType.Expert -> "EXP"
    LifelineType.Flip -> "FLP"
}

@Composable
private fun LifelineConfirmDialog(
    type: LifelineType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerHigh,
        title = {
            Text(
                text = "Use ${lifelineLabel(type)}?",
                color = OnSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to use this lifeline?",
                color = OnSurfaceVariant,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = OnPrimary),
            ) {
                Text("Yes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No", color = OnSurface)
            }
        },
    )
}

@Composable
private fun CountdownTimer(seconds: Int) {
    val totalTime = 30f
    val fraction = seconds / totalTime
    val isWarning = seconds <= TIMER_WARN
    val color = if (isWarning) Error else Gold
    val animFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "timer",
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
        Canvas(modifier = Modifier.size(40.dp)) {
            val strokeW = 3.dp.toPx()
            val radius = (size.minDimension - strokeW) / 2f
            val topLeft = Offset(strokeW / 2f, strokeW / 2f)
            val arcSize = Size(radius * 2, radius * 2)
            val sweep = animFraction * 360f

            drawArc(
                color = SurfaceContainerHighest,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW),
            )
        }
        Text(
            text = "$seconds",
            color = color,
            fontSize = if (isWarning) 14.sp else 13.sp,
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
                val w = s.width; val h = s.height
                val inset = w * 0.02f
                val path = Path().apply {
                    moveTo(inset, 0f)
                    lineTo(w - inset, 0f)
                    lineTo(w, h * 0.5f)
                    lineTo(w - inset, h)
                    lineTo(inset, h)
                    lineTo(0f, h * 0.5f)
                    close()
                }
                clipPath(path) {
                    drawRect(color = Color.Transparent)
                    drawPath(path, Primary.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
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
private fun StitchAnswerButton(
    label: String,
    text: String,
    isEliminated: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = when {
        isCorrect -> SafeZone.copy(alpha = 0.4f)
        isWrong -> Error.copy(alpha = 0.3f)
        isEliminated -> SurfaceContainerHighest.copy(alpha = 0.2f)
        else -> SurfaceContainerLow.copy(alpha = 0.7f)
    }
    val contentColor = when {
        isCorrect -> Color(0xFF43A047)
        isWrong -> Error
        isEliminated -> OnSurfaceVariant.copy(alpha = 0.2f)
        else -> OnSurface
    }
    val letterBg = when {
        isCorrect -> Color(0xFF43A047)
        isWrong -> Error
        else -> Gold
    }
    val letterColor = when {
        isCorrect -> Color.White
        isWrong -> Color.White
        else -> OnPrimary
    }

    val clipShape = ParallelogramShape(cornerInset = 20f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(vertical = 3.dp)
            .clip(clipShape)
            .background(bgColor, clipShape)
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), clipShape)
            .then(
                if (isCorrect) Modifier.border(
                    1.5.dp, SafeZone.copy(alpha = 0.6f), clipShape
                ) else Modifier
            )
            .then(
                if (isWrong) Modifier.border(
                    1.5.dp, Error.copy(alpha = 0.6f), clipShape
                ) else Modifier
            )
            .clickable(enabled = enabled && !isEliminated, onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
            val letterClip = ParallelogramShape(cornerInset = 10f)
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .clip(letterClip)
                    .background(letterBg, letterClip),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isCorrect) "\u2713" else if (isWrong) "\u2717" else label,
                    color = letterColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (isEliminated) "" else text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (!isEliminated) FontWeight.Medium else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun AudiencePollBar(poll: Map<Int, Int>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Text(
            text = "AUDIENCE POLL",
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(6.dp))
        poll.entries.forEach { (option, percentage) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            ) {
                Text(
                    text = ('A' + option).toString(),
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.width(16.dp),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerHighest),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(percentage / 100f)
                            .background(
                                when (option) {
                                    0 -> Primary
                                    else -> SecondaryContainer
                                },
                                RoundedCornerShape(4.dp),
                            ),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$percentage%",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp),
                )
            }
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
        FireworksAnimation()
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

@Composable
private fun GameOverOverlay(
    prize: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerLow.copy(alpha = 0.6f))
                .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\u2661",
                    color = Error,
                    fontSize = 48.sp,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "GAME OVER",
                    color = OnSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 4.sp,
                )
                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.width(40.dp).height(1.dp).background(Gold.copy(alpha = 0.4f)))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "TOTAL WINNINGS",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatPrizeFull(prize),
                    color = Gold,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = OnPrimary),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Play Again", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Home", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatPrize(amount: Int): String = when {
    amount >= 10_000_000 -> "${amount / 10_000_000}Cr"
    amount >= 100_000 -> "${amount / 1_000}K"
    else -> "$amount"
}

private fun formatPrizeFull(amount: Int): String = "\u20B9$amount"

private class ParallelogramShape(private val cornerInset: Float) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density,
    ): androidx.compose.ui.graphics.Outline {
        val w = size.width; val h = size.height
        val inset = cornerInset * density.density
        val path = Path().apply {
            moveTo(inset, 0f)
            lineTo(w, 0f)
            lineTo(w - inset, h)
            lineTo(0f, h)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
