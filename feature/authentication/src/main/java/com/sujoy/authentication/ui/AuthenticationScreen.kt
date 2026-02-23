package com.sujoy.authentication.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sujoy.authentication.AuthUiState
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.R as DesignR

@Composable
fun AuthenticationScreen(
    uiState: AuthUiState,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit,
    onResetAuthFlow: () -> Unit,
    isOTPSent: Boolean,
    timerValue: Int,
    onOTPChanged: (String) -> Unit,
    otpCode: String
) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(DesignR.raw.anim_welcome))
    val lottieProgress by animateLottieCompositionAsState(composition = lottieComposition, iterations = LottieConstants.IterateForever)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState is AuthUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else {
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
                        targetState = isOTPSent,
                        label = "AuthScreenAnimation",
                        transitionSpec = {
                            slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                        }
                    ) { isOtpScreen ->
                        if (isOtpScreen) {
                            OTPComponent(
                                timerValue = timerValue,
                                onVerifyOtp = onVerifyOtp,
                                onResendOtp = onResendOtp,
                                onBack = onResetAuthFlow,
                                isEnabled = isOTPSent,
                                otpCode = otpCode,
                                onOTPChanged = {
                                    if (it.length <= 6){
                                        onOTPChanged(it)
                                    }
                                }
                            )
                        } else {
                            LoginOptionsView(
                                isEnabled = !isOTPSent,
                                onGoogleSignIn = onGoogleSignIn,
                                onPhoneSignIn = onPhoneSignIn
                            )
                        }
                    }
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
            uiState = AuthUiState.Idle,
            onGoogleSignIn = {},
            onPhoneSignIn = {},
            onVerifyOtp = {},
            onResendOtp = {},
            onResetAuthFlow = {},
            isOTPSent = false,
            timerValue = 0,
            onOTPChanged = {},
            otpCode = ""
        )
    }
}
