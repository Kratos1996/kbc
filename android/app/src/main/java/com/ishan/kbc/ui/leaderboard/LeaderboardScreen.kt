package com.ishan.kbc.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.LeaderboardEntry
import com.ishan.kbc.domain.repository.LeaderboardScope
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SuccessGreen

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            stringResource(R.string.leaderboard_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LeaderboardScope.values().forEach { scope ->
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
                    LeaderboardRow(
                        entry = e,
                        isMe = e.userId == state.currentUserId,
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, isMe: Boolean) {
    val highlight = when (entry.rank) {
        1 -> Gold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = if (entry.rank in 1..3) Color.Black else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isMe) SuccessGreen.copy(alpha = 0.15f) else highlight)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "#${entry.rank}",
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp),
            )
            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(
                    entry.displayName?.takeIf { it.isNotBlank() } ?: entry.username,
                    color = textColor,
                    fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                )
                if (isMe) {
                    Text(
                        stringResource(R.string.leaderboard_you),
                        color = textColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(Modifier.fillMaxWidth(0.05f))
            Text(
                "${entry.totalScore}",
                color = textColor,
                fontWeight = FontWeight.Bold,
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
