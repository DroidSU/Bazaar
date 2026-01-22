package com.sujoy.data.repository

import com.sujoy.database.dao.ProductsDAO
import com.sujoy.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TransactionsRepositoryImpl(private val productsDAO: ProductsDAO) : TransactionsRepository {
    override fun getProducts(): Flow<Result<List<Product>>> {
        return productsDAO.getAllProducts()
            .map { products ->
                Result.success(products)
            }
            .catch { exception ->
                emit(Result.failure(exception))
            }
            .flowOn(Dispatchers.IO)
    }
}
