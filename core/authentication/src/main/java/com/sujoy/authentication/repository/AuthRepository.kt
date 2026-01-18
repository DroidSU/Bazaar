package com.sujoy.authentication.repository

import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithCredentials(credential: AuthCredential): Flow<AuthResult>

    fun sendVerificationCode(
        phoneNumber: String
    ): Flow<PhoneAuthEvent>

    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
}
