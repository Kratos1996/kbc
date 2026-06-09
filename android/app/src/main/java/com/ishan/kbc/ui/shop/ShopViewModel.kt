package com.ishan.kbc.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.analytics.AnalyticsManager
import com.ishan.kbc.data.billing.BillingManager
import com.ishan.kbc.data.billing.PurchaseUpdate
import com.ishan.kbc.domain.model.Product
import com.ishan.kbc.domain.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val purchasingId: String? = null,
    val lastResult: PurchaseResult? = null,
)

sealed interface PurchaseResult {
    data object Success : PurchaseResult
    data object Cancelled : PurchaseResult
    data class Failed(val message: String) : PurchaseResult
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository,
    private val billingManager: BillingManager,
    private val analytics: AnalyticsManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    init {
        load()
        observeBillingUpdates()
    }

    private fun observeBillingUpdates() {
        viewModelScope.launch {
            billingManager.updates.collect { update ->
                when (update) {
                    null -> Unit
                    is PurchaseUpdate.Completed -> {
                        analytics.purchase(update.productId)
                        val verifyResult = repository.verify(update.productId, update.purchaseToken)
                        verifyResult.onSuccess {
                            _state.update { it.copy(purchasingId = null, lastResult = PurchaseResult.Success) }
                        }.onFailure { e ->
                            _state.update { it.copy(purchasingId = null, lastResult = PurchaseResult.Failed(e.message ?: "verify failed")) }
                        }
                        billingManager.consumeUpdate()
                    }
                    is PurchaseUpdate.Failed -> {
                        _state.update { it.copy(purchasingId = null, lastResult = PurchaseResult.Failed(update.message)) }
                        billingManager.consumeUpdate()
                    }
                    PurchaseUpdate.Cancelled -> {
                        _state.update { it.copy(purchasingId = null, lastResult = PurchaseResult.Cancelled) }
                        billingManager.consumeUpdate()
                    }
                }
            }
        }
    }

    fun load() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            repository.products()
                .onSuccess { products -> _state.update { it.copy(loading = false, products = products) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun buy(product: Product) {
        if (_state.value.purchasingId != null) return
        _state.update { it.copy(purchasingId = product.id, lastResult = null) }
        viewModelScope.launch {
            val ok = billingManager.launchPurchaseFlow(product.id)
            if (!ok) {
                // The billing manager already pushed a Failed update; clear purchasingId
                _state.update { it.copy(purchasingId = null) }
            }
        }
    }

    fun consumeResult() = _state.update { it.copy(lastResult = null) }
}
