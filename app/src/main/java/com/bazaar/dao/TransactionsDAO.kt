package com.bazaar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bazaar.models.Transactions
import com.bazaar.utils.ConstantsManager
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDAO {
    @Query("SELECT * FROM ${ConstantsManager.TABLE_TRANSACTIONS}")
    fun getAllTransactions() : Flow<Transactions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transactions: Transactions)
}