package com.sujoy.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.common.AppUIState
import com.sujoy.data.repository.DashboardRepository
import com.sujoy.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Loading)
    val uiState: StateFlow<AppUIState> = _uiState.asStateFlow()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()

    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount: StateFlow<Int> = _lowStockCount.asStateFlow()

    private val _outOfStockCount = MutableStateFlow(0)
    val outOfStockCount: StateFlow<Int> = _outOfStockCount.asStateFlow()

    private val _isSignedOut = MutableStateFlow(false)
    val isSignedOut: StateFlow<Boolean> = _isSignedOut.asStateFlow()

    init {
        collectProducts()
    }

    private fun collectProducts() {
        viewModelScope.launch {
            _uiState.value = AppUIState.Loading
            repository.getProducts()
                .catch { exception ->
                    Log.e("Bazaar", "collectProducts: ${exception.message.toString()}")
                    _uiState.value = AppUIState.Error(exception.message ?: "Unknown error")
                }
                .collect { result ->
                    result.onSuccess { products ->
                        _productList.value = products
                        _lowStockCount.value = products.count { it.quantity > 0 && it.quantity < it.thresholdValue }
                        _outOfStockCount.value = products.count { it.quantity == 0 }
                        _uiState.value = AppUIState.Success
                        storeProductsInDB(products)
                    }.onFailure { exception ->
                        _uiState.value = AppUIState.Error(exception.message ?: "Unknown error")
                        Log.e("Bazaar", "collectProducts: ${exception.message.toString()}")
                    }
                }
        }
    }

    private fun storeProductsInDB(productsList: List<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.storeProductList(productsList)
            } catch (e: Exception) {
                Log.e("Bazaar", "storeProductsInDB: Error saving to DB", e)
            }
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            repository.signOut()
            _isSignedOut.value = true
        }
    }
}
