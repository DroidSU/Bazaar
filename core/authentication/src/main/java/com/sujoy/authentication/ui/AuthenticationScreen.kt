package com.sujoy.authentication.ui

import android.widget.Toast
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
import com.sujoy.authentication.data.AuthUiState
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.R as DesignR

@Composable
fun AuthenticationScreen(
    uiState: AuthUiState,
    onAuthSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResetAuthFlow: () -> Unit,
    onErrorShown: () -> Unit,
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
            onErrorShown()
        }
    }

    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(DesignR.raw.anim_welcome))
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
                        OTPComponent(
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
