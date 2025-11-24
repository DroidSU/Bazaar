package com.bazaar.repository

import com.bazaar.models.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val db = Firebase.firestore

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val result = db.collection("products").get().await()
            val products = result.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProducts(product: Product): Result<Unit> {
        return try {
            val documentReference = db.collection("products").document()
            product.id = documentReference.id
            documentReference.set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}