package com.bazaar.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.repository.ProductRepositoryImpl
import com.bazaar.theme.BazaarTheme
import com.bazaar.ui.components.NeumorphicTextField
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
                    viewModel = viewModel,
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
private fun AddProductScreen(viewModel: AddProductViewModel, onSave: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Add New Product",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 40.dp)
            )

            NeumorphicTextField(
                value = viewModel.productName,
                onValueChange = viewModel::onNameChange,
                placeholder = "Product Name"
            )
            Spacer(modifier = Modifier.height(20.dp))
            NeumorphicTextField(
                value = viewModel.productQuantity,
                onValueChange = viewModel::onQuantityChange,
                placeholder = "Quantity",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(20.dp))
            NeumorphicTextField(
                value = viewModel.productPrice,
                onValueChange = viewModel::onPriceChange,
                placeholder = "Price",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                enabled = !viewModel.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        spotColor = MaterialTheme.colorScheme.secondary
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Save Product", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AddProductScreenPreview() {
    BazaarTheme {
        AddProductScreen(viewModel = AddProductViewModel(ProductRepositoryImpl()), onSave = {})
    }
}
