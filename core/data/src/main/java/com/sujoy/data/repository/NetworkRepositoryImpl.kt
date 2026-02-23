package com.sujoy.data.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sujoy.common.ConstantsManager
import com.sujoy.data.models.PhoneAuthEvent
import com.sujoy.data.models.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor() : NetworkRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signInWithCredentials(credential: AuthCredential): NetworkResult =
        try {
        auth.signInWithCredential(credential).await()
        NetworkResult.Success(null)
    }
        catch (e: FirebaseAuthException) {
        Log.e(ConstantsManager.APP_TAG, "Firebase sign-in failed: ${e.errorCode}", e)
        FirebaseCrashlytics.getInstance().recordException(e)
        NetworkResult.Error(e.message ?: "An unknown authentication error occurred.")
    }

    override fun sendVerificationCode(
        phoneNumber: String
    ): Flow<PhoneAuthEvent> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(ConstantsManager.APP_TAG, "Verification completed")
                trySend(PhoneAuthEvent.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneAuthEvent.Error(e.message ?: "Verification failed"))
                FirebaseCrashlytics.getInstance().log("callbackFlow for sendVerificationCode closed.")
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
            Log.d(ConstantsManager.APP_TAG, "callbackFlow for sendVerificationCode closed.")
        }
    }

    override fun getPhoneAuthCredential(verificationId: String, otpCode: String): AuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, otpCode)
    }

    override suspend fun getProducts(): Flow<Result<List<Product>>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeProductList(products: List<Product>) {
        TODO("Not yet implemented")
    }

}