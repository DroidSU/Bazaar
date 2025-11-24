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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductListItem(product: Product) {
    // Format the price to include a currency symbol
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Reduced vertical padding to make items closer
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow( // Bottom-right shadow (darker)
                // Reduced elevation for a subtler effect
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .shadow( // Top-left shadow (lighter)
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.White.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface) // Use surface color for cards
            // Reduced internal padding
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                // Slightly smaller font size for the name
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Reduced spacer height
            Spacer(modifier = Modifier.height(6.dp))

            // Quantity and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quantity Information
                Text(
                    text = "Qty: ${product.quantity}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Slightly smaller font size
                    fontSize = 14.sp
                )
                // Price Information
                Text(
                    text = currencyFormat.format(product.price),
                    fontWeight = FontWeight.SemiBold,
                    // Slightly smaller font size for price
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
            name = "Premium Wireless Headphones",
            quantity = 15,
            price = 249.99,
            createdOn = System.currentTimeMillis()
        )
    BazaarTheme {
        ProductListItem(product)
    }
}
