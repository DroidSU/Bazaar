package com.sujoy.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujoy.designsystem.theme.BazaarTheme

// Data class to define the smaller action buttons
data class FabAction(
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit,
)

@Composable
fun MultiFloatingButton(
    modifier: Modifier = Modifier,
    actions: List<FabAction>,
    mainIcon: ImageVector = Icons.Default.MoreVert,
    closeIcon: ImageVector = Icons.Default.Close
) {
    var isExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "FabRotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Secondary, smaller action buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                actions.forEach { action ->
                    SmallFloatingActionButton(
                        onClick = {
                            action.onClick()
                            isExpanded = false
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(action.icon, contentDescription = action.description)
                    }
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            shape = CircleShape,
            modifier = modifier
        ) {
            Icon(
                imageVector = if (isExpanded) closeIcon else mainIcon,
                contentDescription = "More Options",
                modifier = Modifier.size(32.dp).rotate(rotation)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F3)
@Composable
private fun MultiFloatingButtonPreview() {
    BazaarTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            MultiFloatingButton(
                mainIcon = Icons.Default.Add,
                actions = listOf(
                    FabAction(
                        icon = Icons.Default.Share,
                        description = "Share Product",
                        onClick = { /* TODO */ }
                    ),
                    FabAction(
                        icon = Icons.Default.Settings,
                        description = "Settings",
                        onClick = { /* TODO */ }
                    )
                )
            )
        }
    }
}
