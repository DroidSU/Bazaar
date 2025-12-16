package com.bazaar.repository

import com.bazaar.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getProducts(): Flow<Result<List<Product>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            // If there is no user, close the flow after sending an empty list.
            trySend(Result.success(emptyList()))
            close()
            return@callbackFlow
        }

        val snapshotListener = db.collection("products").document(userId).collection("userProducts")
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
            val userId = auth.currentUser?.uid
                ?: throw IllegalStateException("User must be logged in to add a product")
            val documentReference =
                db.collection("products").document(userId).collection("userProducts").document()
            product.id = documentReference.id
            documentReference.set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw IllegalStateException("User must be logged in to add a product")

            if (product.id.isBlank()) {
                throw IllegalArgumentException("Product ID cannot be empty for an update.")
            }
            product.lastUpdated = System.currentTimeMillis()
            db.collection("products").document(userId).collection("userProducts")
                .document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}