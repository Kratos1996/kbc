package com.ishan.kbc.ui.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.Achievement
import com.ishan.kbc.domain.model.Milestone
import com.ishan.kbc.ui.components.PulseGlowProgress
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow
import com.ishan.kbc.ui.theme.SurfaceVariant

@Composable
fun AchievementScreen(
    onBack: () -> Unit,
    viewModel: AchievementViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = OnSurface
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.achievement_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Black,
                )
            }
            Text(
                text = stringResource(R.string.achievement_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.achievement_mastery),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
                Text(
                    text = "${(state.totalMastery * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary,
                )
            }
            Spacer(Modifier.height(6.dp))
            PulseGlowProgress(
                progress = state.totalMastery,
                color = Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerHighest)
                    .border(1.dp, SurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
            )

            Spacer(Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(0.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(580.dp),
            ) {
                items(state.achievements, key = { it.id }) { a ->
                    AchievementBadge(
                        achievement = a,
                        onClick = { viewModel.selectAchievement(a) },
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (state.milestones.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.achievement_milestones),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(16.dp))

                state.milestones.forEach { m ->
                    MilestoneCard(milestone = m)
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(96.dp))
        }

        AnimatedVisibility(
            visible = state.selectedAchievement != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = true) { viewModel.clearSelection() },
                contentAlignment = Alignment.Center,
            ) {
                state.selectedAchievement?.let { a ->
                    AchievementDetailModal(
                        achievement = a,
                        onDismiss = { viewModel.clearSelection() },
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    achievement: Achievement,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (achievement.isEarned) {
                    Modifier.background(Brush.verticalGradient(listOf(PrimaryContainer.copy(alpha = 0.4f), SurfaceContainerHigh)))
                } else {
                    Modifier.background(Brush.verticalGradient(listOf(SurfaceVariant.copy(alpha = 0.4f), SurfaceContainerHigh.copy(alpha = 0.3f))))
                }
            )
            .border(
                1.dp,
                if (achievement.isEarned) Primary.copy(alpha = 0.3f) else SurfaceVariant.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(HexClip())
                .then(
                    if (achievement.isEarned) {
                        Modifier.background(Brush.verticalGradient(listOf(PrimaryContainer, SurfaceContainerHigh)))
                    } else {
                        Modifier.background(SurfaceVariant.copy(alpha = 0.5f))
                    }
                )
                .border(
                    1.5.dp,
                    if (achievement.isEarned) Primary.copy(alpha = 0.5f) else Color.Transparent,
                    HexClip(),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = achievement.icon.first().uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isEarned) Primary else OnSurfaceVariant.copy(alpha = 0.4f),
            )
            if (!achievement.isEarned) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🔒", fontSize = 16.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = achievement.name,
            color = if (achievement.isEarned) OnSurface else OnSurfaceVariant.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MilestoneCard(milestone: Milestone) {
    val tierColor = when (milestone.tierColor) {
        "gold" -> Gold; "primary" -> Primary; else -> Primary.copy(alpha = 0.7f)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .background(tierColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        milestone.tier,
                        style = MaterialTheme.typography.labelSmall,
                        color = tierColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    milestone.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                milestone.name,
                style = MaterialTheme.typography.titleSmall,
                color = tierColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(tierColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        milestone.tier.take(1),
                        color = tierColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "REWARD: ${milestone.reward}",
                    style = MaterialTheme.typography.labelSmall,
                    color = tierColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AchievementDetailModal(
    achievement: Achievement,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.95f))
            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(32.dp)
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(HexClip())
                    .then(
                        if (achievement.isEarned) {
                            Modifier.background(Brush.verticalGradient(listOf(PrimaryContainer, SurfaceContainerHigh)))
                        } else {
                            Modifier.background(SurfaceVariant.copy(alpha = 0.5f))
                        }
                    )
                    .border(
                        2.dp,
                        if (achievement.isEarned) Primary.copy(alpha = 0.5f) else Color.Transparent,
                        HexClip(),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = achievement.icon.first().uppercase(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = if (achievement.isEarned) Primary else OnSurfaceVariant.copy(alpha = 0.3f),
                )
                if (!achievement.isEarned) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🔒", fontSize = 32.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                achievement.name,
                style = MaterialTheme.typography.headlineSmall,
                color = if (achievement.isEarned) Gold else OnSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        if (achievement.isEarned) Primary.copy(alpha = 0.2f) else SurfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    if (achievement.isEarned) "EARNED" else "LOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (achievement.isEarned) Primary else OnSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            if (achievement.isEarned) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
                            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            "EARNED ON",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                        )
                        Text(
                            achievement.earnedDate ?: "—",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurface,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
                            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            "GLOBAL RARITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                        )
                        Text(
                            achievement.rarity ?: "—",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurface,
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = OnPrimary,
                    ),
                ) {
                    Text(
                        "Share to Society",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Gold, GoldDark)),
                                RoundedCornerShape(14.dp),
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                }
            } else {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Complete the required challenge to unlock this achievement.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainerHigh,
                    contentColor = OnSurfaceVariant,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.achievement_back))
            }
        }
    }
}

private class HexClip : androidx.compose.ui.graphics.Shape {
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
