package com.bazaar.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.models.SaleItemModel
import com.bazaar.repository.TransactionsRepository
import com.bazaar.utils.ConstantsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: TransactionsRepository) : ViewModel() {
    private val _salesList = MutableStateFlow<List<SaleItemModel>>(emptyList())
    val salesList = _salesList.asStateFlow()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList = _productList.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow<Int>(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    private val _selectedSalesProduct = MutableStateFlow<Product?>(null)
    val selectedSalesProduct = _selectedSalesProduct.asStateFlow()

    private val _selectedQuantityForSales = MutableStateFlow(1)
    val selectedQuantityForSales = _selectedQuantityForSales.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount = _totalAmount.asStateFlow()


    init {
        getProductListFromDB()
    }

    private fun getProductListFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getProducts().collect { result ->
                    result.onSuccess { products ->
                        _productList.value = products
                    }.onFailure { exception ->
                        _productList.value = emptyList()
                        Log.e(
                            ConstantsManager.APP_TAG,
                            "getProductListFromDB: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(ConstantsManager.APP_TAG, "getProductListFromDB: Error fetching from DB", e)
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    fun onSalesProductSelected(productID: String) {
        _selectedSalesProduct.value = _productList.value.find { it.id == productID }
        _selectedQuantityForSales.value = 1
        getTotalAmount()
    }

    fun onSalesQuantityChanged(quantity: Int) {
        _selectedQuantityForSales.value = quantity
        getTotalAmount()
    }

    fun getTotalAmount() {
        _totalAmount.value = _selectedSalesProduct.value?.price?.times(_selectedQuantityForSales.value) ?: 0.0
    }
}