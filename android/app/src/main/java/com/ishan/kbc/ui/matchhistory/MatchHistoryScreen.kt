package com.ishan.kbc.ui.matchhistory

import com.ishan.kbc.domain.model.MatchEntry
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.JetBrainsMonoFont
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
fun MatchHistoryScreen(
    onBack: () -> Unit,
    viewModel: MatchHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.match_history_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Gold,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.match_history_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
        )

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(state.matches, key = { it.id }) { match ->
                if (match.isHighlight) {
                    HighlightMatchCard(match = match)
                } else {
                    MatchCard(match = match)
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                StatsCard(
                    winRate = state.winRate,
                    totalEarnings = state.totalEarnings,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MatchCard(match: MatchEntry) {
    val borderColor = if (match.isWin) Gold.copy(alpha = 0.3f) else OnSurfaceVariant.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        match.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontFamily = JetBrainsMonoFont,
                    )
                    Text(
                        match.date,
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurface,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(HexClip())
                        .background(
                            if (match.isWin) GoldDark.copy(alpha = 0.2f)
                            else SurfaceVariant.copy(alpha = 0.3f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        match.category.take(2).uppercase(),
                        color = if (match.isWin) Gold else OnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                match.category,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$${match.prize}",
                    color = if (match.isWin) Gold else OnSurfaceVariant,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.match_history_prize),
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (match.isWin) PrimaryContainer.copy(alpha = 0.2f)
                    else SurfaceContainerHighest,
                    contentColor = if (match.isWin) Primary else OnSurfaceVariant,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    stringResource(R.string.match_history_breakdown),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (match.isWin) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp),
            ) {
                Text(
                    match.badge ?: "",
                    color = Gold,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun HighlightMatchCard(match: MatchEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        PrimaryContainer.copy(alpha = 0.15f),
                        SurfaceContainerLow.copy(alpha = 0.6f),
                    ),
                ),
            )
            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(HexClip())
                    .background(
                        Brush.verticalGradient(listOf(PrimaryContainer, SurfaceContainerLow)),
                    )
                    .border(1.dp, Primary.copy(alpha = 0.2f), HexClip()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "🏆",
                    fontSize = 28.sp,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        match.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontFamily = JetBrainsMonoFont,
                    )
                    Text(
                        match.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    match.category,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                )
                match.description?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$${match.prize}",
                    color = Gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                )
                Text(
                    stringResource(R.string.match_history_ultimate_prize),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                )
                match.badge?.let {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Gold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            it,
                            color = Gold,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(winRate: Float, totalEarnings: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Column {
            Text(
                stringResource(R.string.match_history_stats),
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.match_history_win_rate),
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "${(winRate * 100).toInt()}%",
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SurfaceContainerHighest),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(winRate)
                        .background(Primary, RoundedCornerShape(3.dp)),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.match_history_earnings),
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "$${totalEarnings}",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                )
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
