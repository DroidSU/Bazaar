package com.bazaar.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bazaar.utils.ConstantsManager
import com.google.firebase.firestore.PropertyName
import com.sujoy.designsystem.utils.WeightUnit

@Entity(ConstantsManager.TABLE_PRODUCTS)
data class Product(
    @PrimaryKey @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0,
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("weight") @set:PropertyName("weight") var weight: Double = 0.0,
    @get:PropertyName("weightUnit") @set:PropertyName("weightUnit") var weightUnit: String = WeightUnit.KG.toString(),
    @get:PropertyName("createdOn") @set:PropertyName("createdOn") var createdOn: Long = 0,
    @get:PropertyName("isDeleted") @set:PropertyName("isDeleted") var isDeleted: Boolean = false,
    @get:PropertyName("lastUpdated") @set:PropertyName("lastUpdated") var lastUpdated: Long = 0,
    @get:PropertyName("thresholdValue") @set:PropertyName("thresholdValue") var thresholdValue: Double = 0.0,
)


