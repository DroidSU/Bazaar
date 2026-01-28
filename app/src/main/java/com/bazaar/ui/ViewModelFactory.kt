package com.bazaar.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sujoy.authentication.data.AuthRepositoryImpl
import com.sujoy.authentication.vm.AuthViewModel
import com.sujoy.dashboard.DashboardViewModel
import com.sujoy.data.repository.DashboardRepositoryImpl
import com.sujoy.data.repository.ProductRepositoryImpl
import com.sujoy.data.repository.TransactionsRepositoryImpl
import com.sujoy.database.AppDatabase
import com.sujoy.products.viewmodels.AddProductViewModel
import com.sujoy.products.viewmodels.EditProductsViewModel
import com.sujoy.products.viewmodels.ProductsActivityViewModel
import com.sujoy.transactions.viewmodels.TransactionsViewModel

class ViewModelFactory(private val context : Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val productRepository = ProductRepositoryImpl()
        
        return when {
            modelClass.isAssignableFrom(AddProductViewModel::class.java) -> {
                AddProductViewModel(productRepository) as T
            }
            modelClass.isAssignableFrom(ProductsActivityViewModel::class.java) -> {
                ProductsActivityViewModel(productRepository, db.productsDao()) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(EditProductsViewModel::class.java) -> {
                EditProductsViewModel(productRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(DashboardRepositoryImpl(db.productsDao())) as T
            }
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> {
                TransactionsViewModel(TransactionsRepositoryImpl(db.productsDao()), db.transactionsDao()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
