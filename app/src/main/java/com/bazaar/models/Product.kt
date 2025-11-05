package com.bazaar.models

import java.io.Serializable

data class Product(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = ""
) : Serializable