package com.ishan.kbc.domain.usecase

import com.ishan.kbc.domain.model.Product
import com.ishan.kbc.domain.repository.ShopRepository
import javax.inject.Inject

class ShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    suspend fun products(): Result<List<Product>> = shopRepository.products()
    suspend fun verify(productId: String, purchaseToken: String): Result<Unit> =
        shopRepository.verify(productId, purchaseToken)
}
