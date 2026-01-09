package com.bazaar.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.EditProductScreen
import com.bazaar.utils.WeightUnit
import com.bazaar.vm.EditProductsViewModel
import com.bazaar.vm.ViewModelFactory

class EditProductActivity : ComponentActivity() {

    private val viewModel: EditProductsViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val productId = intent.getStringExtra("PRODUCT_ID") ?: ""
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: ""
        val productQuantity = intent.getIntExtra("PRODUCT_QUANTITY", 0)
        val productWeight = intent.getDoubleExtra("PRODUCT_WEIGHT", 0.0)
        val productUnit = intent.getStringExtra("PRODUCT_UNIT") ?: WeightUnit.GM.name
        val productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0)
        val productCreated = intent.getLongExtra("PRODUCT_CREATED", System.currentTimeMillis())
        val threshold = intent.getDoubleExtra("PRODUCT_THRESHOLD", 0.0)

        val initialProduct = Product(
            id = productId,
            name = productName,
            quantity = productQuantity,
            weight = productWeight,
            weightUnit = productUnit,
            price = productPrice,
            createdOn = productCreated,
            thresholdValue = threshold
        )

        setContent {
            BazaarTheme {
                val uiState = viewModel.uiState.collectAsState()

                EditProductScreen(
                    product = initialProduct,
                    onDismiss = {
                        finish()
                    },
                    onSave = { updatedProduct ->
                        viewModel.onUpdateProduct(product = updatedProduct)
                    },
                    uiState = uiState.value,
                    onReset = {
                        viewModel.resetState()
                    }
                )
            }
        }
    }
}