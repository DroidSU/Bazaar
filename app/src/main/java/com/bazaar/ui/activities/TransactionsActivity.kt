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

                TransactionsScreen(
                    productList = productList,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        viewModel.onTabSelected(index)
                    }
                )
            }
        }
    }
}