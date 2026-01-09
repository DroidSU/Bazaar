package com.bazaar.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bazaar.models.Product
import com.bazaar.utils.ConstantsManager

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM ${ConstantsManager.COLLECTION_PRODUCTS}")
    fun getAllProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productList: List<Product>)
}