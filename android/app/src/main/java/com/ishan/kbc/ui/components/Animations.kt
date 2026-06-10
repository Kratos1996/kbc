package com.ishan.kbc.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ShimmerOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.12f,
    content: @Composable () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )

    Box(modifier = modifier.drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha),
                    Color.Transparent,
                ),
                start = Offset(translateX * size.width, 0f),
                end = Offset(translateX * size.width + size.width * 0.5f, 0f),
            ),
        )
    }, contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
fun PulseGlowProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF7000FF),
) {
    val transition = rememberInfiniteTransition(label = "pulseGlow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                color = color.copy(alpha = glowAlpha * progress),
                size = androidx.compose.ui.geometry.Size(size.width * progress, size.height),
            )
        },
    )
}
