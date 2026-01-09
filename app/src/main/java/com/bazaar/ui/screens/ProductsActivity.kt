package com.bazaar.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.bazaar.repository.ProductsUiState
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.ProductScreen
import com.bazaar.vm.ProductsActivityViewModel
import com.bazaar.vm.ViewModelFactory

class ProductsActivity : ComponentActivity() {

    private val viewModel: ProductsActivityViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState(initial = ProductsUiState.Loading)
                val searchQuery by viewModel.searchQuery.collectAsState()
                val uploadState by viewModel.uploadState.collectAsState()
                val sortOption by viewModel.sortOption.collectAsState()

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
                    onSortOptionChange = viewModel::onSortOptionChange
                )
            }
        }
    }
}
