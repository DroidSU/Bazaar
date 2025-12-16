package com.bazaar.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.bazaar.repository.ProductsUiState
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.ProductScreen
import com.bazaar.utils.SortOption
import com.bazaar.vm.ProductsActivityViewModel
import com.bazaar.vm.ViewModelFactory

class ProductsActivity : ComponentActivity() {

    private val viewModel: ProductsActivityViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState(initial = ProductsUiState.Loading)
                val searchQuery by viewModel.searchQuery.collectAsState()
                val editingProduct by viewModel.editingProduct.collectAsState()
                val isSavingUpdate by viewModel.isSavingUpdate.collectAsState()
                val uploadState by viewModel.uploadState.collectAsState()
                val isSignedOut by viewModel.isSignedOut.collectAsState()

                if(isSignedOut) {
                    LaunchedEffect(Unit) {
                        val intent = Intent(this@ProductsActivity, AuthenticationActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }

                ProductScreen(
                    uiState = uiState,
                    searchQuery = searchQuery,
                    uploadState = uploadState,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onAddProduct = {
                        startActivity(Intent(this, AddProductActivity::class.java))
                    },
                    editingProduct = editingProduct,
                    isSavingUpdate = isSavingUpdate,
                    onEditProduct = viewModel::onEditProductClicked,
                    onDismissEdit = viewModel::onDismissEdit,
                    onUpdateProduct = viewModel::onUpdateProduct,
                    onUploadCsv = { uri ->
                        viewModel.uploadProductsFromCsv(contentResolver, uri)
                    },
                    onDismissUpload = viewModel::onDismissUpload,
                    onSignOut = viewModel::signOut,
                    onDeleteProduct = viewModel::onDeleteProduct,
                    currentSortOption = SortOption.NAME_ASC,
                    onSortOptionChange = viewModel::onSortOptionChange
                )
            }
        }
    }
}
