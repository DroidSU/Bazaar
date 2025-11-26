package com.bazaar.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.ProductRepository
import com.bazaar.utils.WeightUnit
import kotlinx.coroutines.launch

class AddProductViewModel(private val repository: ProductRepository) : ViewModel() {

    var productName by mutableStateOf("")
    var productQuantity by mutableStateOf("")
    var productPrice by mutableStateOf("")
    var productWeight by mutableStateOf("")
        private set
    var weightUnit by mutableStateOf(WeightUnit.KG)
        private set
    var isSaving by mutableStateOf(false)
        private set

    fun onNameChange(name: String) {
        productName = name
    }

    fun onQuantityChange(quantity: String) {
        productQuantity = quantity.filter { it.isDigit() }
    }

    fun onWeightChange(newWeight: String) {
        val parts = newWeight.split('.')
        if (newWeight.count { it == '.' } <= 1 && (parts.getOrNull(1)?.length ?: 0) <= 2) {
            productWeight = newWeight
        }
    }

    fun onUnitChange(newUnit: WeightUnit) {
        weightUnit = newUnit
    }

    fun onPriceChange(price: String) {
        productPrice = price
    }

    fun saveProduct(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = productName.trim()
        val quantity = productQuantity.trim().toIntOrNull()
        val price = productPrice.trim().toDoubleOrNull()

        if (productName.isEmpty()) {
            onError("Product name cannot be empty.")
            return
        }
        else if (productQuantity.isEmpty() && productWeight.isEmpty()) {
            onError("Please enter either quantity or weight.")
            return
        }
        else if (productPrice.isEmpty()) {
            onError("Product price cannot be empty.")
            return
        }
        else {
            isSaving = true
            viewModelScope.launch {
                val product = Product(name = name, quantity = quantity!!, price = price!!, createdOn = System.currentTimeMillis())
                val result = repository.addProducts(product)
                result.onSuccess {
                    onSuccess
                }.onFailure {
                    onError(it.message ?: "An unknown error occurred.")
                }
                isSaving = false
            }
        }
    }
}
