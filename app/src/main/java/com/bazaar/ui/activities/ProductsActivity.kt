package com.bazaar.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.bazaar.ui.components.ProductScreen
import com.bazaar.vm.ProductsActivityViewModel
import com.bazaar.vm.ViewModelFactory
import com.sujoy.common.AppUIState
import com.sujoy.designsystem.theme.BazaarTheme

class ProductsActivity : ComponentActivity() {

    private val viewModel: ProductsActivityViewModel by viewModels {
        ViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState(initial = AppUIState.Idle)
                val searchQuery by viewModel.searchQuery.collectAsState()
                val uploadState by viewModel.uploadState.collectAsState()
                val sortOption by viewModel.sortOption.collectAsState()
                val productList by viewModel.productsList.collectAsState()

                ProductScreen(
                    uiState = uiState,
                    searchQuery = searchQuery,
                    uploadState = uploadState,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onAddProduct = {
                        startActivity(Intent(this, AddProductActivity::class.java))
                    },
                    onUploadCsv = { uri ->
                        viewModel.uploadProductsFromCsv(contentResolver, uri)
                    },
                    onDismissUpload = viewModel::onDismissUpload,
                    onDeleteProduct = viewModel::onDeleteProduct,
                    currentSortOption = sortOption,
                    onSortOptionChange = viewModel::onSortOptionChange,
                    productList = productList,
                    onEditClicked = { product ->
                        val intent = Intent(
                            this,
                            EditProductActivity::class.java
                        ).apply {
                            putExtra("PRODUCT_ID", product.id)
                            putExtra("PRODUCT_NAME", product.name)
                            putExtra(
                                "PRODUCT_QUANTITY",
                                product.quantity
                            )
                            putExtra("PRODUCT_WEIGHT", product.weight)
                            putExtra("PRODUCT_UNIT", product.weightUnit)
                            putExtra("PRODUCT_PRICE", product.price)
                            putExtra(
                                "PRODUCT_CREATED",
                                product.createdOn
                            )
                            putExtra(
                                "PRODUCT_THRESHOLD",
                                product.thresholdValue
                            )
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
