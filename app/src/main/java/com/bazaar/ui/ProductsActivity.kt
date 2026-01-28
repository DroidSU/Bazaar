package com.bazaar.ui

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.sujoy.common.AppUIState
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.products.components.ProductScreen
import com.sujoy.products.viewmodels.ProductsActivityViewModel
import java.util.Locale

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

                val csvPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { uri ->
                        uri?.let {
                            viewModel.uploadProductsFromCsv(contentResolver, it)
                        }
                    }
                )

                val voiceSearchLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                        if (!spokenText.isNullOrEmpty()) {
                            viewModel.onSearchQueryChanged(spokenText)
                        }
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
                    onUploadCsvClick = {
                        csvPickerLauncher.launch(arrayOf("text/comma-separated-values", "text/csv"))
                    },
                    onVoiceSearchClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...")
                        }
                        voiceSearchLauncher.launch(intent)
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
                            putExtra("PRODUCT_QUANTITY", product.quantity)
                            putExtra("PRODUCT_WEIGHT", product.weight)
                            putExtra("PRODUCT_UNIT", product.weightUnit)
                            putExtra("PRODUCT_PRICE", product.price)
                            putExtra("PRODUCT_CREATED", product.createdOn)
                            putExtra("PRODUCT_THRESHOLD", product.thresholdValue)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
