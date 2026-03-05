package com.sujoy.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.models.Product
import com.sujoy.data.models.TransactionEntity
import com.sujoy.data.workers.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val productDAO: ProductsDAO,
    private val transactionDAO: TransactionsDAO,
    private val syncManager: SyncManager
) :
    DatabaseRepository {

    override fun getProductsFromLocal(): Flow<List<Product>> {
        return productDAO.getAllProducts()
    }

    override suspend fun addProductsToLocal(products: List<Product>): Result<Unit> {
        return try {
            if (products.isNotEmpty()) {
                productDAO.insertProducts(products)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Empty List"))
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            Result.failure(ex)
        }
    }

    override suspend fun addProductToDB(product: Product): Result<Unit> {
        return try {
            productDAO.insertProduct(product)
            syncManager.scheduleSync()
            Result.success(Unit)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            Result.failure(ex)
        }
    }

    override suspend fun updateProductInDB(product: Product): Result<Unit> {
        return try {
            productDAO.updateProduct(product)
            syncManager.scheduleSync()
            Result.success(Unit)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            Result.failure(ex)
        }
    }

    override suspend fun createTransaction(transaction: TransactionEntity) : Result<Unit>{
        return try {
            transactionDAO.insertTransaction(transaction)
            syncManager.scheduleSync()
            Result.success(Unit)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            Result.failure(ex)
        }
    }
}