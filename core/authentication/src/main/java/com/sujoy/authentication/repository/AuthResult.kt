package com.sujoy.authentication.repository

sealed interface AuthResult {
    data object Success : AuthResult
    data class Failure(val message: String) : AuthResult
}