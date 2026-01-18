package com.sujoy.designsystem.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujoy.designsystem.theme.BazaarTheme
import com.sujoy.designsystem.utils.WeightUnit

@Composable
fun AdaptiveThresholdView(
    productQuantity: Int,
    productWeight: Double,
    weightUnit: WeightUnit,
    threshold: Double,
    onThresholdChanged: (Double) -> Unit,
    enabled: Boolean = true
){
    val isQuantityEntered = productQuantity > 0
    val isWeightEntered = productWeight > 0.0

    // Determine what we are setting the threshold for
    // If both are entered, we default to Quantity, but you could add a Toggle here if needed.
    // For this design, we will display a meaningful label based on the input.
    val thresholdLabel = when {
        isQuantityEntered && isWeightEntered -> "Quantity Threshold" // Or add a toggle to switch
        isWeightEntered -> "Weight Threshold"
        else -> "Stock Threshold" // Default
    }

    val unitLabel = when {
        isQuantityEntered && isWeightEntered -> "items"
        isWeightEntered -> weightUnit
        else -> "items"
    }

    // Only show if at least one input is provided, otherwise show a "Enter details first" state
    val isEnabled = isQuantityEntered || isWeightEntered

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isEnabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .background(
                color = if (isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isEnabled) {
            // Empty State
            Icon(
                imageVector = Icons.Rounded.Add, // Or a warning icon
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Enter Quantity or Weight to set alerts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        } else {
            // Active State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = thresholdLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Alert when stock is below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Visual Indicator of Unit
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = unitLabel.toString().uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    onClick = {
                        val step = if (isWeightEntered && !isQuantityEntered && weightUnit == WeightUnit.KG) 0.5 else 1.0
                        if (threshold > 0) {
                            onThresholdChanged((threshold - step).coerceAtLeast(0.0))
                        }
                    },
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(100.dp)
                        .height(56.dp)
                        .shadow(
                            elevation = 0.dp, // Inner shadow simulation could go here
                            shape = RoundedCornerShape(16.dp),
                            clip = true
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (threshold % 1.0 == 0.0) threshold.toInt().toString() else threshold.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unitLabel.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                FilledIconButton(
                    onClick = {
                        val step = if (isWeightEntered && !isQuantityEntered && weightUnit == WeightUnit.KG) 0.5 else 1.0
                        onThresholdChanged(threshold + step)
                    },
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdaptiveThresholdViewPreview() {
    BazaarTheme {
        AdaptiveThresholdView(
            productQuantity = 10,
            productWeight = 0.0,
            weightUnit = WeightUnit.KG,
            threshold = 5.0,
            onThresholdChanged = {}
        )
    }
}
