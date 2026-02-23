package com.sujoy.data.repository

import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.models.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val productsDAO: ProductsDAO
) : TransactionsRepository {
    override fun getProducts(): Flow<List<Product>> {
        return productsDAO.getAllProducts()
    }

    override suspend fun updateProductInDB(productId: String, newQuantity: Int) {
        productsDAO.updateProductQuantity(productId, newQuantity)
    }
}
