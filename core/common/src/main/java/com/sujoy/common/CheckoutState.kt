package com.sujoy.common

sealed interface CheckoutState {
    data object Idle : CheckoutState
    data object Success : CheckoutState
    data class Error(val message: String) : CheckoutState
}