package com.bazaar.repository

import com.bazaar.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts() : Flow<Result<List<Product>>>
    suspend fun addProducts(product: Product) : Result<Unit>
}