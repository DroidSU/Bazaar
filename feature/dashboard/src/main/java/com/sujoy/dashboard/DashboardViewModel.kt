package com.sujoy.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.data.repository.DashboardRepository
import com.sujoy.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        collectProducts()
    }

    private fun collectProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getProducts()
                .catch { exception ->
                    Log.e("Bazaar", "collectProducts: ${exception.message.toString()}")
                    _uiState.update { it.copy(productList = emptyList(), isLoading = false, error = exception.message) }
                }
                .collect { result ->
                    result.onSuccess { products ->
                        _uiState.update { state ->
                            state.copy(
                                productList = products,
                                lowStockCount = products.count { it.quantity < it.thresholdValue },
                                outOfStockCount = products.count { it.quantity == 0 },
                                isLoading = false,
                                error = null
                            )
                        }
                        storeProductsInDB(products)
                    }.onFailure { exception ->
                        _uiState.update { it.copy(productList = emptyList(), isLoading = false, error = exception.message) }
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
            _uiState.update { it.copy(isSignedOut = true) }
        }
    }
}
