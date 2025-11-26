package com.bazaar.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.theme.BazaarTheme
import com.bazaar.utils.WeightUnit

@Composable
fun WeightInput(
    value: String,
    unit: WeightUnit,
    onValueChange: (String) -> Unit,
    onUnitChange: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text Field for weight value
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("e.g., 500") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.weight(1f)
        )

        // Custom Unit Toggle Switch
        UnitToggleSwitch(
            selectedUnit = unit,
            onUnitChange = onUnitChange,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
private fun UnitToggleSwitch(
    selectedUnit: WeightUnit,
    onUnitChange: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 24.dp
    val toggleWidth = 110.dp

    Box(
        modifier = modifier
            .height(40.dp)
            .width(toggleWidth)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sliding indicator with a shadow for a "pressed" effect
        val offset by animateDpAsState(
            targetValue = if (selectedUnit == WeightUnit.GM) 0.dp else toggleWidth / 2,
            label = "offset"
        )
        Box(
            modifier = Modifier
                .height(40.dp)
                .width(toggleWidth / 2)
                .offset(x = offset)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(cornerRadius))
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        // Text labels ("gm" and "kg")
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isGmSelected = selectedUnit == WeightUnit.GM
            val isKgSelected = selectedUnit == WeightUnit.KG

            // "gm" Text
            Text(
                text = "gm",
                color = if (isGmSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isGmSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = { onUnitChange(WeightUnit.GM) })
            )
            // "kg" Text
            Text(
                text = "kg",
                color = if (isKgSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isKgSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = { onUnitChange(WeightUnit.KG) })
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightInputPreview() {
    BazaarTheme {
        WeightInput(
            modifier = Modifier,
            onValueChange = {},
            value = "10",
            unit = WeightUnit.KG,
            onUnitChange = {}
        )
    }
}
