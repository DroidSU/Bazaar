package com.sujoy.data.repository

import com.google.firebase.auth.AuthCredential
import com.sujoy.data.models.PhoneAuthEvent
import com.sujoy.data.models.Product
import kotlinx.coroutines.flow.Flow

interface NetworkRepository{

    suspend fun signInWithCredentials(credential: AuthCredential): NetworkResult

    fun sendVerificationCode(
        phoneNumber: String
    ): Flow<PhoneAuthEvent>

    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
    suspend fun getProducts(): Flow<Result<List<Product>>>
    suspend fun storeProductList(products: List<Product>)
}