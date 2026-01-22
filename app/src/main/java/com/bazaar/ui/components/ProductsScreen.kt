package com.bazaar.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.bazaar.models.UploadState
import com.bazaar.repository.ProductsUiState
import com.bazaar.ui.activities.EditProductActivity
import com.bazaar.utils.SortOption
import com.sujoy.designsystem.components.FabAction
import com.sujoy.designsystem.components.MultiFloatingButton
import com.sujoy.designsystem.components.SearchBar
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.utils.WeightUnit
import com.sujoy.model.Product
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    uiState: ProductsUiState,
    searchQuery: String,
    uploadState: UploadState,
    onQueryChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    onUploadCsv: (Uri) -> Unit,
    onDismissUpload: () -> Unit,
    onDeleteProduct: (Product) -> Unit,
    currentSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit
) {
    val context = LocalContext.current

    // ---- CSV Picker Logic ----
    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let(onUploadCsv)
    }

    // ---- Voice Search Logic ----
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

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        floatingActionButton = {
            MultiFloatingButton(
                modifier = Modifier.padding(bottom = 8.dp),
                mainIcon = Icons.Default.MoreVert,
                actions = listOf(
                    FabAction(
                        icon = Icons.Default.UploadFile,
                        description = "Upload excel",
                        onClick = {
                            csvPickerLauncher.launch(
                                arrayOf(
                                    "text/csv",
                                    "text/comma-separated-values",
                                    "application/csv",
                                )
                            )
                        }
                    ),
                    FabAction(
                        icon = Icons.Default.Add,
                        description = "Add new product",
                        onClick = onAddProduct
                    )
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bazaar",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                UploadProgressIndicator(
                    uploadState = uploadState,
                    onDismiss = onDismissUpload,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onVoiceSearchClick = onVoiceSearchClick,
                    modifier = Modifier.weight(1f)
                )

                SortDropDown(
                    currentSortOption = currentSortOption,
                    onSortOptionSelected = onSortOptionChange,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is ProductsUiState.Loading -> CircularProgressIndicator()
                    is ProductsUiState.Success -> {
                        val products = uiState.products

                        if (products.isEmpty()) {
                            EmptyState(isSearch = searchQuery.isNotEmpty())
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(items = products, key = { it.id }) { product ->
                                    ProductListItem(
                                        product,
                                        onEditClick = { selectedProduct ->
                                            // Open the Activity directly
                                            val intent = Intent(
                                                context,
                                                EditProductActivity::class.java
                                            ).apply {
                                                // Assuming Product is not Parcelable, passing fields individually
                                                putExtra("PRODUCT_ID", selectedProduct.id)
                                                putExtra("PRODUCT_NAME", selectedProduct.name)
                                                putExtra(
                                                    "PRODUCT_QUANTITY",
                                                    selectedProduct.quantity
                                                )
                                                putExtra("PRODUCT_WEIGHT", selectedProduct.weight)
                                                putExtra("PRODUCT_UNIT", selectedProduct.weightUnit)
                                                putExtra("PRODUCT_PRICE", selectedProduct.price)
                                                putExtra(
                                                    "PRODUCT_CREATED",
                                                    selectedProduct.createdOn
                                                )
                                                putExtra(
                                                    "PRODUCT_THRESHOLD",
                                                    selectedProduct.thresholdValue
                                                )
                                            }
                                            context.startActivity(intent)
                                        },
                                        onDelete = onDeleteProduct,
                                        onSwipeLeft = {})
                                }
                            }
                            // Removed: Internal EditProductScreen call
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
private fun SortDropDown(
    currentSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.Sort,
                contentDescription = "Sort Products"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.displayName,
                            fontWeight = if (option == currentSortOption) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (option == currentSortOption) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    ),
                    colors = MenuDefaults.itemColors(
                        textColor = if (option == currentSortOption) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
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
                        id = "01",
                        name = "Sample Product",
                        quantity = 10,
                        price = 99.99,
                        weight = 1.0,
                        weightUnit = WeightUnit.KG.toString(),
                        createdOn = System.currentTimeMillis()
                    )
                )
            ),
            searchQuery = "",
            uploadState = UploadState.Uploading(50),
            onQueryChange = {},
            onAddProduct = {},
            onUploadCsv = {},
            onDismissUpload = {},
            onDeleteProduct = {},
            currentSortOption = SortOption.NAME_ASC,
            onSortOptionChange = {}
        )
    }
}
