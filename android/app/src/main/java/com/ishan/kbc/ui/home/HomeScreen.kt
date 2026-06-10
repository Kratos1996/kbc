package com.ishan.kbc.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import com.ishan.kbc.R
import com.ishan.kbc.ui.components.ShimmerOverlay
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

private data class HubItem(
    val labelRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onPreShow: () -> Unit,
    onFff: () -> Unit,
    onDaily: () -> Unit,
    onAchievements: () -> Unit,
    onMatchHistory: () -> Unit,
    onTournament: () -> Unit,
    onLeaderboard: () -> Unit,
    onMultiplayer: () -> Unit,
    onShop: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val items = listOf(
        HubItem(R.string.cta_play, Icons.Filled.PlayArrow, onPlay),
        HubItem(R.string.hub_arena, Icons.Filled.SportsEsports, onPreShow),
        HubItem(R.string.hub_fff, Icons.Filled.Timer, onFff),
        HubItem(R.string.hub_daily, Icons.Filled.CalendarMonth, onDaily),
        HubItem(R.string.hub_achievements, Icons.Filled.Star, onAchievements),
        HubItem(R.string.hub_match_history, Icons.Filled.History, onMatchHistory),
        HubItem(R.string.hub_tournament, Icons.Filled.DateRange, onTournament),
        HubItem(R.string.hub_leaderboard, Icons.Filled.EmojiEvents, onLeaderboard),
        HubItem(R.string.hub_multiplayer, Icons.Filled.Group, onMultiplayer),
        HubItem(R.string.hub_shop, Icons.Filled.ShoppingCart, onShop),
        HubItem(R.string.hub_profile, Icons.Filled.Person, onProfile),
        HubItem(R.string.hub_settings, Icons.Filled.Settings, onSettings),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            // Brand
            Text(
                text = "LUMINA",
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(R.string.hub_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            // Welcome glass panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLow.copy(alpha = 0.7f))
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.home_welcome),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StatChip(label = stringResource(R.string.home_rank), value = "#1,204")
                        StatChip(label = stringResource(R.string.home_winrate), value = "84%")
                        StatChip(label = stringResource(R.string.home_streak), value = "7")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Play Daily Quiz — large 3D gold button with shimmer
            ShimmerOverlay(alpha = 0.1f) {
                Button(
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = OnPrimary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.horizontalGradient(listOf(Gold, GoldDark)),
                                RoundedCornerShape(16.dp),
                            ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.cta_play),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 2.sp,
                    )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Hub grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(0.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(items, key = { it.labelRes }) { item -> HubTile(item) }
            }

            Spacer(Modifier.height(8.dp))

            // Sign out
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogout)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.cta_sign_out),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Gold,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun HubTile(item: HubItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh.copy(alpha = 0.6f))
            .clickable(onClick = item.onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(item.labelRes),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}
