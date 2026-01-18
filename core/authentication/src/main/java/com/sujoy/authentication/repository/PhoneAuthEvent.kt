package com.sujoy.authentication.repository

import com.google.firebase.auth.AuthCredential

sealed class PhoneAuthEvent {
    data class CodeSent(val verificationId: String) : PhoneAuthEvent()
    data class VerificationCompleted(val credential: AuthCredential) : PhoneAuthEvent()
    data class Error(val message: String) : PhoneAuthEvent()
}