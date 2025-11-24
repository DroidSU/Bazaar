package com.bazaar.repository

import com.bazaar.models.Product

interface ProductRepository {
    suspend fun getProducts() : Result<List<Product>>
    suspend fun addProducts(product: Product) : Result<Unit>
}