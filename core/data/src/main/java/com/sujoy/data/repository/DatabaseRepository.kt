package com.sujoy.data.repository

import com.sujoy.data.models.Product
import com.sujoy.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getProductsFromLocal(): Flow<List<Product>>
    suspend fun addProductsToLocal(products: List<Product>): Result<Unit>

    suspend fun addProductToDB(product: Product): Result<Unit>

    suspend fun updateProductInDB(product: Product): Result<Unit>

    suspend fun createTransaction(transaction: TransactionEntity) : Result<Unit>
}