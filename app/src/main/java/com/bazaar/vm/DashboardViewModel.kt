package com.bazaar.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _isSignedOut = MutableStateFlow(false)
    val isSignedOut = _isSignedOut.asStateFlow()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList = _productList.asStateFlow()

    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount = _lowStockCount.asStateFlow()

    private val _outOfStockCount = MutableStateFlow(0)
    val outOfStockCount = _outOfStockCount.asStateFlow()


    init {
        collectProducts()
    }

    private fun collectProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { exception ->
                    Log.e("Bazaar", "collectProducts: ${exception.message.toString()}")
                    _productList.value = emptyList()
                }
                .collect { result ->
                    result.onSuccess { products ->
                        _productList.value = products
                        storeProductsInDB(products)
                        getLowStockCount()
                        getOutOfStockCount()
                    }.onFailure {
                        _productList.value = emptyList()
                        Log.e("Bazaar", "collectProducts: ${it.message.toString()}")
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

    private fun getProductListFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getProducts().collect { result ->
                    result.onSuccess { products ->
                        _productList.value = products
                    }.onFailure {
                        _productList.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("Bazaar", "getProductListFromDB: Error fetching from DB", e)
            }
        }
    }

    private fun getLowStockCount() {
        productList.value.count { it.quantity < it.thresholdValue }.also { _lowStockCount.value = it }
    }

    private fun getOutOfStockCount() {
        productList.value.count { it.quantity == 0 }.also { _outOfStockCount.value = it}
    }

    fun onSignOut() {
        viewModelScope.launch {
            repository.signOut()
            _isSignedOut.value = true
        }
    }
}