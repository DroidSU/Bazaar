package com.bazaar.repository

sealed interface AddProductUiState {
    data object Loading : AddProductUiState
    data object Success : AddProductUiState
    data class Error(val message: String) : AddProductUiState
}