package com.bazaar.models

import com.bazaar.utils.WeightUnit
import com.google.firebase.firestore.PropertyName

data class Product(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0,
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("weight") @set:PropertyName("weight") var weight: Double = 0.0,
    @get:PropertyName("weightUnit") @set:PropertyName("weightUnit") var weightUnit: String = WeightUnit.KG.toString(),
    @get:PropertyName("createdOn") @set:PropertyName("createdOn") var createdOn: Long = 0,
)


