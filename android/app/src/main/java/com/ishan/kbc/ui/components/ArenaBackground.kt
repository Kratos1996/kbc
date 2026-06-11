package com.ishan.kbc.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.Background
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.SecondaryContainer
import com.ishan.kbc.ui.theme.SurfaceContainerLowest

@Composable
fun ArenaBackground(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    val bgResId = if (config.screenWidthDp >= 600) R.drawable.bg_fold_or_tab else R.drawable.bg_mobile
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SurfaceContainerLowest.copy(alpha = 0.3f),
                            Background.copy(alpha = 0.5f),
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .offset(x = (-80).dp, y = (-100).dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.12f))
                .blur(radius = 120.dp)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 120.dp, y = 400.dp)
                .clip(CircleShape)
                .background(SecondaryContainer.copy(alpha = 0.08f))
                .blur(radius = 100.dp)
        )
    }
}
