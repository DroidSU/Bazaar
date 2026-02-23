package com.sujoy.data.repository

sealed class NetworkResult {
    data class Success(val data: Any?) : NetworkResult()
    data class Error(val message: String) : NetworkResult()
}