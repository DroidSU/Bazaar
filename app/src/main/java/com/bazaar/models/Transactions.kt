package com.bazaar.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bazaar.utils.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_TRANSACTIONS)
data class Transactions(
    @PrimaryKey(autoGenerate = true) val transactionsId: Int = 0,
    val productName: String,
    val totalPrice: Double,
)
