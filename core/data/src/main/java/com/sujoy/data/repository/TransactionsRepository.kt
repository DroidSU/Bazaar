package com.sujoy.data.repository

import com.sujoy.model.Product
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun updateProductInDB(productId: String, quantity: Int)
}
