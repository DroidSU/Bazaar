package com.bazaar.utils

import com.bazaar.models.Product

class UtilityManager {

    fun getProductById(productList : List<Product>, id : String) : Product {
        return productList.first { it.productId == id }
    }
}