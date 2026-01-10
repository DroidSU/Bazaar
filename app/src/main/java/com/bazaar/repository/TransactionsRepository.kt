package com.bazaar.repository

import com.bazaar.models.Product
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getProducts(): Flow<Result<List<Product>>>
}