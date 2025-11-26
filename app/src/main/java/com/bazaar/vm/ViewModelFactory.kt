package com.bazaar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bazaar.data.repository.AuthRepositoryImpl
import com.bazaar.repository.ProductRepositoryImpl

class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddProductViewModel::class.java) -> {
                AddProductViewModel(ProductRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(ProductsActivityViewModel::class.java) -> {
                ProductsActivityViewModel(ProductRepositoryImpl()) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepositoryImpl()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
