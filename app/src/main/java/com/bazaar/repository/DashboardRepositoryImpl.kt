package com.bazaar.repository

import com.bazaar.db.dao.ProductsDAO
import com.bazaar.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DashboardRepositoryImpl(private val productDAO : ProductsDAO) : DashboardRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getProducts(): Flow<Result<List<Product>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
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

    override suspend fun storeProductList(products: List<Product>) {
        if(products.isNotEmpty()){
            productDAO.insertProduct(products)
        }
    }
}