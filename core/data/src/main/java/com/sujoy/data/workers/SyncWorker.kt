package com.sujoy.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.sujoy.common.ConstantsManager
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.models.SyncState
import com.sujoy.data.repository.NetworkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val productDAO: ProductsDAO,
    private val transactionDAO: TransactionsDAO,
    private val networkRepository: NetworkRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result {
        return try {
            val pendingProducts = productDAO.getProductsBySyncState(SyncState.PENDING)
            val pendingTransactions = transactionDAO.getTransactionsBySyncState(SyncState.PENDING)

            // Sync Products
            for (product in pendingProducts) {
                networkRepository.updateProduct(product)
                productDAO.updateProduct(product.copy(syncState = SyncState.SYNCED))
            }

            // Sync Transactions
            for (transaction in pendingTransactions) {
                networkRepository.createTransactionsEntry(transaction)
                transactionDAO.updateSyncState(transaction.transactionsId, SyncState.SYNCED)
            }

            ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e(ConstantsManager.APP_TAG, "Sync error", e)
            if (runAttemptCount < 3) {
                ListenableWorker.Result.retry()
            } else {
                ListenableWorker.Result.failure()
            }
        }
    }
}
