package com.bazaar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bazaar.models.Product
import com.bazaar.models.WeightUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductSheet(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    isSaving: Boolean
) {

    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var weight by remember { mutableStateOf(product.weight.toString()) }
    var weightUnit by remember { mutableStateOf(WeightUnit.valueOf(product.weightUnit)) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .imePadding(), // Automatically handles keyboard padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Edit Product",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            NeumorphicTextField(value = name, onValueChange = { name = it }, placeholder = "Product Name")
            Spacer(Modifier.height(16.dp))
            QuantitySelector(quantity = quantity, onQuantityChange = { quantity = it })
            Spacer(Modifier.height(16.dp))
            WeightInput(value = weight, unit = weightUnit, onValueChange = { weight = it }, onUnitChange = { weightUnit = it })
            Spacer(Modifier.height(16.dp))
            PriceField(value = price, onValueChange = { price = it })

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedProduct = product.copy(
                        name = name,
                        quantity = quantity.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0,
                        weight = weight.toDouble(),
                        weightUnit = weightUnit.toString()
                    )
                    onSave(updatedProduct)
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
                } else {
                    Text("Save Changes", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}
