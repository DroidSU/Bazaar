package com.sujoy.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sujoy.common.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_TRANSACTIONS)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionsId: Long = 0,
    var itemsList: List<SaleItemEntity>,
    var totalAmount: Double,
    var createdOn: Long
)

data class SaleItemEntity(
    val productId: String,
    val productName: String,
    val totalPrice: Double,
    val quantity: Int = 0,
    val weight: Double = 0.0,
    val createdOn: Long = 0,
)
