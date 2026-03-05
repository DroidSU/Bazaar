package com.sujoy.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sujoy.common.ConstantsManager
import com.sujoy.data.models.SyncState
import com.sujoy.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDAO {
    @Query("SELECT * FROM ${ConstantsManager.TABLE_TRANSACTIONS}")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM ${ConstantsManager.TABLE_TRANSACTIONS} WHERE syncState = :syncState")
    suspend fun getTransactionsBySyncState(syncState: SyncState): List<TransactionEntity>

    @Query("UPDATE ${ConstantsManager.TABLE_TRANSACTIONS} SET syncState = :syncState WHERE transactionsId = :id")
    suspend fun updateSyncState(id: Long, syncState: SyncState)
}
