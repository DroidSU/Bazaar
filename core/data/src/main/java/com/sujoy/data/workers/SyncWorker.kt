package com.sujoy.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.sujoy.common.ConstantsManager
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.models.SyncState
import com.sujoy.data.repository.NetworkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val productDAO: ProductsDAO,
    private val transactionDAO: TransactionsDAO,
    private val firestore: FirebaseFirestore,
    private val networkRepository: NetworkRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val pendingProducts = productDAO.getProductsBySyncState(SyncState.PENDING)
            val pendingTransactions = transactionDAO.getTransactionsBySyncState(SyncState.PENDING)

            // Sync Products
            for (product in pendingProducts) {
                firestore.collection(ConstantsManager.COLLECTION_PRODUCTS)
                    .document(product.id)
                    .set(product)
                    .await()

                networkRepository.updateProduct(product)

                productDAO.updateProduct(product.copy(syncState = SyncState.SYNCED))
            }

            // Sync Transactions
            for (transaction in pendingTransactions) {
                firestore.collection(ConstantsManager.COLLECTION_TRANSACTIONS)
                    .document(transaction.transactionsId.toString())
                    .set(transaction)
                    .await()

                transactionDAO.updateSyncState(transaction.transactionsId, SyncState.SYNCED)

            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}