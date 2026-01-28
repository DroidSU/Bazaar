package com.bazaar.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sujoy.dashboard.DashboardScreen
import com.sujoy.dashboard.DashboardViewModel
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.transactions.viewmodels.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()
    private val transactionsViewModel: TransactionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val productList by viewModel.productList.collectAsState()
                val lowStockCount by viewModel.lowStockCount.collectAsState()
                val outOfStockCount by viewModel.outOfStockCount.collectAsState()
                val isSignedOut by viewModel.isSignedOut.collectAsState()

                if (isSignedOut) {
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()
                }

                var showMenu by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        totalItemsCount = productList.size,
                        lowStockCount = lowStockCount,
                        outOfStockCount = outOfStockCount,
                        showMenu = showMenu,
                        onMenuClick = { showMenu = it },
                        onViewAllProducts = {
                            startActivity(Intent(this, ProductsActivity::class.java))
                        },
                        onViewTransactions = {
                            startActivity(Intent(this, TransactionsActivity::class.java))
                        },
                        onAddNewProduct = {
                            startActivity(Intent(this, AddProductActivity::class.java))
                        },
                        onSignOut = {
                            viewModel.onSignOut()
                        }
                    )
                }
            }
        }
    }
}
