package com.ishan.kbc.data.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PurchaseUpdate {
    data class Completed(val productId: String, val purchaseToken: String) : PurchaseUpdate
    data class Failed(val message: String) : PurchaseUpdate
    data object Cancelled : PurchaseUpdate
}

@Singleton
class BillingManager @Inject constructor() {
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready

    private val _updates = MutableStateFlow<PurchaseUpdate?>(null)
    val updates: StateFlow<PurchaseUpdate?> = _updates

    @Volatile
    var currentActivity: Activity? = null

    private val listener = PurchasesUpdatedListener { result, purchases ->
        onPurchasesUpdated(result, purchases)
    }

    @Volatile
    private var client: BillingClient? = null

    fun connect(billingClient: BillingClient) {
        if (client == null) client = billingClient
        if (_ready.value) return
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                _ready.value = billingResult.responseCode == BillingClient.BillingResponseCode.OK
            }
            override fun onBillingServiceDisconnected() {
                _ready.value = false
            }
        })
    }

    fun endConnection() {
        client?.let { if (_ready.value) it.endConnection() }
    }

    fun purchasesUpdatedListener(): PurchasesUpdatedListener = listener

    fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.firstOrNull()?.let { p ->
                val token = p.purchaseToken
                val sku = p.products.firstOrNull() ?: ""
                if (token.isNotBlank() && sku.isNotBlank()) {
                    _updates.value = PurchaseUpdate.Completed(sku, token)
                }
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _updates.value = PurchaseUpdate.Cancelled
        } else {
            _updates.value = PurchaseUpdate.Failed(result.debugMessage)
        }
    }

    suspend fun launchPurchaseFlow(productId: String): Boolean {
        val c = client ?: return false
        if (!_ready.value) return false
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            if (productId.startsWith("premium_")) BillingClient.ProductType.SUBS
                            else BillingClient.ProductType.INAPP
                        )
                        .build()
                )
            )
            .build()
        val (result, list) = c.queryProductDetails(params)
        if (result.responseCode != BillingClient.BillingResponseCode.OK || list.isNullOrEmpty()) {
            _updates.value = PurchaseUpdate.Failed("Product not available in Play Console")
            return false
        }
        val product = list.first()
        val activity = currentActivity
        if (activity == null) {
            _updates.value = PurchaseUpdate.Failed("No active Activity to launch billing flow")
            return false
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()
                )
            )
            .build()
        val launchResult = c.launchBillingFlow(activity, flowParams)
        if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _updates.value = PurchaseUpdate.Failed(launchResult.debugMessage)
            return false
        }
        return true
    }

    fun consumeUpdate() { _updates.value = null }
}
