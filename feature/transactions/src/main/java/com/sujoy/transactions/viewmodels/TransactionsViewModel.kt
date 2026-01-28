package com.sujoy.transactions.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.data.repository.TransactionsRepository
import com.sujoy.database.dao.TransactionsDAO
import com.sujoy.database.model.SaleItemEntity
import com.sujoy.database.model.TransactionEntity
import com.sujoy.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CheckoutState {
    data object Idle : CheckoutState
    data object Success : CheckoutState
    data class Error(val message: String) : CheckoutState
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val transactionsDAO: TransactionsDAO
) : ViewModel() {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()

    private val _salesList = MutableStateFlow<List<SaleItemEntity>>(emptyList())
    val salesList: StateFlow<List<SaleItemEntity>> = _salesList.asStateFlow()

    private val _selectedSalesProduct = MutableStateFlow<Product?>(null)
    val selectedSalesProduct: StateFlow<Product?> = _selectedSalesProduct.asStateFlow()

    private val _selectedQuantityForSales = MutableStateFlow(1)
    val selectedQuantityForSales: StateFlow<Int> = _selectedQuantityForSales.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()

    private val _checkoutState = MutableSharedFlow<CheckoutState>()
    val checkoutState: SharedFlow<CheckoutState> = _checkoutState.asSharedFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { /* Handle error */ }
                .collect { products ->
                    _productList.value = products.filter { !it.isDeleted }
                }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    fun onSalesProductSelected(productId: String) {
        val product = _productList.value.find { it.id == productId }
        _selectedSalesProduct.value = product
        _selectedQuantityForSales.value = 1
    }

    fun onSalesQuantityChanged(isDecrease: Boolean) {
        if (isDecrease) {
            if (_selectedQuantityForSales.value > 1) {
                _selectedQuantityForSales.value -= 1
            }
        } else {
            _selectedQuantityForSales.value += 1
        }
    }

    fun onAddToCart() {
        val product = _selectedSalesProduct.value ?: return
        val quantity = _selectedQuantityForSales.value
        val saleItem = SaleItemEntity(
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            weight = 0.0,
            totalPrice = product.price * quantity
        )
        _salesList.value = _salesList.value + saleItem
        calculateTotal()
        _selectedSalesProduct.value = null
        _selectedQuantityForSales.value = 1
    }

    fun onRemoveItem(index: Int) {
        val list = _salesList.value.toMutableList()
        list.removeAt(index)
        _salesList.value = list
        calculateTotal()
    }

    private fun calculateTotal() {
        _totalAmount.value = _salesList.value.sumOf { it.totalPrice }
    }

    fun onCheckout() {
        if (_salesList.value.isEmpty()) return

        viewModelScope.launch {
            val transaction = TransactionEntity(
                itemsList = _salesList.value,
                totalAmount = _totalAmount.value,
                createdOn = System.currentTimeMillis()
            )

            transactionsDAO.insertTransaction(transaction)

            _salesList.value.forEach { saleItem ->
                val product = _productList.value.find { it.id == saleItem.productId }
                product?.let {
                    repository.updateProductInDB(it.id, (product.quantity - saleItem.quantity))
                }
            }

            _salesList.value = emptyList()
            calculateTotal()
            _checkoutState.emit(CheckoutState.Success)
        }
    }
}
