package com.sujoy.data.repository

import com.sujoy.data.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<Result<List<Product>>>
    suspend fun addProducts(product: Product): Result<Unit>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
