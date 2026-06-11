package com.ishan.kbc.ui.wallet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class WalletUiState(
    val balance: String = "$25,000",
    val lifetimeEarnings: String = "$142,890",
    val matchesPlayed: Int = 142,
    val winRate: String = "84%",
    val transactions: List<TransactionItem> = emptyList(),
)

data class TransactionItem(
    val id: String,
    val label: String,
    val amount: String,
    val date: String,
    val isPositive: Boolean,
)

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(WalletUiState(
        transactions = listOf(
            TransactionItem("1", "Daily Challenge Bonus", "+$500", "Today", true),
            TransactionItem("2", "Match Win — Classic", "+$2,000", "Yesterday", true),
            TransactionItem("3", "Lifeline Refill", "-$800", "2 days ago", false),
            TransactionItem("4", "Tournament Entry", "-$1,500", "3 days ago", false),
            TransactionItem("5", "Achievement Unlocked", "+$1,000", "5 days ago", true),
            TransactionItem("6", "Weekly Leaderboard", "+$5,000", "1 week ago", true),
        )
    ))
    val state: StateFlow<WalletUiState> = _state.asStateFlow()
}
