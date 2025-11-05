package com.bazaar.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.bazaar.R
import com.bazaar.models.Product

class ProductDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("product", Product::class.java)
        } else {
            intent.getSerializableExtra("product") as Product
        }

        setContent {
            if (product != null) {
                ProductDetailScreen(product)
            }
        }
    }

}

@Composable
fun ProductDetailScreen(product: Product) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(1F)
    ) {
//        AsyncImage(
//            modifier = Modifier.size(150.dp),
//            model = product.productImageUrl,
//            contentDescription = ""
//        )
        Image(painter = painterResource(id = R.drawable.food), contentDescription = "")
        Text(text = product.productName)
        Text(text = "$${product.productPrice}")
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailActivityPreview() {
    ProductDetailScreen(product = Product("1", "Sample Product", 9.99, ""))
}
