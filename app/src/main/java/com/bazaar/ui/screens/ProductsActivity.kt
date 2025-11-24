package com.bazaar.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.bazaar.repository.ProductsUiState
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.ProductListItem
import com.bazaar.vm.ProductsActivityViewModel
import com.bazaar.vm.ViewModelFactory

class ProductsActivity : ComponentActivity() {

    private val viewModel: ProductsActivityViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                val uiState by viewModel.uiState.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()

                ProductScreen(
                    uiState = uiState,
                    searchQuery = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onAddProduct = {
                        val intent = Intent(this, AddProductActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductScreen(
    uiState: ProductsUiState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onAddProduct: () -> Unit
) {
    Scaffold(
        // Use systemBarsPadding to handle insets automatically
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
                .padding(paddingValues) // Use padding from Scaffold
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header and Search Bar Section
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
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Section
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is ProductsUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ProductsUiState.Success -> {
                        if (uiState.products.isEmpty()) {
                            // Show empty state for no results or no initial products
                            EmptyState(isSearch = searchQuery.isNotEmpty())
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 80.dp) // Padding for FAB
                            ) {
                                items(items = uiState.products, key = { it.id }) { product ->
                                    ProductListItem(product)
                                }
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
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
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
                    Product("01", "Eco-friendly Water Bottle", 150, 25.0),
                    Product("02", "Wireless Ergonomic Mouse", 75, 89.99),
                    Product("03", "Organic Green Tea", 200, 12.50)
                )
            ),
            onAddProduct = {},
            searchQuery = "",
            onQueryChange = {}
        )
    }
}
