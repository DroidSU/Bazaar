@file:OptIn(ExperimentalMaterial3Api::class)

package com.bazaar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import com.bazaar.utils.WeightUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    isSaving: Boolean
) {
    // --- SAFE INITIALIZATION of state ---
    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var weight by remember { mutableStateOf(product.weight.toString()) }
    var weightUnit by remember {
        mutableStateOf(
            runCatching { WeightUnit.valueOf(product.weightUnit) }
                .getOrDefault(WeightUnit.GM)
        )
    }

    // Use Scaffold for a proper screen structure
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
        }
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
                    .imePadding() // Handle keyboard showing
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- FORM FIELDS ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    NeumorphicTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Product Name"
                    )

                    Column {
                        Text(
                            "Quantity",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        QuantitySelector(quantity = quantity, onQuantityChange = { quantity = it })
                    }

                    Column {
                        Text(
                            "Weight",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        WeightInput(
                            value = weight,
                            unit = weightUnit,
                            onValueChange = { weight = it },
                            onUnitChange = { weightUnit = it }
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Price",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                        PriceField(
                            value = price,
                            onValueChange = { price = it },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // --- SAVE BUTTON ---
                Button(
                    onClick = {
                        val updatedProduct = product.copy(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            price = price.toDoubleOrNull() ?: 0.0,
                            weight = weight.toDoubleOrNull() ?: 0.0,
                            weightUnit = if (weight.isNotBlank()) weightUnit.name else ""
                        )
                        onSave(updatedProduct)
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.height(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            "Save Changes",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                // Add extra bottom padding for scroll area
                Spacer(Modifier.height(24.dp))
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
            isSaving = false,
            onDismiss = {},
            onSave = {}
        )
    }
}
