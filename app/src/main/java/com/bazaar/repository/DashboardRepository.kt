package com.bazaar.repository

import com.bazaar.models.Product
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    suspend fun getProducts() : Flow<Result<List<Product>>>
    suspend fun storeProductList(products: List<Product>)
    suspend fun signOut()
}