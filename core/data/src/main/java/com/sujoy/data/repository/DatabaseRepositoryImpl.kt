package com.sujoy.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.models.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(private val productDAO: ProductsDAO) :
    DatabaseRepository {

    override fun getProductsFromLocal(): Flow<List<Product>> {
        return productDAO.getAllProducts()
    }

    override suspend fun addProductsToLocal(products: List<Product>): Result<Unit> {
        try {
            if (products.isNotEmpty()) {
                productDAO.insertProducts(products)
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Empty List"))
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            return Result.failure(ex)
        }
    }
}