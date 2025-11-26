package com.bazaar.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bazaar.R
import com.bazaar.models.Product
import com.bazaar.models.WeightUnit
import com.bazaar.repository.ProductsUiState
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.EditProductSheet
import com.bazaar.ui.components.ProductListItem
import com.bazaar.vm.ProductsActivityViewModel
import com.bazaar.vm.ViewModelFactory
import java.util.Locale

class ProductsActivity : ComponentActivity() {

    private val viewModel: ProductsActivityViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val editingProduct by viewModel.editingProduct.collectAsState()
                val isSavingUpdate by viewModel.isSavingUpdate.collectAsState()

                ProductScreen(
                    uiState = uiState,
                    searchQuery = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onAddProduct = {
                        val intent = Intent(this, AddProductActivity::class.java)
                        startActivity(intent)
                    },
                    editingProduct = editingProduct,
                    isSavingUpdate = isSavingUpdate,
                    onEditProduct = viewModel::onEditProductClicked,
                    onDismissEdit = viewModel::onDismissEdit,
                    onUpdateProduct = viewModel::onUpdateProduct
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductScreen(
    uiState: ProductsUiState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    editingProduct: Product?,
    isSavingUpdate: Boolean,
    onEditProduct: (Product) -> Unit,
    onDismissEdit: () -> Unit,
    onUpdateProduct: (Product) -> Unit
) {
    // ---- Voice Search Logic ----
    val context = LocalContext.current
    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                    results[0]
                }
            if (!spokenText.isNullOrEmpty()) {
                onQueryChange(spokenText)
            }
        }
    }

    val onVoiceSearchClick: () -> Unit = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...")
        }
        voiceSearchLauncher.launch(intent)
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Bazaar",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onVoiceSearchClick = onVoiceSearchClick, // Pass the callback
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is ProductsUiState.Loading -> CircularProgressIndicator()
                    is ProductsUiState.Success -> {
                        if (uiState.products.isEmpty()) {
                            EmptyState(isSearch = searchQuery.isNotEmpty())
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(items = uiState.products, key = { it.id }) { product ->
                                    ProductListItem(product, onEditClick = onEditProduct)
                                }
                            }

                            if(editingProduct != null){
                                EditProductSheet(
                                    product = editingProduct,
                                    onDismiss = onDismissEdit,
                                    onSave = onUpdateProduct,
                                    isSaving = isSavingUpdate
                                )
                            }
                        }
                    }

                    is ProductsUiState.Error -> {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onVoiceSearchClick: () -> Unit, // New callback for voice icon
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        trailingIcon = {
            IconButton(onClick = onVoiceSearchClick) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun EmptyState(isSearch: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(if (isSearch) R.raw.anim_no_products else R.raw.anim_no_products))
    val textToShow = if (isSearch) "No products found" else "No products yet"
    val subtext =
        if (isSearch) "Try a different search term" else "Click the '+' button to add your first product."

    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = textToShow,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtext,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductScreenPreview() {
    BazaarTheme {
        ProductScreen(
            uiState = ProductsUiState.Success(
                listOf(
                    Product(
                        "01",
                        "Eco-friendly Water Bottle",
                        150,
                        25.0,
                        weight = 1.0,
                        weightUnit = WeightUnit.KG.toString(),
                        System.currentTimeMillis()
                    ),
                    Product(
                        "02",
                        "Wireless Ergonomic Mouse",
                        75,
                        89.99,
                        weight = 1.0,
                        weightUnit = WeightUnit.KG.toString(),
                        System.currentTimeMillis()
                    ),
                    Product(
                        "03",
                        "Organic Green Tea",
                        200,
                        12.50,
                        weight = 1.0,
                        weightUnit = WeightUnit.KG.toString(),
                        System.currentTimeMillis()
                    )
                )
            ),
            onAddProduct = {},
            searchQuery = "",
            onQueryChange = {},
            editingProduct = null,
            isSavingUpdate = false,
            onEditProduct = {},
            onDismissEdit = {},
            onUpdateProduct = {}
        )
    }
}
