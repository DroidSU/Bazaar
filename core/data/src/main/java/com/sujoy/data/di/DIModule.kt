package com.sujoy.data.di

import android.content.Context
import com.sujoy.data.database.AppDatabase
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.database.dao.TransactionsDAO
import com.sujoy.data.repository.DashboardRepository
import com.sujoy.data.repository.DashboardRepositoryImpl
import com.sujoy.data.repository.DatabaseRepository
import com.sujoy.data.repository.DatabaseRepositoryImpl
import com.sujoy.data.repository.NetworkRepository
import com.sujoy.data.repository.NetworkRepositoryImpl
import com.sujoy.data.repository.ProductRepository
import com.sujoy.data.repository.ProductRepositoryImpl
import com.sujoy.data.repository.TransactionsRepository
import com.sujoy.data.repository.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DIModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindDatabaseRepository(
        databaseRepositoryImpl: DatabaseRepositoryImpl
    ): DatabaseRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(
        transactionsRepositoryImpl: TransactionsRepositoryImpl
    ): TransactionsRepository

    companion object {
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
}
