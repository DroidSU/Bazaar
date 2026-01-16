package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.models.Product
import com.bazaar.models.SaleItemModel
import com.bazaar.theme.BazaarTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    productList: List<Product>,
    salesList: List<SaleItemModel>,
    selectedProduct: Product?,
    selectedQuantityForSales: Int,
    onProductSelected: (String) -> Unit,
    onQuantityChanged: (Boolean) -> Unit,
    onAddToCartClicked: () -> Unit,
    onRemoveProductClicked: (Int) -> Unit,
    onCheckout: () -> Unit,
    totalAmount: Double,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Product Selection Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                )
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Add Item",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select Product") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            productList.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name ) },
                                    onClick = {
                                        onProductSelected(product.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Voice Search Placeholder
                    IconButton(
                        onClick = { /* TODO: Voice Search */ },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Outlined.Mic,
                            contentDescription = "Voice Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity and Add Button
                if (selectedProduct != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (selectedQuantityForSales > 1) {
                                    onQuantityChanged(true)
                                }
                            }) {
                                Icon(
                                    Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Decrease"
                                )
                            }
                            Text(
                                text = selectedQuantityForSales.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { onQuantityChanged(false) }) {
                                Icon(
                                    Icons.Default.AddCircleOutline,
                                    contentDescription = "Increase"
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onAddToCartClicked()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Summary List ---
        Text(
            "Current Bill",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = salesList.size, key = { index -> salesList[index] }) { id ->
                ListItem(
                    headlineContent = {
                        Text(
                            salesList[id].productName,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    supportingContent = {
                        val quantity = salesList[id].quantity
                        val weight = salesList[id].weight
                        val displayValue = if (quantity != 0) quantity else weight

                        val productPrice = productList.find { salesList[id].productId == it.id }?.price
                        Text("Qty: $displayValue × ₹$productPrice")
                    },
                    trailingContent = {
                        IconButton(onClick = { onRemoveProductClicked(id) }) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }

        // bottom card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Amount",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        "₹${totalAmount}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Button(
                    onClick = {
                        onCheckout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Checkout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SalesScreenPreview() {
    BazaarTheme {
        SalesScreen(
            productList = emptyList(),
            salesList = emptyList(),
            selectedProduct = null,
            selectedQuantityForSales = 0,
            onProductSelected = {},
            onQuantityChanged = {},
            onAddToCartClicked = {},
            onRemoveProductClicked = {},
            totalAmount = 0.0,
            onCheckout = {}
        )
    }
}