package com.bazaar.repository

import com.bazaar.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getProducts(): Flow<Result<List<Product>>> = callbackFlow {
        val snapshotListener = db.collection("products")
            .orderBy("createdOn", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val products = snapshot.toObjects(Product::class.java)
                    trySend(Result.success(products))
                }
            }

        awaitClose {
            snapshotListener.remove()
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