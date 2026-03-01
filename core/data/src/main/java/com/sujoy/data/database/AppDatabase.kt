package com.sujoy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sujoy.common.ConstantsManager
import com.sujoy.data.database.dao.Converters
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.models.Product
import com.sujoy.data.models.TransactionEntity
import com.sujoy.data.models.UserEntity

@Database(
    entities = [Product::class, TransactionEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDAO
    abstract fun transactionsDao(): TransactionsDAO


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `${ConstantsManager.TABLE_USER}` (" +
                            "`userId` TEXT NOT NULL, " +
                            "`mobileNumber` TEXT NOT NULL, " +
                            "`lastSyncTimestamp` INTEGER NOT NULL, " +
                            "`syncState` TEXT NOT NULL, " +
                            "PRIMARY KEY(`userId`))"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    ConstantsManager.DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}