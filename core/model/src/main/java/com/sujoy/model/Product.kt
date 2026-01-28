package com.sujoy.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sujoy.common.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_PRODUCTS)
data class Product(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var weight: Double = 0.0,
    var weightUnit: String = WeightUnit.KG.toString(),
    var createdOn: Long = 0,
    var isDeleted: Boolean = false,
    var lastUpdated: Long = 0,
    var thresholdValue: Double = 0.0,
)
