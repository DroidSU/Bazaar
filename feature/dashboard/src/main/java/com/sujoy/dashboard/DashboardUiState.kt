package com.sujoy.dashboard

import com.sujoy.model.Product

data class DashboardUiState(
    val productList: List<Product> = emptyList(),
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val isSignedOut: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
