package com.sujoy.data.repository

import com.sujoy.database.dao.ProductsDAO
import com.sujoy.model.Product
import kotlinx.coroutines.flow.Flow

class TransactionsRepositoryImpl(private val productsDAO: ProductsDAO) : TransactionsRepository {
    override fun getProducts(): Flow<List<Product>> {
        return productsDAO.getAllProducts()
    }

    override suspend fun updateProductInDB(productId: String, newQuantity: Int) {
        productsDAO.updateProductQuantity(productId, newQuantity)
    }
}
