package com.sujoy.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NeumorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = TextStyle(textAlign = TextAlign.Center),
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.3f
                ),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            // Setting the container color to match a neumorphic aesthetic
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background
        )
    )
}
