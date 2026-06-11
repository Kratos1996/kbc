package com.ishan.kbc.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FireworksAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "fireworks")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "fireworkPhase",
    )

    val particles = remember {
        List(80) {
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val speed = Random.nextFloat() * 200f + 50f
            FireworkParticle(
                startX = Random.nextFloat() * 1000f,
                startY = Random.nextFloat() * 600f + 100f,
                angle = angle,
                speed = speed,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 0.9f,
                ),
                size = Random.nextFloat() * 5f + 2f,
                delay = Random.nextFloat() * 2f,
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = (phase / 360f) * 2f * PI.toFloat()

        particles.forEach { p ->
            val localT = (t + p.delay * PI.toFloat()) % (2f * PI.toFloat())
            val progress = (sin(localT) + 1f) / 2f
            val dist = p.speed * progress
            val x = p.startX / 1000f * w + cos(p.angle) * dist
            val y = p.startY / 1000f * h + sin(p.angle) * dist
            val alpha = 1f - progress
            val size = p.size * (1f - progress * 0.5f)

            if (alpha > 0f) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = size,
                    center = Offset(x, y),
                )
            }
        }
    }
}

private data class FireworkParticle(
    val startX: Float,
    val startY: Float,
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val delay: Float,
)
