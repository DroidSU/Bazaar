package com.bazaar.models

sealed interface CheckoutState {
    object Idle : CheckoutState
    object Success : CheckoutState
    data class Error(val message: String) : CheckoutState
}