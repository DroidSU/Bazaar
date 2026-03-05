package com.sujoy.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sujoy.common.ConstantsManager
import com.sujoy.data.models.Product
import com.sujoy.data.models.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM ${ConstantsManager.TABLE_PRODUCTS}")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(productList: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM ${ConstantsManager.TABLE_PRODUCTS} WHERE id = :id")
    suspend fun getProductById(id: String): Product

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE ${ConstantsManager.TABLE_PRODUCTS} SET quantity = :newQuantity WHERE id = :productId")
    suspend fun updateProductQuantity(productId: String, newQuantity: Int)

    @Query("SELECT * FROM ${ConstantsManager.TABLE_PRODUCTS} WHERE syncState = :syncState")
    suspend fun getProductsBySyncState(syncState: SyncState): List<Product>
}
