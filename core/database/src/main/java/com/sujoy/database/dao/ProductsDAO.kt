package com.sujoy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sujoy.common.ConstantsManager
import com.sujoy.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM ${ConstantsManager.TABLE_PRODUCTS}")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(productList: List<Product>)
}
