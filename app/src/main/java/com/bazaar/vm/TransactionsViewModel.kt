package com.bazaar.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.dao.TransactionsDAO
import com.bazaar.models.CheckoutState
import com.bazaar.models.SaleItemModel
import com.bazaar.models.Transactions
import com.bazaar.repository.TransactionsRepository
import com.sujoy.common.ConstantsManager
import com.sujoy.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.find

class TransactionsViewModel(private val repository: TransactionsRepository, private val transactionsDAO: TransactionsDAO) : ViewModel() {
    private val _salesList = MutableStateFlow<List<SaleItemModel>>(emptyList())
    val salesList = _salesList.asStateFlow()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList = _productList.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow<Int>(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    private val _selectedSalesProduct = MutableStateFlow<Product?>(null)
    val selectedSalesProduct = _selectedSalesProduct.asStateFlow()

    private val _selectedQuantityForSales = MutableStateFlow(0)
    val selectedQuantityForSales = _selectedQuantityForSales.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount = _totalAmount.asStateFlow()

    private val _checkoutState = MutableSharedFlow<CheckoutState>()
    val checkoutState = _checkoutState.asSharedFlow()

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
        _selectedQuantityForSales.value = 0
    }

    fun onSalesQuantityChanged(isNegative: Boolean) {
        if(isNegative){
            _selectedQuantityForSales.value -= 1
        }
        else{
            _selectedQuantityForSales.value += 1
        }
    }

    fun onAddToCart() {
        _totalAmount.value += (_selectedSalesProduct.value?.price ?: 0.0) * _selectedQuantityForSales.value

        val saleItemModel = SaleItemModel(
            productId = _selectedSalesProduct.value?.id ?: "",
            productName = _selectedSalesProduct.value?.name ?: "",
            quantity = _selectedQuantityForSales.value,
            weight = _selectedSalesProduct.value?.weight ?: 0.0,
            totalPrice = _selectedSalesProduct.value?.price?.times(_selectedQuantityForSales.value) ?: 0.0,
            createdOn = System.currentTimeMillis()
        )
        _salesList.value += saleItemModel

        _selectedSalesProduct.value = null
        _selectedQuantityForSales.value = 0
    }

    fun onCheckout() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val transactions = Transactions(
                    itemsList = _salesList.value,
                    totalAmount = _totalAmount.value,
                    createdOn = System.currentTimeMillis()
                )
                transactionsDAO.insertTransaction(transactions)
                _salesList.value = emptyList()
                _totalAmount.value = 0.0
                Log.d(ConstantsManager.APP_TAG, "onCheckout: Transaction added successfully")
                _checkoutState.emit(CheckoutState.Success)
            }
            catch(e: Exception) {
                Log.e(ConstantsManager.APP_TAG, "onCheckout: Error adding transaction")
                _checkoutState.emit(CheckoutState.Error("Error adding transaction"))
            }
        }
    }

    fun onRemoveItem(index: Int) {
        val currentList = _salesList.value.toMutableList()
        if(index in currentList.indices){
            val itemToRemove = currentList.removeAt(index)
            _totalAmount.value -= itemToRemove.totalPrice
            _salesList.value = currentList
        }
    }
}