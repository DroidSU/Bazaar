package com.sujoy.data.repository

import com.sujoy.data.models.Product
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getProductsFromLocal(): Flow<List<Product>>
    suspend fun addProductsToLocal(products: List<Product>): Result<Unit>
}