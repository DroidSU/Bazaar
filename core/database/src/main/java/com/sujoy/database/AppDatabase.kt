package com.sujoy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sujoy.database.dao.Converters
import com.sujoy.database.dao.ProductsDAO
import com.sujoy.database.dao.TransactionsDAO
import com.sujoy.database.model.TransactionEntity
import com.sujoy.model.Product

@Database(entities = [Product::class, TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDAO
    abstract fun transactionsDao(): TransactionsDAO
}
