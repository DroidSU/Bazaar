package com.bazaar.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.designsystem.components.AdaptiveThresholdView
import com.sujoy.designsystem.components.NeumorphicTextField
import com.sujoy.designsystem.components.PriceField
import com.sujoy.designsystem.components.WeightInput
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.utils.WeightUnit
import com.sujoy.products.components.QuantitySelector
import com.sujoy.products.viewmodels.AddProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductActivity : ComponentActivity() {

    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BazaarTheme {
                AddProductScreen(
                    productName = viewModel.productName,
                    productPrice = viewModel.productPrice,
                    productQuantity = viewModel.productQuantity,
                    productWeight = viewModel.productWeight,
                    weightUnit = viewModel.weightUnit,
                    threshold = viewModel.productThreshold,
                    isSaving = viewModel.isSaving,
                    onNameChange = viewModel::onNameChange,
                    onQuantityChange = viewModel::onQuantityChange,
                    onPriceChange = viewModel::onPriceChange,
                    onWeightChange = viewModel::onWeightChange,
                    onUnitChange = viewModel::onUnitChange,
                    onThresholdChanged = viewModel::onThresholdChanged,
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
    productWeight: String,
    weightUnit: WeightUnit,
    threshold: Double,
    productPrice: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onUnitChange: (WeightUnit) -> Unit,
    onThresholdChanged: (Double) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Product",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 32.dp,
                        bottom = 32.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column {
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
                }

                Column {
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
                }

                Column {
                    Text(
                        text = "Weight (Optional)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    WeightInput(
                        value = productWeight,
                        unit = weightUnit,
                        onValueChange = onWeightChange,
                        onUnitChange = onUnitChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PriceField(
                        value = productPrice,
                        onValueChange = onPriceChange,
                        modifier = Modifier.height(56.dp),
                        currencySymbol = "₹",
                        placeholder = "0.0",
                        enabled = true,
                        isValid = {
                            it.matches(Regex("^\\d*\\.?\\d{0,2}$"))
                        }
                    )
                }

                AdaptiveThresholdView(
                    productQuantity = productQuantity.toIntOrNull() ?: 0,
                    productWeight = productWeight.toDoubleOrNull() ?: 0.0,
                    weightUnit = weightUnit,
                    threshold = threshold,
                    onThresholdChanged = onThresholdChanged,
                    enabled = true,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .width(200.dp)
                    .height(56.dp)
                    .shadow(
                        elevation = 15.dp,
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
