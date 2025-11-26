package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductListItem(product: Product, onEditClick: (Product) -> Unit) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.White.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // Align to the top
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Product Name
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp) // Add padding to not touch the icon
                )
                // Edit Icon Button
                IconButton(onClick = { onEditClick(product) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Product",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // --- MODIFIED SECTION: Quantity, Weight, and Price ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Group Quantity and Weight together
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quantity Information
                    if (product.quantity > 0) {
                        Text(
                            text = "Qty: ${product.quantity}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }

                    if (product.weight > 0) {
                        Text(
                            text = "Weight: ${product.weight}${product.weightUnit.lowercase()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

                // Price Information
                Text(
                    text = currencyFormat.format(product.price),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProductListItemPreview() {
    val product =
        Product(
            id = "0",
            name = "Premium Wireless Headphones with Extra Bass and a Very Long Name",
            quantity = 15,
            price = 249.99,
            weight = 500.0,
            weightUnit = "gm",
            createdOn = System.currentTimeMillis()
        )
    BazaarTheme {
        ProductListItem(product, onEditClick = {})
    }
}