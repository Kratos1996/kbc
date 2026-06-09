package com.ishan.kbc.data.repository

import com.ishan.kbc.data.remote.KbcApi
import com.ishan.kbc.data.remote.dto.ProductDto
import com.ishan.kbc.data.remote.dto.VerifyPurchaseRequest
import com.ishan.kbc.domain.model.Product
import com.ishan.kbc.domain.repository.ShopRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val api: KbcApi,
) : ShopRepository {

    override suspend fun products(): Result<List<Product>> = runCatching {
        api.products().map { it.toDomain() }
    }

    override suspend fun verify(productId: String, purchaseToken: String): Result<Unit> = runCatching {
        api.verifyPurchase(VerifyPurchaseRequest(productId, purchaseToken))
        Unit
    }
}

private fun ProductDto.toDomain() = Product(
    id = id,
    type = type,
    coins = coins,
    tier = tier,
)
