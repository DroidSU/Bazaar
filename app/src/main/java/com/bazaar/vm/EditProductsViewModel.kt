package com.bazaar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.ProductRepository
import com.bazaar.repository.ProductsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // StateFlow to manage the state of the product being edited.
    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    // StateFlow to indicate if an update operation is in progress.
    private val _isSavingUpdate = MutableStateFlow(false)
    val isSavingUpdate: StateFlow<Boolean> = _isSavingUpdate.asStateFlow()


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
                _isSavingUpdate.value = false
            }
        }
    }
}