package com.sujoy.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sujoy.common.ConstantsManager
import com.sujoy.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDAO {
    @Query("SELECT * FROM ${ConstantsManager.TABLE_TRANSACTIONS}")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}
