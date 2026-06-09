package com.ishan.kbc.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SurfaceVariant

private data class HubItem(
    val labelRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onDaily: () -> Unit,
    onLeaderboard: () -> Unit,
    onMultiplayer: () -> Unit,
    onShop: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val items = listOf(
        HubItem(R.string.cta_play, Icons.Filled.PlayArrow, onPlay),
        HubItem(R.string.hub_daily, Icons.Filled.CalendarMonth, onDaily),
        HubItem(R.string.hub_leaderboard, Icons.Filled.EmojiEvents, onLeaderboard),
        HubItem(R.string.hub_multiplayer, Icons.Filled.Group, onMultiplayer),
        HubItem(R.string.hub_shop, Icons.Filled.ShoppingCart, onShop),
        HubItem(R.string.hub_profile, Icons.Filled.Person, onProfile),
        HubItem(R.string.hub_settings, Icons.Filled.Settings, onSettings),
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "KBC",
            style = MaterialTheme.typography.displayLarge,
            color = Gold,
        )
        Text(
            text = stringResource(R.string.hub_subtitle),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Box(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items) { item -> HubTile(item) }
        }
        Box(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout).padding(8.dp),
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

@Composable
private fun HubTile(item: HubItem) {
    Box(
        modifier = Modifier
            .size(width = 140.dp, height = 110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .clickable(onClick = item.onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(36.dp),
            )
            Box(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(item.labelRes),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}
