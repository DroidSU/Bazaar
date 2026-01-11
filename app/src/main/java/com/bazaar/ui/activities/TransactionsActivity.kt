package com.bazaar.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.TransactionsScreen
import com.bazaar.vm.TransactionsViewModel
import com.bazaar.vm.ViewModelFactory

class TransactionsActivity : ComponentActivity() {
    private val viewModel: TransactionsViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val productList by viewModel.productList.collectAsState(emptyList())
                val selectedTabIndex by viewModel.selectedTabIndex.collectAsState(0)
                val selectedSalesProduct by viewModel.selectedSalesProduct.collectAsState(null)
                // initial value for selectedQuantityForSales will be 1 as we want the count to start from 1.
                val selectedQuantityForSales by viewModel.selectedQuantityForSales.collectAsState(1)
                val totalAmount by viewModel.totalAmount.collectAsState(0.0)

                TransactionsScreen(
                    productList = productList,
                    selectedProduct = selectedSalesProduct,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        viewModel.onTabSelected(index)
                    },
                    onSalesProductSelected = {
                        viewModel.onSalesProductSelected(it)
                    },
                    selectedQuantityForSales = selectedQuantityForSales,
                    onSalesQuantityChanged = {
                        viewModel.onSalesQuantityChanged(it)
                    },
                    totalAmount = totalAmount
                )
            }
        }
    }
}