@file:OptIn(ExperimentalMaterial3Api::class)

package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.models.Product
import com.bazaar.models.WeightUnit
import com.bazaar.theme.BazaarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductSheet(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    isSaving: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    // --- SAFE INITIALIZATION of state to prevent crashes ---
    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var weight by remember { mutableStateOf(product.weight.toString()) }
    var weightUnit by remember {
        mutableStateOf(
            // Safely convert string to enum, default to GM if invalid or null
            runCatching { WeightUnit.valueOf(product.weightUnit) }
                .getOrDefault(WeightUnit.GM)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface, // Use theme surface color
        dragHandle = { DragHandle() } // Add a drag handle for better UX
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 40.dp) // Generous padding at the bottom
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Text(
                "Edit Product",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

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
                        // Safely convert weight to Double or 0
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        // Only set weightUnit if weight is present
                        weightUnit = if (weight.isNotBlank()) weightUnit.name else ""
                    )
                    onSave(updatedProduct)
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp) // Softer corners
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
        }
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .width(40.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    )
}


@Preview(showBackground = true)
@Composable
private fun EditProductSheetPreview() {
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
        // Use a Box to simulate the sheet appearing on a screen
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            EditProductSheet(product = product, isSaving = false, onDismiss = {}, onSave = {})
        }
    }
}
