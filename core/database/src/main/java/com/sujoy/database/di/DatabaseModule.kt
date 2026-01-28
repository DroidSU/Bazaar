package com.sujoy.database.di

import android.content.Context
import com.sujoy.database.AppDatabase
import com.sujoy.database.dao.ProductsDAO
import com.sujoy.database.dao.TransactionsDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideProductsDao(database: AppDatabase): ProductsDAO {
        return database.productsDao()
    }

    @Provides
    fun provideTransactionsDao(database: AppDatabase): TransactionsDAO {
        return database.transactionsDao()
    }
}
