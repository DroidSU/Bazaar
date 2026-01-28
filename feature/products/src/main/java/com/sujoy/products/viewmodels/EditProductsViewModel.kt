package com.sujoy.products.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.common.AppUIState
import com.sujoy.data.repository.ProductRepository
import com.sujoy.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onUpdateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = AppUIState.Loading

            val result = repository.updateProduct(product)
            result.onSuccess {
                _uiState.value = AppUIState.Success
            }.onFailure {
                _uiState.value = AppUIState.Error(it.message ?: "Update Failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = AppUIState.Idle
    }
}
