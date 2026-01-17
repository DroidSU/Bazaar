package com.bazaar.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bazaar.models.Product
import com.bazaar.ui.components.DashboardScreen
import com.bazaar.vm.DashboardViewModel
import com.bazaar.vm.ViewModelFactory
import com.sujoy.designsystem.theme.BazaarTheme

class DashboardActivity : ComponentActivity() {
    private val viewModel: DashboardViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BazaarTheme {
                val isSignedOut by viewModel.isSignedOut.collectAsState()
                val productList by viewModel.productList.collectAsState(emptyList<Product>())
                val lowStockCount by viewModel.lowStockCount.collectAsState(0)
                val outOfStockCount by viewModel.outOfStockCount.collectAsState(0)

                if(isSignedOut) {
                    LaunchedEffect(Unit) {
                        val intent = Intent(this@DashboardActivity, AuthenticationActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }

                DashboardScreen(
                    productList = productList,
                    onTransactionsClicked = {
                        startActivity(Intent(this, TransactionsActivity::class.java))
                    },
                    onTotalItemsClicked = {
                        startActivity(Intent(this, ProductsActivity::class.java))
                    },
                    onAddNewItemClicked = {
                        startActivity(Intent(this, AddProductActivity::class.java))
                    },
                    lowStockCount = lowStockCount,
                    outOfStockCount = outOfStockCount
                )
            }
        }
    }
}