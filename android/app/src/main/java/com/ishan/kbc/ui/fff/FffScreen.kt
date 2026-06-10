package com.ishan.kbc.ui.fff

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.JetBrainsMonoFont
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SoraFont
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

@Composable
fun FffScreen(
    onBack: () -> Unit,
    viewModel: FffViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { if (state.loading) viewModel.start() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "FASTEST FINGER FIRST",
                style = MaterialTheme.typography.titleMedium,
                color = Primary.copy(alpha = 0.7f),
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Black,
            )

            Spacer(Modifier.height(20.dp))

            PremiumTimer(seconds = state.timeRemaining)

            Spacer(Modifier.height(28.dp))

            state.question?.let { q ->
                HexFffQuestionCard(text = q.text)

                Spacer(Modifier.height(28.dp))

                q.items.forEach { item ->
                    val orderIndex = state.selectedOrder.indexOf(item.id)
                    val isSelected = orderIndex >= 0
                    HexFffOptionCard(
                        item = item,
                        isSelected = isSelected,
                        selectionOrder = if (isSelected) orderIndex + 1 else null,
                        enabled = state.result == null && !state.finished,
                        onClick = { viewModel.selectItem(item.id) },
                    )
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(20.dp))

                val canSubmit = state.selectedOrder.size == 4 && state.result == null && !state.finished
                Button(
                    onClick = { viewModel.submit() },
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(0.7f).height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerHigh.copy(alpha = 0.8f),
                        contentColor = OnSurface,
                        disabledContainerColor = SurfaceContainerHigh.copy(alpha = 0.3f),
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                if (canSubmit) Brush.horizontalGradient(listOf(Gold, GoldDark))
                                else Color.Transparent,
                                RoundedCornerShape(16.dp),
                            ),
                    )
                    Text(
                        text = stringResource(R.string.fff_submit),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 4.sp,
                        color = if (canSubmit) OnPrimary else OnSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Gold),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.fff_competitors),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 2.sp,
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        if (state.result != null) {
            FffResultOverlay(
                result = state.result!!,
                onRetry = viewModel::retry,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun PremiumTimer(seconds: Int) {
    val fraction = seconds / 30f
    val isWarning = seconds <= 5
    val color = if (isWarning) Error else Gold
    val animFraction by animateFloatAsState(targetValue = fraction, animationSpec = tween(300), label = "timer")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(SurfaceContainerLow, SurfaceContainerLow.copy(alpha = 0.3f))),
            )
            .drawBehind {
                drawRoundRect(
                    color = Gold.copy(alpha = 0.4f),
                    cornerRadius = CornerRadius(14f, 14f),
                    style = Stroke(width = 2f),
                )
            }
            .padding(horizontal = 40.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                progress = { animFraction },
                modifier = Modifier.size(26.dp),
                color = color,
                strokeWidth = 3.dp,
                trackColor = SurfaceContainerHighest,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "00:" + (seconds / 60).toString().padStart(2, '0') + ":" + (seconds % 60).toString().padStart(2, '0'),
                color = color,
                fontFamily = JetBrainsMonoFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
            )
        }
    }
}

@Composable
private fun HexFffQuestionCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                val s = size
                val path = Path().apply {
                    val inset = 16f; val h = s.height; val w = s.width
                    moveTo(inset, 0f); lineTo(w - inset, 0f)
                    lineTo(w, h * 0.45f); lineTo(w - inset, h)
                    lineTo(inset, h); lineTo(0f, h * 0.45f); close()
                }
                clipPath(path) {
                    drawRect(Brush.verticalGradient(listOf(Color(0xFF281464), Color(0xFF0A051E))))
                    drawPath(path, Gold.copy(alpha = 0.6f), style = Stroke(width = 2f))
                    drawPath(path, Primary.copy(alpha = 0.3f), style = Stroke(width = 1f))
                }
            }
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = OnSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HexFffOptionCard(
    item: com.ishan.kbc.domain.model.FffItem,
    isSelected: Boolean,
    selectionOrder: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val bgColors = if (isSelected) listOf(Gold, GoldDark) else listOf(Color(0xFF281464), Color(0xFF0A051E))
    val textColor = if (isSelected) OnPrimary else OnSurface
    val labelColor = if (isSelected) OnPrimary else Gold

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .drawBehind {
                val s = size
                val path = Path().apply {
                    val inset = 12f; val h = s.height; val w = s.width
                    moveTo(inset, 0f); lineTo(w - inset, 0f)
                    lineTo(w, h * 0.45f); lineTo(w - inset, h)
                    lineTo(inset, h); lineTo(0f, h * 0.45f); close()
                }
                clipPath(path) {
                    drawRect(Brush.verticalGradient(bgColors))
                    drawPath(path, if (isSelected) Gold.copy(alpha = 0.8f) else Gold.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                }
            }
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isSelected) Color(0xFF221B00) else PrimaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isSelected && selectionOrder != null) "$selectionOrder" else item.label,
                    color = labelColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    fontFamily = JetBrainsMonoFont,
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = item.text,
                color = textColor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun FffResultOverlay(
    result: com.ishan.kbc.domain.model.FffResult,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = if (result.correct) "CORRECT!" else "WRONG ORDER",
                fontFamily = SoraFont,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = if (result.correct) Gold else Error,
                letterSpacing = 4.sp,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Time: ${result.timeTaken}s",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Your order:",
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = result.userOrder.joinToString(" → ") { idx ->
                    result.correctOrder.indexOf(idx).let { pos -> "${('A' + pos)}" }
                },
                color = if (result.correct) Gold else Error.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Correct order:",
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = result.correctOrder.joinToString(" → ") { "${('A' + it - 1)}" },
                color = Gold,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.6f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = OnPrimary,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(listOf(Gold, GoldDark)),
                            RoundedCornerShape(14.dp),
                        ),
                )
                Text(
                    text = stringResource(R.string.fff_retry),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainerHigh,
                    contentColor = OnSurfaceVariant,
                ),
            ) {
                Text(stringResource(R.string.cta_back))
            }
        }
    }
}
