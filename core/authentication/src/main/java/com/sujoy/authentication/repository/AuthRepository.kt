package com.sujoy.authentication.repository

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Signs in the user with the given Firebase credentials.
     * @return A Flow that emits the result of the authentication event.
     */
    fun signInWithCredentials(credential: AuthCredential): Flow<AuthResult>

    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    /**
     * Creates a credential from the verification ID and OTP code.
     */
    fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential
}