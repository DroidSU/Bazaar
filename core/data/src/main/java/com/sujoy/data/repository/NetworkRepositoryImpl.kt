package com.sujoy.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.sujoy.common.ConstantsManager
import com.sujoy.data.database.dao.ProductsDAO
import com.sujoy.data.models.PhoneAuthEvent
import com.sujoy.data.models.Product
import com.sujoy.data.models.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productsDAO: ProductsDAO
) : NetworkRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun signInWithCredentials(credential: AuthCredential): NetworkResult =
        try {
            auth.signInWithCredential(credential).await()
            NetworkResult.Success(null)
        } catch (e: FirebaseAuthException) {
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
                FirebaseCrashlytics.getInstance()
                    .log("callbackFlow for sendVerificationCode closed.")
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

    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listenerRegistration = db.collection("products").document(auth.currentUser!!.uid)
            .collection("userProducts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(ConstantsManager.APP_TAG, "Error fetching products", error)
                    FirebaseCrashlytics.getInstance().recordException(error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.toObjects(Product::class.java)
                    trySend(products)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun storeProductList(products: List<Product>) {
        TODO("Not yet implemented")
    }

    override suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return@withContext false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(network) ?: return@withContext false

        val isConnected = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        if (isConnected) {
            try {
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)
                socket.connect(socketAddress, timeoutMs)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        } else {
            false
        }
    }

    override fun getUserDetails(): Flow<UserEntity> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(UserEntity())
            close()
            return@callbackFlow
        }

        val userDetailsSnapshot = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(ConstantsManager.APP_TAG, "Error fetching user details", error)
                    FirebaseCrashlytics.getInstance().recordException(error)
                    trySend(UserEntity())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userEntity = snapshot.toObject(UserEntity::class.java)
                    if (userEntity != null) {
                        trySend(userEntity)
                    } else {
                        trySend(UserEntity())
                    }
                } else {
                    trySend(UserEntity())
                }
            }

        awaitClose {
            userDetailsSnapshot.remove()
        }
    }

}
