package com.bazaar.repository

sealed interface EditProductsUiState {
    data object Loading : EditProductsUiState
    data object Success : EditProductsUiState
    data class Error(val message: String) : EditProductsUiState
    data object Idle: EditProductsUiState
}