package com.ishan.kbc.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.LeaderboardEntry
import com.ishan.kbc.domain.repository.LeaderboardScope
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SuccessGreen
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

private val HexShape = object : Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.25f)
            lineTo(w, h * 0.75f)
            lineTo(w * 0.5f, h)
            lineTo(0f, h * 0.75f)
            lineTo(0f, h * 0.25f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LeaderboardScope.entries.forEach { scope ->
                FilterChip(
                    selected = state.scope == scope,
                    onClick = { viewModel.load(scope) },
                    label = { Text(scopeLabel(scope)) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error != null -> ErrorBlock(state.error!!, onBack)
            state.entries.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.leaderboard_empty), color = MaterialTheme.colorScheme.onSurface)
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.entries, key = { it.userId }) { e ->
                    LeaderboardGlassRow(
                        entry = e,
                        isMe = e.userId == state.currentUserId,
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardGlassRow(entry: LeaderboardEntry, isMe: Boolean) {
    val rankColor = when (entry.rank) {
        1 -> Gold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> OnSurfaceVariant
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, SurfaceContainerHighest.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .then(if (isMe) Modifier.border(1.5f.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp)) else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(HexShape)
                    .background(PrimaryContainer.copy(alpha = 0.4f))
                    .border(1.5f.dp, Primary.copy(alpha = 0.5f), HexShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = entry.displayName?.take(1)?.uppercase() ?: entry.username.take(1).uppercase(),
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "#${entry.rank}",
                color = rankColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName?.takeIf { it.isNotBlank() } ?: entry.username,
                    color = OnSurface,
                    fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                )
                if (isMe) {
                    Text(
                        text = stringResource(R.string.leaderboard_you),
                        color = SuccessGreen,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Text(
                text = formatScore(entry.totalScore),
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(72.dp),
            )
        }
    }
}

@Composable
private fun ErrorBlock(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
    }
}

@Composable
private fun scopeLabel(scope: LeaderboardScope): String = when (scope) {
    LeaderboardScope.Global -> stringResource(R.string.scope_global)
    LeaderboardScope.Weekly -> stringResource(R.string.scope_weekly)
    LeaderboardScope.Monthly -> stringResource(R.string.scope_monthly)
    LeaderboardScope.Friends -> stringResource(R.string.scope_friends)
}

private fun formatScore(score: Int): String = when {
    score >= 1_000_000 -> "₹${score / 1_000_000}Cr"
    score >= 100_000 -> "₹${score / 1_000}K"
    else -> "₹$score"
}
