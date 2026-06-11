package com.ishan.kbc.ui.home

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
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
import com.ishan.kbc.R
import com.ishan.kbc.ui.components.ArenaBackground
import com.ishan.kbc.ui.components.ShimmerOverlay
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SecondaryContainer
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
    val hubItems = listOf(
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
        ArenaBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))

            // Welcome header with Crorepati badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.home_welcome),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(listOf(SecondaryContainer, GoldDark)),
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = OnPrimary,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "CROREPATI",
                                color = OnPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant.copy(alpha = 0.8f),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats bento grid — 2+1 layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatBentoCard(
                label = stringResource(R.string.home_rank),
                value = "#1,204",
                sub = "Top 1%",
                modifier = Modifier.weight(1f),
            )
            StatBentoCard(
                label = stringResource(R.string.home_winrate),
                value = "84%",
                icon = Icons.Filled.EmojiEvents,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        StatBentoCard(
            label = "Matches Played",
            value = "142",
            sub = "+12 this week",
            wide = true,
        )

        Spacer(Modifier.height(16.dp))

        // Play Daily Quiz
        ShimmerOverlay(alpha = 0.1f) {
            Button(
                onClick = onPlay,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = OnPrimary,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Gold, GoldDark)), RoundedCornerShape(16.dp))
                        .wrapContentSize(Alignment.Center),
                ) {
                    Row (horizontalArrangement = Arrangement.Center) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = stringResource(R.string.cta_play).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 4.sp,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Sub actions: FFF + Live Sunday Show
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SubActionCard(
                icon = Icons.Filled.Timer,
                title = "Fastest Finger First",
                desc = "Race against the clock",
                badge = "NEW ROUND",
                onClick = onFff,
                modifier = Modifier.weight(1f),
            )
            SubActionCard(
                icon = Icons.Filled.LiveTv,
                title = "Live Sunday Show",
                desc = "Join the grand finale",
                badge = "02:14:45",
                accent = true,
                onClick = onPreShow,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(20.dp))

        // Hub grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(480.dp),
        ) {
            items(hubItems, key = { it.labelRes }) { item ->
                HubTile(item)
            }
        }

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatBentoCard(
    label: String,
    value: String,
    sub: String? = null,
    icon: ImageVector? = null,
    wide: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.5f))
            .border(1.dp, Primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = label,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = if (icon != null) SecondaryContainer else Primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                )
                if (icon != null) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SecondaryContainer,
                        modifier = Modifier.size(24.dp).padding(bottom = 4.dp),
                    )
                }
            }
            if (sub != null) {
                Text(
                    text = sub,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun SubActionCard(
    icon: ImageVector,
    title: String,
    desc: String,
    badge: String,
    accent: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (accent) SecondaryContainer.copy(alpha = 0.3f) else Primary.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (accent) SecondaryContainer.copy(alpha = 0.2f) else PrimaryContainer.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (accent) SecondaryContainer else Primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Text(
                    text = badge,
                    color = if (accent) SecondaryContainer else Primary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                color = OnSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = desc,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun HubTile(item: HubItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh.copy(alpha = 0.5f))
            .border(1.dp, Primary.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .clickable(onClick = item.onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(item.labelRes),
                color = OnSurface,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}
