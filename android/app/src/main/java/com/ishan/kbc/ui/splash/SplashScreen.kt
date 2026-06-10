package com.ishan.kbc.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.Background
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SecondaryContainer
import com.ishan.kbc.ui.theme.SecondaryFixed
import com.ishan.kbc.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onComplete: () -> Unit,
    viewModel: SplashViewModel,
) {
    val rawProgress by viewModel.progress.collectAsState()
    val isComplete by viewModel.isComplete.collectAsState()

    val progress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 50),
        label = "splashProgress",
    )

    LaunchedEffect(isComplete) {
        if (isComplete) onComplete()
    }

    // Pulse animation state
    val logoScale = remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            logoScale.value = 1.02f
            delay(1500)
            logoScale.value = 1f
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Background))

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-100).dp, y = (-100).dp)
                    .background(Primary.copy(alpha = 0.06f))
                    .clip(CircleShape)
                    .graphicsLayer { scaleX = 2f; scaleY = 2f },
            )
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 100.dp, y = 100.dp)
                    .background(PrimaryContainer.copy(alpha = 0.08f))
                    .clip(CircleShape)
                    .graphicsLayer { scaleX = 2f; scaleY = 2f },
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
                    .background(Primary.copy(alpha = 0.05f), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "KBC Quiz Logo",
                    modifier = Modifier.size(180.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Play. Learn. Win Crores.",
                color = SecondaryFixed,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.15.em,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(SurfaceContainerLowest.copy(alpha = 0.5f))
                        .clip(CircleShape),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(SecondaryContainer)
                            .clip(CircleShape),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Synchronizing Arena...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.05.em,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
        )
    }
}
