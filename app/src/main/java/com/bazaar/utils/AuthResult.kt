package com.bazaar.utils

sealed class AuthResult {
    data object Success : AuthResult()
    data class Failure(val message: String) : AuthResult()
}