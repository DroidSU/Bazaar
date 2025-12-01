package com.bazaar.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bazaar.R
import com.bazaar.models.AuthUiState
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.LoginOptionsView
import com.bazaar.ui.components.OtpEntryView
import com.bazaar.vm.AuthViewModel
import com.bazaar.vm.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "AuthenticationActivity"

class AuthenticationActivity : ComponentActivity() {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    private val viewModel: AuthViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState()

                AuthenticationScreen(
                    uiState = uiState,
                    onAuthSuccess = {
                        Toast.makeText(this, "Authentication Successful!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, ProductsActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onGoogleSignIn = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    },
                    onPhoneSignIn = { phoneNumber -> viewModel.sendOtp(this, phoneNumber) },
                    onVerifyOtp = viewModel::verifyOtp,
                    onResetAuthFlow = viewModel::resetAuthFlow,
                    onErrorShown = viewModel::errorShown
                )
            }
        }
    }

    // --- Google Sign-In Logic ---
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google Sign-In successful, getting credential.")
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signInWithCredential(credential)
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign-In failed.", e)
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.w(TAG, "Google Sign-In flow was cancelled by user.")
        }
    }
}


@Composable
private fun AuthenticationScreen(
    uiState: AuthUiState,
    onAuthSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResetAuthFlow: () -> Unit,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current

    // --- Handle Side Effects ---
    LaunchedEffect(uiState.isAuthSuccessful) {
        if (uiState.isAuthSuccessful) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onErrorShown() // Acknowledge the error has been shown
        }
    }

    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim_welcome))
    val lottieProgress by animateLottieCompositionAsState(composition = lottieComposition, iterations = LottieConstants.IterateForever)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = lottieComposition,
                    progress = { lottieProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                AnimatedContent(
                    targetState = uiState.isOtpSent,
                    label = "AuthScreenAnimation",
                    transitionSpec = {
                        slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                    }
                ) { isOtpScreen ->
                    if (isOtpScreen) {
                        OtpEntryView(
                            isLoading = uiState.isLoading,
                            resendTimer = uiState.resendTimer,
                            onVerifyOtp = onVerifyOtp,
                            onBack = onResetAuthFlow
                        )
                    } else {
                        LoginOptionsView(
                            isLoading = uiState.isLoading,
                            onGoogleSignIn = onGoogleSignIn,
                            onPhoneSignIn = onPhoneSignIn
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    BazaarTheme {
        AuthenticationScreen(
            uiState = AuthUiState(isLoading = false, isOtpSent = false),
            onAuthSuccess = {},
            onGoogleSignIn = {},
            onPhoneSignIn = {},
            onVerifyOtp = {},
            onResetAuthFlow = {},
            onErrorShown = {}
        )
    }
}