package com.bazaar.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.sujoy.common.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_TRANSACTIONS)
data class Transactions(
    @PrimaryKey(autoGenerate = true) val transactionsId: Long = 0,
    @get:PropertyName("item_list") @set:PropertyName("item_list") var itemsList: List<SaleItemModel>,
    @get:PropertyName("total_amount") @set:PropertyName("total_amount") var totalAmount: Double,
    @get:PropertyName("created_on") @set:PropertyName("created_on") var createdOn: Long
)
