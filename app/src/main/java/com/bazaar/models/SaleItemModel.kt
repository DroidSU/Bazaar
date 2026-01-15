package com.bazaar.models

data class SaleItemModel(
    val productId: String,
    val productName: String,
    val totalPrice: Double,
    val quantity: Int = 0,
    val weight: Double = 0.0,
    val createdOn: Long = 0,
)
