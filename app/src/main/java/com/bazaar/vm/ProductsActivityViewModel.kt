package com.bazaar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.ProductRepository
import com.bazaar.repository.ProductsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductsActivityViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var originalProducts = listOf<Product>()

    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    private val _isSavingUpdate = MutableStateFlow(false)
    val isSavingUpdate: StateFlow<Boolean> = _isSavingUpdate.asStateFlow()


    init {
        collectProducts()
    }

    private fun collectProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { exception ->
                    _uiState.value =
                        ProductsUiState.Error(exception.message ?: "An unknown error occurred")
                }
                .collect { result ->
                    result.onSuccess { products ->
                        originalProducts = products
                        filterProducts() // Filter the new list
                    }.onFailure {
                        _uiState.value =
                            ProductsUiState.Error(it.message ?: "An unknown error occurred")
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    private fun filterProducts() {
        val query = _searchQuery.value
        val filteredList = if (query.isBlank()) {
            originalProducts
        } else {
            originalProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = ProductsUiState.Success(filteredList)
    }

    fun onEditProductClicked(product: Product) {
        _editingProduct.value = product
    }

    fun onDismissEdit() {
        _editingProduct.value = null
    }

    fun onUpdateProduct(product: Product) {
        viewModelScope.launch {
            _isSavingUpdate.value = true
            val result = repository.updateProduct(product)
            result.onSuccess {
                _isSavingUpdate.value = false
                onDismissEdit() // Close the sheet on success
            }.onFailure {
                // Handle error (e.g., show a toast)
                _isSavingUpdate.value = false
            }
        }
    }

}
