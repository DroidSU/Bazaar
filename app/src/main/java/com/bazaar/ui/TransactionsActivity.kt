package com.bazaar.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.transactions.components.TransactionsScreen
import com.sujoy.transactions.viewmodels.TransactionsViewModel

class TransactionsActivity : ComponentActivity() {
    private val viewModel: TransactionsViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val productList by viewModel.productList.collectAsState()
                val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
                val selectedSalesProduct by viewModel.selectedSalesProduct.collectAsState()
                val selectedQuantityForSales by viewModel.selectedQuantityForSales.collectAsState()
                val totalAmount by viewModel.totalAmount.collectAsState()
                val salesList by viewModel.salesList.collectAsState()

                TransactionsScreen(
                    productList = productList,
                    selectedProduct = selectedSalesProduct,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = viewModel::onTabSelected,
                    onSalesProductSelected = viewModel::onSalesProductSelected,
                    selectedQuantityForSales = selectedQuantityForSales,
                    onSalesQuantityChanged = viewModel::onSalesQuantityChanged,
                    onAddToCartClicked = viewModel::onAddToCart,
                    totalAmount = totalAmount,
                    salesList = salesList,
                    onCheckout = viewModel::onCheckout,
                    onRemoveItem = viewModel::onRemoveItem,
                    checkoutSuccessFlow = viewModel.checkoutState
                )
            }
        }
    }
}
