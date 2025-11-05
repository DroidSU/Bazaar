package com.bazaar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductScreenState {
    object Loading : ProductScreenState()
    data class Success(val products: List<Product>) : ProductScreenState()
    data class Error(val message: String) : ProductScreenState()
}

class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _products = MutableStateFlow<ProductScreenState>(ProductScreenState.Loading)
    val products: StateFlow<ProductScreenState> = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                repository.getProducts().collect {
                    _products.value = ProductScreenState.Success(it)
                }
            } catch (e: Exception) {
                _products.value = ProductScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }
}