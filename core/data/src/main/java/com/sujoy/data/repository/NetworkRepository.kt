package com.sujoy.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.sujoy.data.models.PhoneAuthEvent
import com.sujoy.data.models.Product
import com.sujoy.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

interface NetworkRepository{

    suspend fun signInWithCredentials(credential: AuthCredential): NetworkResult

    fun sendVerificationCode(
        phoneNumber: String
    ): Flow<PhoneAuthEvent>

    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
    fun getProducts(): Flow<List<Product>>
    suspend fun storeProductList(products: List<Product>)

    suspend fun isNetworkAvailable() : Boolean

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser
    fun getUserDetails() : Flow<UserEntity>
}