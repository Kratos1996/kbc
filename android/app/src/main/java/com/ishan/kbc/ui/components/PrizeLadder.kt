package com.ishan.kbc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SafeZone
import com.ishan.kbc.ui.theme.Surface

private val PRIZE_LADDER = listOf(
    1_000, 2_000, 3_000, 5_000, 10_000,
    20_000, 40_000, 80_000, 1_60_000, 3_20_000,
    6_40_000, 12_50_000, 25_00_000, 50_00_000, 75_00_000,
)

@Composable
fun PrizeLadder(
    currentLevel: Int,
    safeZones: Set<Int>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Display top-down (15 -> 1) so the highest prize is at the top
            PRIZE_LADDER.indices.reversed().forEach { idx ->
                val level = idx + 1
                val isCurrent = level == currentLevel
                val isSafe = level in safeZones
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isCurrent) Gold
                            else if (isSafe) SafeZone.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "$level  ₹${formatIndian(PRIZE_LADDER[idx])}",
                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isCurrent || isSafe) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

private fun formatIndian(n: Int): String =
    when {
        n >= 1_00_00_000 -> "${n / 1_00_00_000}.${(n % 1_00_00_000) / 10_00_000} Cr"
        n >= 1_00_000 -> "${n / 1_00_000}.${(n % 1_00_000) / 10_000} L"
        n >= 1_000 -> "${n / 1_000},${n % 1_000}"
        else -> n.toString()
    }
