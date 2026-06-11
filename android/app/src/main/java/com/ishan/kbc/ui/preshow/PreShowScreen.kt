package com.ishan.kbc.ui.preshow

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.ui.components.ArenaBackground
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
import com.ishan.kbc.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun PreShowScreen(
    onEnterArena: () -> Unit,
    onBack: () -> Unit,
    viewModel: PreShowViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        ArenaBackground()
        ParticleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = OnSurface
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "RADIANCE ARENA",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                )
            }

            Spacer(Modifier.height(28.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.preshow_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary.copy(alpha = 0.7f),
                        letterSpacing = 4.sp,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = formatTimer(state.secondsRemaining),
                        fontFamily = SoraFont,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = OnSurface,
                        letterSpacing = (-2).sp,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.preshow_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    AnimatedBorder(modifier = Modifier.fillMaxWidth(0.8f)) {
                        Button(
                            onClick = onEnterArena,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = OnPrimary,
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.preshow_join),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(listOf(Gold, GoldDark)),
                                        RoundedCornerShape(14.dp),
                                    )
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.preshow_jackpot),
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", state.prizePool)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = stringResource(R.string.preshow_growing),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceContainerHighest)
                            .border(1.dp, SurfaceContainerLow, RoundedCornerShape(6.dp)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(state.prizeProgress)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(listOf(Primary, Gold)),
                                    RoundedCornerShape(6.dp),
                                ),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("MIN $100K", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                        Text("NEXT $300K", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.preshow_spotlight),
                        style = MaterialTheme.typography.titleSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "128 ${stringResource(R.string.preshow_joined)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                    )
                }
                Spacer(Modifier.height(12.dp))
                state.contestants.forEach { c ->
                    ContestantRow(c)
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerHighest.copy(alpha = 0.5f),
                        contentColor = Primary,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.preshow_view_lobby))
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                accentColor = Gold,
                accentSide = true,
            ) {
                Text(
                    text = stringResource(R.string.preshow_params),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                ParamRow(stringResource(R.string.preshow_rounds), "15 Hex-Levels")
                Spacer(Modifier.height(8.dp))
                ParamRow(stringResource(R.string.preshow_time), "12s / Question")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.preshow_difficulty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                    )
                    Text(
                        "HARMONIC CHAOS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Error,
                        fontWeight = FontWeight.Bold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.preshow_lifelines),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    LifelineIcon("ORACLE", Primary)
                    LifelineIcon("VANISH", Gold)
                    LifelineIcon("SWAP", OnSurface)
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.2f))
                                .border(1.5.dp, Primary.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "\u25B6",
                                color = Primary,
                                fontSize = 24.sp,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Live Studio Warm-up",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Host: Master Oracle & Special Guests",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .background(Error.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Error),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                color = Error,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun ParticleBackground() {
    val transition = rememberInfiniteTransition(label = "particles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "particlePhase",
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawIntoCanvas { c ->
            val paint = android.graphics.Paint().apply { isAntiAlias = true }
            val seed = (phase * 10).toInt()
            val rng = java.util.Random(seed.toLong())
            repeat(60) {
                val x = ((rng.nextFloat() * w + phase * 0.5f) % w).toFloat()
                val y = ((rng.nextFloat() * h + phase * 0.3f) % h).toFloat()
                val r = (rng.nextFloat() * 2 + 1).toFloat()
                val alpha = (rng.nextFloat() * 0.3).toFloat()
                val color = if (rng.nextBoolean()) Color(0xFF7000FF) else Color(0xFFFFE16D)
                paint.alpha = (alpha * 255).toInt()
                paint.color = color.toArgb()
                c.nativeCanvas.drawCircle(x, y, r, paint)
            }
        }
    }
}

@Composable
private fun AnimatedBorder(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "pulse-border")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )
    Box(
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = Gold.copy(alpha = pulseAlpha),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = 2f),
            )
        },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun GlassPanel(
    modifier: Modifier = Modifier,
    accentColor: Color = Primary.copy(alpha = 0.1f),
    accentSide: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.4f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .then(
                if (accentSide) Modifier.border(
                    width = 3.dp,
                    color = accentColor,
                    shape = RoundedCornerShape(20.dp),
                ) else Modifier,
            )
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
private fun ContestantRow(contestant: Contestant) {
    val divColor = when (contestant.division) {
        "D-III" -> Brush.verticalGradient(listOf(Gold, Primary))
        "D-I" -> Brush.verticalGradient(listOf(Primary.copy(alpha = 0.8f), Primary))
        else -> Brush.verticalGradient(listOf(OnSurfaceVariant, OnSurface))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh.copy(alpha = 0.5f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(HexClipShape())
                    .background(divColor)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(HexClipShape())
                        .background(SurfaceContainerLow),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = contestant.name.take(2).uppercase(),
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(Gold, RoundedCornerShape(2.dp))
                    .border(1.dp, SurfaceContainerLow, RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            ) {
                Text(
                    contestant.division,
                    color = OnPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contestant.name, color = OnSurface, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(contestant.description, color = OnSurfaceVariant.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(contestant.tier, color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ParamRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
    }
}

@Composable
private fun LifelineIcon(name: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHighest)
                .border(1.5f.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.take(2),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = JetBrainsMonoFont,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private class HexClipShape : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density,
    ) = androidx.compose.ui.graphics.Outline.Generic(
        Path().apply {
            val w = size.width; val h = size.height
            moveTo(w * 0.25f, 0f); lineTo(w * 0.75f, 0f)
            lineTo(w, h * 0.5f); lineTo(w * 0.75f, h)
            lineTo(w * 0.25f, h); lineTo(0f, h * 0.5f); close()
        }
    )
}

private fun formatTimer(sec: Int): String {
    val m = sec / 60; val s = sec % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
