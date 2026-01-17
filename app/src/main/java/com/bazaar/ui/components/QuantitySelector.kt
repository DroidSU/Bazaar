package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujoy.designsystem.theme.BazaarTheme

@Composable
fun QuantitySelector(
    quantity: String,
    onQuantityChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Center the whole component
    ) {
        // Minus Button
        IconButton(
            onClick = {
                val currentQuantity = quantity.toIntOrNull() ?: 1
                if (currentQuantity > 0) {
                    onQuantityChange((currentQuantity - 1).toString())
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease quantity",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Quantity Text Field
        NeumorphicTextField(
            value = quantity,
            onValueChange = {
                // Allow only numeric input
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    onQuantityChange(it)
                }
            },
            placeholder = "0",
            // Set a fixed width for the text field and remove weight
            modifier = Modifier
                .width(100.dp)
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        // Plus Button
        IconButton(
            onClick = {
                val currentQuantity = quantity.toIntOrNull() ?: 0
                onQuantityChange((currentQuantity + 1).toString())
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantitySelectorPreview() {
    BazaarTheme {
        QuantitySelector(quantity = "5", onQuantityChange = {})
    }
}