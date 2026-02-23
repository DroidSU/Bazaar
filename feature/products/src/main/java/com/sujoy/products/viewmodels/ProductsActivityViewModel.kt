package com.sujoy.products.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.common.AppUIState
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.models.Product
import com.sujoy.data.repository.ProductRepository
import com.sujoy.designsystem.utils.WeightUnit
import com.sujoy.products.SortOption
import com.sujoy.products.models.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductsActivityViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val productsDAO: ProductsDAO
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUIState>(AppUIState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NAME_ASC)
    val sortOption = _sortOption.asStateFlow()

    private var originalProducts = listOf<Product>()

    private val _productsList = MutableStateFlow<List<Product>>(emptyList())
    val productsList = _productsList.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    init {
        getProductsFromDB()
    }

    private fun getProductsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProducts()
                .catch { exception ->
                    _uiState.value =
                        AppUIState.Error(exception.message ?: "An unknown error occurred")
                }
                .collect { result ->
                result.onSuccess { products ->
                    originalProducts = products.filter { !it.isDeleted }
                    filterProducts()
                }.onFailure {
                    _uiState.value =
                        AppUIState.Error(it.message ?: "An unknown error occurred")
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    private fun filterProducts() {
        val query = _searchQuery.value
        val filteredList = if (query.isBlank()) {
            originalProducts
        } else {
            originalProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        val sortedList = when (_sortOption.value) {
            SortOption.NAME_ASC -> filteredList.sortedBy { it.name }
            SortOption.NAME_DESC -> filteredList.sortedByDescending { it.name }
            SortOption.STOCK_ALERTS -> filteredList.sortedByDescending { it.quantity < it.thresholdValue }
            SortOption.PRICE_HIGH_TO_LOW -> filteredList.sortedByDescending { it.price }
            SortOption.PRICE_LOW_TO_HIGH -> filteredList.sortedBy { it.price }
        }

        _uiState.value = AppUIState.Success
        _productsList.value = sortedList
    }

    fun onDismissUpload() {
        _uploadState.value = UploadState.Idle
    }

    fun uploadProductsFromCsv(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading(0)
            try {
                val products = parseCsv(contentResolver, uri)
                if (products.isEmpty()) {
                    _uploadState.value = UploadState.Error("CSV is empty or invalid.")
                    return@launch
                }

                products.forEachIndexed { index, product ->
                    repository.addProducts(product)
                    val progress = ((index + 1) * 100 / products.size)
                    _uploadState.value = UploadState.Uploading(progress)
                }

                _uploadState.value = UploadState.Success
                delay(2000)
                _uploadState.value = UploadState.Idle

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "An unknown error occurred")
                e.printStackTrace()
            }
        }
    }

    private suspend fun parseCsv(contentResolver: ContentResolver, uri: Uri): List<Product> {
        return withContext(Dispatchers.IO) {
            val products = mutableListOf<Product>()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val tokens = line!!.split(",")
                        if (tokens.size >= 3) {
                            try {
                                val product = Product(
                                    id = UUID.randomUUID().toString(),
                                    name = tokens[0].trim(),
                                    quantity = tokens.getOrNull(1)?.trim()?.toIntOrNull() ?: 0,
                                    price = tokens.getOrNull(2)?.trim()?.toDoubleOrNull() ?: 0.0,
                                    weight = tokens.getOrNull(3)?.trim()?.toDoubleOrNull() ?: 0.0,
                                    weightUnit = tokens.getOrNull(4)?.trim()?.uppercase()
                                        .takeIf { !it.isNullOrEmpty() } ?: WeightUnit.KG.toString(),
                                    createdOn = System.currentTimeMillis()
                                )
                                products.add(product)
                            } catch (e: NumberFormatException) {
                                println("Skipping invalid line: $line")
                            }
                        }
                    }
                }
            }
            products
        }
    }

    fun onDeleteProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(isDeleted = true))
        }
    }

    fun onSortOptionChange(sortOption: SortOption) {
        _sortOption.value = sortOption
        filterProducts()
    }
}
