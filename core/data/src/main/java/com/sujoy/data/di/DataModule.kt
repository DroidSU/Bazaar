package com.sujoy.data.di

import com.sujoy.data.repository.DashboardRepository
import com.sujoy.data.repository.DashboardRepositoryImpl
import com.sujoy.data.repository.ProductRepository
import com.sujoy.data.repository.ProductRepositoryImpl
import com.sujoy.data.repository.TransactionsRepository
import com.sujoy.data.repository.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

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
}
