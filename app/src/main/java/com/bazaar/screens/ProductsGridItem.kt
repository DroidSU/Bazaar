package com.bazaar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.R
import com.bazaar.models.Product

@Composable
fun ProductsGridItem(product: Product, onItemClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        modifier = Modifier
            .size(160.dp, 160.dp)
            .aspectRatio(1f)
            .padding(15.dp)
            .clickable { onItemClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = product.productImageUrl,
//                contentDescription = "Product Image",
//                modifier = Modifier.fillMaxSize(.4f), // Image is 40% of the card size
//                contentScale = ContentScale.Fit // Changed to Fit to see the whole image
//            )

            Image(
                painter = painterResource(R.drawable.food), contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(.4f), // Image is 40% of the card size
                contentScale = ContentScale.Fit // Changed to Fit to see the whole image)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsGridItemPreview() {
    val product = Product("1", "Apple", 1.99, "")
    ProductsGridItem(product, {})
}
