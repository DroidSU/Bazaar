package com.bazaar.repository

import com.bazaar.models.Product

interface DashboardUiState {
    data object Loading : DashboardUiState
    data class ProductFetchSuccess(val products: List<Product>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}