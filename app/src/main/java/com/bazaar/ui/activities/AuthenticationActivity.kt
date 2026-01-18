package com.bazaar.ui.activities

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bazaar.R
import com.bazaar.vm.AuthViewModel
import com.bazaar.vm.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.sujoy.authentication.ui.AuthenticationScreen
import com.sujoy.designsystem.theme.BazaarTheme

private const val TAG = "AuthenticationActivity"

class AuthenticationActivity : ComponentActivity() {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    private val viewModel: AuthViewModel by viewModels { ViewModelFactory(applicationContext) }

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
                        val intent = Intent(this, DashboardActivity::class.java)
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