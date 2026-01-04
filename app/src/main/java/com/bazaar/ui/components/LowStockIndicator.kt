package com.bazaar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.theme.BazaarTheme

@Composable
fun LowStockIndicator(
    modifier: Modifier = Modifier,
    text: String = "Low Stock",
    ribbonColor: Color = MaterialTheme.colorScheme.error,
    textColor: Color = MaterialTheme.colorScheme.onError
) {
    Box(
        modifier = modifier.size(80.dp), // Adjust size of the corner area
        contentAlignment = Alignment.TopEnd
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ribbonPath = Path().apply {
                moveTo(size.width * 0.35f, 0f) // Start 40% from left on top edge
                lineTo(size.width, 0f)       // Go to top-right corner
                lineTo(size.width, size.height * 0.65f) // Go down 60% on right edge
                close() // Close path to make a triangle
            }

            drawPath(
                path = ribbonPath,
                color = ribbonColor
            )
        }

        // Optional: Add text rotated or icon inside
        // For a simple ribbon, a small Icon or exclamation mark works best at this scale
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 6.dp, top = 4.dp, end = 4.dp)
                .size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LowStockIndicatorPreview(){
    BazaarTheme {
        LowStockIndicator()
    }
}
