package com.ishan.kbc.domain.repository

import com.ishan.kbc.domain.model.Product

interface ShopRepository {
    suspend fun products(): Result<List<Product>>
    suspend fun verify(productId: String, purchaseToken: String): Result<Unit>
}
