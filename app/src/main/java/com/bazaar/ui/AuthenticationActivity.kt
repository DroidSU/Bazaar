package com.bazaar.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.authentication.AuthUiState
import com.sujoy.authentication.ui.AuthenticationScreen
import com.sujoy.authentication.vm.AuthViewModel
import com.sujoy.designsystem.theme.BazaarTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AuthenticationActivity"

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState(AuthUiState.Idle)
                val otpValue by viewModel.otpValue.collectAsState("")
                val isOTPSent by viewModel.isOTPSent.collectAsState()

                if (uiState is AuthUiState.Success) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                AuthenticationScreen(
                    uiState = uiState,
                    onGoogleSignIn = {
                    },
                    onPhoneSignIn = {
                        viewModel.sendOtp(it)
                    },
                    onVerifyOtp = {
                        viewModel.verifyOtp(otpValue)
                    },
                    onResetAuthFlow = {
                        viewModel.resetAuthFlow()
                    },
                    isOTPSent = isOTPSent,
                    timerValue = 0,
                    onOTPChanged = {
                        viewModel.onOTPChanged(it)
                    },
                    otpCode = otpValue,
                    onResendOtp = {
                        viewModel.resendOtp()
                    }
                )
            }
        }
    }
}
