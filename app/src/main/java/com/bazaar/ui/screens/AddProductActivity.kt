package com.bazaar.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.NeumorphicTextField
import com.bazaar.ui.components.QuantitySelector
import com.bazaar.vm.AddProductViewModel
import com.bazaar.vm.ViewModelFactory

class AddProductActivity : ComponentActivity() {

    private val viewModel: AddProductViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                AddProductScreen(
                    productName = viewModel.productName,
                    productPrice = viewModel.productPrice,
                    productQuantity = viewModel.productQuantity,
                    isSaving = viewModel.isSaving,
                    onNameChange = viewModel::onNameChange,
                    onQuantityChange = viewModel::onQuantityChange,
                    onPriceChange = viewModel::onPriceChange,
                    onSave = {
                        viewModel.saveProduct(
                            onSuccess = {
                                Toast.makeText(this, "Product Saved!", Toast.LENGTH_SHORT).show()
                                finish()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AddProductScreen(
    productName: String,
    productQuantity: String,
    productPrice: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
        ) {
            // Header
            Text(
                text = "Add New Product",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth() // Allow text to take full width for padding
                    .padding(
                        top = 32.dp,
                        bottom = 32.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between label and field
            ) {
                // Product Name Field
                Text(
                    text = "Product Name",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                NeumorphicTextField(
                    value = productName,
                    onValueChange = onNameChange,
                    placeholder = "e.g., Wireless Headphones",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity Field
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                QuantitySelector(
                    quantity = productQuantity,
                    onQuantityChange = onQuantityChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price Field
                Text(
                    text = "Price",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                NeumorphicTextField(
                    value = productPrice,
                    onValueChange = onPriceChange,
                    placeholder = "e.g., 99.99",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )
            }


            Spacer(modifier = Modifier.weight(1f, fill = true))

            // Save Button
            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier
                    .padding(vertical = 32.dp) // Keep vertical padding
                    .width(200.dp) // Make the button smaller
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp, // Increase elevation for a more pronounced shadow
                        shape = RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.secondary,
                        ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        "Save Product",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AddProductScreenPreview() {
    BazaarTheme {
        AddProductScreen(
            productName = "Sample Product",
            productQuantity = "10",
            productPrice = "9.99",
            isSaving = false,
            onNameChange = {},
            onQuantityChange = {},
            onPriceChange = {}, onSave = {})
    }
}