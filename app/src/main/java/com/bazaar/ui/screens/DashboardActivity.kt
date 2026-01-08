package com.bazaar.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bazaar.theme.BazaarTheme
import com.bazaar.vm.DashboardViewModel
import com.bazaar.vm.ViewModelFactory

class DashboardActivity : ComponentActivity() {
    private val viewModel: DashboardViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BazaarTheme {
                val isSignedOut by viewModel.isSignedOut.collectAsState()

                if(isSignedOut) {
                    LaunchedEffect(Unit) {
                        val intent = Intent(this@DashboardActivity, AuthenticationActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }

                DashboardScreen()
            }
        }
    }
}