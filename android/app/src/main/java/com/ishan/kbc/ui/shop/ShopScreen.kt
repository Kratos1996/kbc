package com.ishan.kbc.ui.shop

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.Product
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SurfaceVariant

@Composable
fun ShopScreen(
    onBack: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val successMsg = stringResource(R.string.shop_buy_success)
    val cancelMsg = stringResource(R.string.shop_buy_cancelled)
    val failPrefix = stringResource(R.string.shop_buy_failed)

    LaunchedEffect(state.lastResult) {
        val r = state.lastResult ?: return@LaunchedEffect
        val text = when (r) {
            PurchaseResult.Success -> successMsg
            PurchaseResult.Cancelled -> cancelMsg
            is PurchaseResult.Failed -> "$failPrefix: ${r.message}"
        }
        snackbarHostState.showSnackbar(text)
        viewModel.consumeResult()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
                Spacer(Modifier.height(0.dp))
                Text(
                    text = "  ${stringResource(R.string.shop_title)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(16.dp))
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.products, key = { it.id }) { p ->
                        ProductCard(
                            product = p,
                            purchasing = state.purchasingId == p.id,
                            onBuy = { viewModel.buy(p) },
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) { data -> Snackbar(snackbarData = data) }
    }
}

@Composable
private fun ProductCard(product: Product, purchasing: Boolean, onBuy: () -> Unit) {
    val isPremium = product.type == "subscription"
    val tierLabel = when (product.tier) {
        "monthly" -> stringResource(R.string.shop_premium_tier_monthly)
        "yearly" -> stringResource(R.string.shop_premium_tier_yearly)
        else -> null
    }
    val title = tierLabel ?: productLabel(product.id)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPremium) Gold.copy(alpha = 0.15f) else SurfaceVariant)
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.6f),
                )
                Spacer(Modifier.fillMaxWidth(0.1f))
                Text(
                    text = if (isPremium) stringResource(R.string.shop_subscription) else stringResource(R.string.shop_one_time),
                    color = if (isPremium) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (product.coins != null && product.coins > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.shop_coins, product.coins),
                    color = Gold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onBuy, enabled = !purchasing) {
                if (purchasing) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.cta_buy))
                }
            }
        }
    }
}

@Composable
private fun productLabel(id: String): String = when (id) {
    "coins_small" -> "100 Coins"
    "coins_medium" -> "500 Coins"
    "coins_large" -> "1500 Coins"
    "lifeline_restock" -> "Lifeline Restock"
    else -> id
}
