package com.bazaar.screens

import android.content.Intent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.bazaar.models.Product
import com.bazaar.ui.ProductDetailActivity

@Composable
fun ProductsGrid(products: List<Product>) {
    val context = LocalContext.current

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(products) { product ->
            ProductsGridItem(product = product) {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                context.startActivity(intent)
            }
        }
    }
}
