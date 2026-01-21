package com.sujoy.authentication.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.sujoy.authentication.data.AuthUiState
import com.sujoy.authentication.repository.AuthRepository
import com.sujoy.authentication.repository.AuthResult
import com.sujoy.authentication.repository.PhoneAuthEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"
private const val RESEND_TIMEOUT = 60

class AuthViewModel(
    private val repository: AuthRepository
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
    private var authJob: Job? = null // New: To manage single auth process

    /**
     * Signs in the user using a given credential (from Google or Phone).
     */
    fun signInWithCredential(credential: AuthCredential) {
        authJob?.cancel() // Cancel previous attempts
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.signInWithCredentials(credential).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _verificationId.value = ""
                        _isOTPSent.value = false
                        _uiState.value = AuthUiState.Success
                    }
                    is AuthResult.Failure -> {
                        _uiState.value = AuthUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun onOTPChanged(otpValue : String) {
        _otpValue.value = otpValue
    }

    fun sendOtp(phoneNumber: String) {
        val formattedPhoneNumber =
            if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
        _phoneNumber.value = formattedPhoneNumber

        authJob?.cancel() // Cancel previous attempts
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.sendVerificationCode(formattedPhoneNumber).collect { event ->
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
            val credential = repository.getPhoneAuthCredential(verificationId, otpCode)
            signInWithCredential(credential)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error(e.message ?: "An error occurred during OTP verification.")
        }
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            // Start from timeout down to 1
            for (i in RESEND_TIMEOUT downTo 1) {
                _timerValue.value = i
                delay(1000)
            }
            _timerValue.value = 0 // Finished
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

    override fun onCleared() {
        super.onCleared()
        authJob?.cancel()
        resendTimerJob?.cancel()
    }
}
