package com.bazaar.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bazaar.repository.DashboardRepositoryImpl
import com.bazaar.repository.ProductRepositoryImpl
import com.bazaar.repository.TransactionsRepositoryImpl
import com.bazaar.utils.AppDatabase
import com.sujoy.authentication.data.AuthRepositoryImpl
import com.sujoy.authentication.vm.AuthViewModel

class ViewModelFactory(private val context : Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddProductViewModel::class.java) -> {
                AddProductViewModel(ProductRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(ProductsActivityViewModel::class.java) -> {
                val db = AppDatabase.getInstance(context)
                ProductsActivityViewModel(ProductRepositoryImpl(), db.productDao()) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(EditProductsViewModel::class.java) -> {
                EditProductsViewModel(ProductRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                val db = AppDatabase.getInstance(context)
                DashboardViewModel(DashboardRepositoryImpl(db.productDao())) as T
            }
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> {
                val db = AppDatabase.getInstance(context)
                TransactionsViewModel(TransactionsRepositoryImpl(db.productDao()), db.transactionsDAO()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
