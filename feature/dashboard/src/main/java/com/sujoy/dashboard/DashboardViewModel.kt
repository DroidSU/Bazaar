package com.sujoy.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sujoy.common.AppUIState
import com.sujoy.data.models.Product
import com.sujoy.data.repository.DatabaseRepository
import com.sujoy.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository,
) : ViewModel() {
    
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
            try{
                databaseRepository.getProductsFromLocal().collect { products ->
                    _productList.value = products
                    _lowStockCount.value = products.count { it.quantity > 0 && it.quantity < it.thresholdValue }
                    _outOfStockCount.value = products.count { it.quantity == 0 }
                    _uiState.value = AppUIState.Success
                }
            }
            catch (ex : Exception) {
                _uiState.value = AppUIState.Error(ex.message ?: "Unknown error")
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            networkRepository.signOut()
            _isSignedOut.value = true
        }
    }
}
