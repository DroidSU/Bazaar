package com.sujoy.authentication.data

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sujoy.authentication.repository.AuthRepository
import com.sujoy.authentication.repository.AuthResult
import com.sujoy.authentication.repository.PhoneAuthEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun signInWithCredentials(credential: AuthCredential): Flow<AuthResult> = flow {
        try {
            auth.signInWithCredential(credential).await()
            Log.d(TAG, "Firebase sign-in with credential successful.")
            emit(AuthResult.Success)
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase sign-in failed: ${e.errorCode}", e)
            emit(AuthResult.Failure(e.message ?: "An unknown authentication error occurred."))
        }
    }.catch { e ->
        Log.e(TAG, "An unexpected error occurred.", e)
        emit(AuthResult.Failure(e.message ?: "An unexpected error occurred."))
    }

    override fun sendVerificationCode(
        phoneNumber: String
    ): Flow<PhoneAuthEvent> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                trySend(PhoneAuthEvent.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneAuthEvent.Error(e.message ?: "Verification failed"))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(PhoneAuthEvent.CodeSent(verificationId))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            Log.d(TAG, "callbackFlow for sendVerificationCode closed.")
        }
    }

    override fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, otpCode)
    }
}
