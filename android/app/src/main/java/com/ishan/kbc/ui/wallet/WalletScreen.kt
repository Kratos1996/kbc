package com.ishan.kbc.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.ui.theme.Error
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
import com.ishan.kbc.ui.theme.SurfaceContainerLowest

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        item {
            Text(
                text = "VAULT",
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
            )
            Text(
                text = "Your earnings & transactions",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
        }

        item { Spacer(Modifier.height(4.dp)) }

        // Balance card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(listOf(PrimaryContainer.copy(alpha = 0.3f), SurfaceContainerLowest))
                    )
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "BALANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 3.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.balance,
                        style = MaterialTheme.typography.displayMedium,
                        color = Gold,
                        fontWeight = FontWeight.Black,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StatItem(label = "Lifetime", value = state.lifetimeEarnings)
                        StatItem(label = "Matches", value = state.matchesPlayed.toString())
                        StatItem(label = "Win Rate", value = state.winRate)
                    }
                }
            }
        }

        // Transactions header
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "RECENT TRANSACTIONS",
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant,
                letterSpacing = 3.sp,
            )
        }

        items(state.transactions, key = { it.id }) { tx ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerLow.copy(alpha = 0.5f))
                    .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (tx.isPositive) Gold.copy(alpha = 0.2f) else Error.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (tx.isPositive) "+" else "-",
                            color = if (tx.isPositive) Gold else Error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.label,
                            color = OnSurface,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = tx.date,
                            color = OnSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Text(
                        text = tx.amount,
                        color = if (tx.isPositive) Gold else Error,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = OnSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = OnSurfaceVariant.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
