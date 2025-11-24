package com.bazaar.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bazaar.models.Product
import com.bazaar.repository.ProductRepository
import kotlinx.coroutines.launch

class AddProductViewModel(private val repository: ProductRepository) : ViewModel() {

    var productName by mutableStateOf("")
    var productQuantity by mutableStateOf("")
    var productPrice by mutableStateOf("")
    var isSaving by mutableStateOf(false)
        private set

    fun onNameChange(name: String) {
        productName = name
    }

    fun onQuantityChange(quantity: String) {
        productQuantity = quantity
    }

    fun onPriceChange(price: String) {
        productPrice = price
    }

    fun saveProduct(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = productName.trim()
        val quantity = productQuantity.trim().toIntOrNull()
        val price = productPrice.trim().toDoubleOrNull()

        if (name.isBlank() || quantity == null || price == null) {
            onError("Please fill all fields correctly.")
            return
        }

        isSaving = true
        viewModelScope.launch {
            val product = Product(name = name, quantity = quantity, price = price)
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
