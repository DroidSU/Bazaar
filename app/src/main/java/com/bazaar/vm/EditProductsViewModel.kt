package com.bazaar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.EditProductsUiState
import com.bazaar.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProductsUiState>(EditProductsUiState.Idle)
    val uiState = _uiState.asStateFlow()


    fun onUpdateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = EditProductsUiState.Loading

            val result = repository.updateProduct(product)
            result.onSuccess {
                _uiState.value = EditProductsUiState.Success
            }.onFailure {
                _uiState.value = EditProductsUiState.Error(it.message ?: "Update Failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = EditProductsUiState.Idle
    }
}