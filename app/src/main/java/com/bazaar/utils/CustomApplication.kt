package com.bazaar.utils

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bazaar.dao.Converters
import com.bazaar.dao.TransactionsDAO
import com.bazaar.db.dao.ProductsDAO
import com.bazaar.models.Product
import com.bazaar.models.Transactions
import com.google.firebase.FirebaseApp

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}

@Database(entities = [Product::class, Transactions::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductsDAO
    abstract fun transactionsDAO(): TransactionsDAO

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
                    "bazaar_database" // Your database name
                )
                    .fallbackToDestructiveMigration() // Useful during development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}