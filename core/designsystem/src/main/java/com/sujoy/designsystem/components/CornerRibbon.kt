package com.sujoy.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
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
import com.sujoy.designsystem.theme.BazaarTheme

/**
 * A corner ribbon indicator to highlight low stock items.
 */
@Composable
fun CornerRibbon(
    modifier: Modifier = Modifier,
    containerSize: Int = 40,
    ribbonColor: Color = MaterialTheme.colorScheme.error,
    iconColor: Color = MaterialTheme.colorScheme.onError
) {
    Box(
        modifier = modifier.size(containerSize.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Canvas(modifier = Modifier.size(containerSize.dp)) {
            val sizePx = size.width
            val ribbonPath = Path().apply {
                // Creates a triangle in the top right corner
                moveTo(sizePx * 0.2f, 0f)      // Top edge, starting 80% from right
                lineTo(sizePx, 0f)             // Top right corner
                lineTo(sizePx, sizePx * 0.8f)  // Right edge, 80% down from top
                close()
            }

            drawPath(
                path = ribbonPath,
                color = ribbonColor
            )
        }

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Low Stock",
            tint = iconColor,
            modifier = Modifier
                .size((containerSize / 2.5).dp)
                // Offset the icon to center it visually within the triangle
                .offset(x = (-2).dp, y = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CornerRibbonPreview() {
    BazaarTheme {
        Box(modifier = Modifier.size(100.dp)) {
            CornerRibbon(
                modifier = Modifier.align(Alignment.TopEnd),
                containerSize = 50
            )
        }
    }
}