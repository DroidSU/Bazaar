package com.sujoy.authentication.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.sujoy.authentication.AuthUiState
import com.sujoy.common.ConstantsManager.RESEND_TIMEOUT
import com.sujoy.data.models.PhoneAuthEvent
import com.sujoy.data.models.Product
import com.sujoy.data.models.UserEntity
import com.sujoy.data.repository.DatabaseRepository
import com.sujoy.data.repository.NetworkRepository
import com.sujoy.data.repository.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _verificationId = MutableStateFlow("")
    val verificationId = _verificationId.asStateFlow()

    private val _timerValue = MutableStateFlow(RESEND_TIMEOUT)
    val timerValue = _timerValue.asStateFlow()

    private val _isOTPSent = MutableStateFlow(false)
    val isOTPSent = _isOTPSent.asStateFlow()

    private val _otpValue = MutableStateFlow("")
    val otpValue = _otpValue.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private var resendTimerJob: Job? = null
    private var authJob: Job? = null

    private val _productsList = MutableStateFlow<List<Product>>(emptyList())
    val productsList = _productsList.asStateFlow()

    private val _userData = MutableStateFlow(UserEntity())
    val userData = _userData.asStateFlow()


    fun signInWithCredential(credential: AuthCredential) {
        authJob?.cancel()
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            when (val result = networkRepository.signInWithCredentials(credential)) {
                is NetworkResult.Success -> {
                    _verificationId.value = ""
                    _isOTPSent.value = false

                    if(networkRepository.getCurrentUser() != null) {
                        fetchUserDetails()
                        fetchProductsList()
                    }


                    _uiState.value = AuthUiState.Success
                }

                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    fun onOTPChanged(otpValue: String) {
        _otpValue.value = otpValue
    }

    fun sendOtp(phoneNumber: String) {
        val formattedPhoneNumber =
            if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
        _phoneNumber.value = formattedPhoneNumber

        authJob?.cancel()
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            if(networkRepository.isNetworkAvailable()) {
                networkRepository.sendVerificationCode(formattedPhoneNumber).collect { event ->
                    when (event) {
                        is PhoneAuthEvent.CodeSent -> {
                            _uiState.value = AuthUiState.Idle
                            _verificationId.value = event.verificationId
                            _isOTPSent.value = true
                            startResendTimer()
                        }

                        is PhoneAuthEvent.VerificationCompleted -> {
                            signInWithCredential(event.credential)
                        }

                        is PhoneAuthEvent.Error -> {
                            _uiState.value = AuthUiState.Error(event.message)
                        }
                    }
                }
            }
            else{
                _uiState.value = AuthUiState.Error("No Internet Connection")
            }
        }
    }

    fun resendOtp() {
        val phoneNumber = _phoneNumber.value
        if (phoneNumber.isNotEmpty()) {
            sendOtp(phoneNumber)
        }
    }

    fun verifyOtp(otpCode: String) {
        val verificationId = _verificationId.value
        if (verificationId.isEmpty()) {
            _uiState.value = AuthUiState.Error("Session expired. Please request a new OTP.")
            return
        }

        try {
            val credential = networkRepository.getPhoneAuthCredential(verificationId, otpCode)
            signInWithCredential(credential)
        } catch (e: Exception) {
            _uiState.value =
                AuthUiState.Error(e.message ?: "An error occurred during OTP verification.")
        }
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            for (i in RESEND_TIMEOUT downTo 1) {
                _timerValue.value = i
                delay(1000)
            }
            _timerValue.value = 0
        }
    }

    fun resetAuthFlow() {
        authJob?.cancel()
        resendTimerJob?.cancel()
        _uiState.value = AuthUiState.Idle
        _verificationId.value = ""
        _isOTPSent.value = false
        _timerValue.value = RESEND_TIMEOUT
        _phoneNumber.value = ""
        _otpValue.value = ""
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            networkRepository.getUserDetails().collect {
                _userData.value = it
            }
        }
    }

    fun fetchProductsList() {
        viewModelScope.launch {
            networkRepository.getProducts().collect {
                databaseRepository.addProductsToLocal(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authJob?.cancel()
        resendTimerJob?.cancel()
    }


}
