package com.sujoy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sujoy.common.ConstantsManager
import com.sujoy.data.database.dao.Converters
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.models.Product
import com.sujoy.data.models.TransactionEntity

@Database(entities = [Product::class, TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDAO
    abstract fun transactionsDao(): TransactionsDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    ConstantsManager.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}