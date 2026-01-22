@file:OptIn(ExperimentalMaterial3Api::class)

package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.repository.EditProductsUiState
import com.sujoy.designsystem.components.AdaptiveThresholdView
import com.sujoy.designsystem.components.NeumorphicTextField
import com.sujoy.designsystem.components.PriceField
import com.sujoy.designsystem.components.WeightInput
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.utils.WeightUnit
import com.sujoy.model.Product
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    product: Product,
    onSave: (Product) -> Unit,
    onDismiss: () -> Unit,
    uiState: EditProductsUiState,
    onReset: () -> Unit,
) {

    val isBusy = uiState is EditProductsUiState.Loading

    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var weight by remember { mutableStateOf(product.weight.toString()) }
    var threshold by remember { mutableDoubleStateOf(product.thresholdValue) }
    var weightUnit by remember {
        mutableStateOf(
            runCatching { WeightUnit.valueOf(product.weightUnit) }
                .getOrDefault(WeightUnit.GM)
        )
    }

    // Auto-dismiss on Success
    LaunchedEffect(uiState) {
        if (uiState is EditProductsUiState.Success) {
            delay(1500) // Wait 1.5 seconds to show the Green Tick
            onDismiss()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Edit Product",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(MaterialTheme.colorScheme.background),
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp)
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Product Name",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            NeumorphicTextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "Product Name",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                textStyle = TextStyle(textAlign = TextAlign.Start),
                                enabled = !isBusy
                            )
                        }

                        Column {
                            Text(
                                "Quantity",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            QuantitySelector(
                                quantity = quantity,
                                onQuantityChange = { quantity = it })
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Weight",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            WeightInput(
                                value = weight,
                                unit = weightUnit,
                                onValueChange = { weight = it },
                                onUnitChange = { weightUnit = it },
                                enabled = !isBusy
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Price",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PriceField(
                                value = price,
                                onValueChange = {
                                    price = it
                                },
                                modifier = Modifier.height(56.dp),
                                enabled = !isBusy,
                                currencySymbol = "₹",
                                placeholder = "0.0",
                                isValid = {
                                    it.matches(Regex("^\\d*\\.?\\d{0,2}$"))
                                }
                            )
                        }

                        AdaptiveThresholdView(
                            productQuantity = quantity.toIntOrNull() ?: 0,
                            productWeight = weight.toDoubleOrNull() ?: 0.0,
                            weightUnit = weightUnit,
                            threshold = threshold,
                            onThresholdChanged = {
                                threshold = it
                            },
                            enabled = !isBusy
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SAVE BUTTON ---
                    Button(
                        onClick = {
                            val updatedProduct = product.copy(
                                name = name,
                                quantity = quantity.toIntOrNull() ?: 0,
                                price = price.toDoubleOrNull() ?: 0.0,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                weightUnit = if (weight.isNotBlank()) weightUnit.name else "",
                                thresholdValue = threshold
                            )
                            onSave(updatedProduct)
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .width(200.dp)
                            .height(56.dp)
                            .shadow(
                                elevation = 15.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = MaterialTheme.colorScheme.secondary,
                                ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            "Save Changes",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }

        if (uiState !is EditProductsUiState.Idle) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = remember { null },
                        onClick = {
                            if (uiState is EditProductsUiState.Error) {
                                onReset
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (uiState) {
                        is EditProductsUiState.Loading -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.height(24.dp),
                                strokeWidth = 3.dp
                            )
                        }
                        is EditProductsUiState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF4CAF50), // Green color
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Updated!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        is EditProductsUiState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Error",
                                tint = Color(0xFFE53935), // Red color
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Failed to Update",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            // Display the error message if available
                            val errorMsg = "Update Failed"
                            if (errorMsg.isNotEmpty()) {
                                Text(
                                    text = errorMsg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        else -> {
                            /* Do nothing for Idle */
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProductScreenPreview() {
    val product = Product(
        id = "0",
        name = "Premium Wireless Headphones with Extra Bass",
        quantity = 5,
        weight = 500.0,
        weightUnit = WeightUnit.GM.name,
        price = 100.0,
        createdOn = System.currentTimeMillis()
    )
    BazaarTheme {
        EditProductScreen(
            product = product,
            onSave = {},
            onDismiss = {},
            uiState = EditProductsUiState.Idle,
            onReset = {}
        )
    }
}
