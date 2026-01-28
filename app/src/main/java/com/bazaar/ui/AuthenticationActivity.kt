package com.bazaar.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.sujoy.authentication.data.AuthUiState
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
                    },
                )
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google Sign-In successful, getting credential.")
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signInWithCredential(credential)
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign-In failed.", e)
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Log.w(TAG, "Google Sign-In flow was cancelled by user.")
        }
    }
}
