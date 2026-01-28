package com.bazaar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.models.CheckoutState
import com.sujoy.database.model.SaleItemEntity
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    productList: List<Product>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onSalesProductSelected: (String) -> Unit,
    selectedProduct: Product? = null,
    selectedQuantityForSales: Int,
    onSalesQuantityChanged: (Boolean) -> Unit,
    salesList: List<SaleItemEntity>,
    totalAmount: Double,
    onAddToCartClicked: () -> Unit,
    onCheckout: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    checkoutSuccessFlow: SharedFlow<CheckoutState>
) {
    val tabs = listOf("Sales", "Restock")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transactions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        width = 64.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            onTabSelected(index)
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> SalesScreen(
                    productList = productList,
                    salesList = salesList,
                    selectedProduct = selectedProduct,
                    selectedQuantityForSales = selectedQuantityForSales,
                    totalAmount = totalAmount,
                    onProductSelected = {
                        onSalesProductSelected(it)
                    },
                    onQuantityChanged = {
                        onSalesQuantityChanged(it)
                    },
                    onAddToCartClicked = {
                        onAddToCartClicked()
                    },
                    onRemoveProductClicked = {
                        onRemoveItem(it)
                    },
                    onCheckout = {
                        onCheckout()
                    },
                    checkoutStateFlow = checkoutSuccessFlow
                )

                1 -> RestockScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview() {
    BazaarTheme {
        TransactionsScreen(
            productList = emptyList(),
            selectedTabIndex = 0,
            onTabSelected = {},
            onSalesProductSelected = {},
            selectedQuantityForSales = 0,
            onSalesQuantityChanged = {},
            totalAmount = 0.0,
            salesList = emptyList(),
            onCheckout = {},
            onAddToCartClicked = {},
            onRemoveItem = {},
            checkoutSuccessFlow = MutableSharedFlow()
        )
    }
}