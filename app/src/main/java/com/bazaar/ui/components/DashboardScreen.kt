package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import com.bazaar.theme.md_dashboard_low_stock_background_dark
import com.bazaar.theme.md_dashboard_low_stock_background_light
import com.bazaar.theme.md_dashboard_low_stock_icon_dark
import com.bazaar.theme.md_dashboard_low_stock_icon_light
import com.bazaar.theme.md_dashboard_out_of_stock_background_dark
import com.bazaar.theme.md_dashboard_out_of_stock_background_light
import com.bazaar.theme.md_dashboard_out_of_stock_icon_light

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    productList: List<Product>,
    onTransactionsClicked: () -> Unit,
    onTotalItemsClicked: () -> Unit,
    onAddNewItemClicked: () -> Unit,
    lowStockCount: Int,
    outOfStockCount: Int
) {
    // Define dynamic colors based on theme
    val isDark = isSystemInDarkTheme()
    // Low Stock Colors
    val lowStockIconColor = if (isDark) md_dashboard_low_stock_icon_dark else md_dashboard_low_stock_icon_light
    val lowStockContainerColor = if (isDark) md_dashboard_low_stock_background_dark else md_dashboard_low_stock_background_light

    // Out of Stock Colors
    val outOfStockIconColor = if (isDark) md_dashboard_low_stock_icon_dark else md_dashboard_out_of_stock_icon_light
    val outOfStockContainerColor = if (isDark) md_dashboard_out_of_stock_background_dark else md_dashboard_out_of_stock_background_light

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, Admin",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle more actions */ }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Main Overview Card
                TotalItemsCard(count = (productList.count { !it.isDeleted }), onTotalItemsClicked = onTotalItemsClicked)
            }

            item {
                // Warning / Alert Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Low Stock",
                        count = lowStockCount,
                        icon = Icons.Filled.Warning,
                        iconTint = lowStockIconColor,
                        containerColor = lowStockContainerColor
                    )
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Out of Stock",
                        count = outOfStockCount,
                        icon = Icons.Outlined.Inventory2,
                        iconTint = outOfStockIconColor,
                        containerColor = outOfStockContainerColor
                    )
                }
            }

            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                QuickActionsGrid(
                    onTransactionsClicked = {
                        onTransactionsClicked()
                    },
                    onInventoryClicked = {
                        onTotalItemsClicked()
                    },
                    onAddNewItemClicked = {
                        onAddNewItemClicked()
                    }
                )
            }
        }
    }
}

@Composable
fun TotalItemsCard(count: Int, onTotalItemsClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable {
                onTotalItemsClicked()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Total Inventory",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
                Text(
                    text = "$count Items",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    iconTint: Color,
    containerColor: Color
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color(0xFFEEEEEE) else Color(0xFF212121)
    val subTextColor = if (isDark) Color(0xB3EEEEEE) else Color(0x99000000)
    val circleBg = if (isDark) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f)

    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(circleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = subTextColor
                )
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    onTransactionsClicked: () -> Unit,
    onInventoryClicked: () -> Unit,
    onAddNewItemClicked: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickActionButton(
            title = "Process Sale / Restock",
            subtitle = "Update item quantities",
            icon = Icons.Rounded.AddShoppingCart,
            onClick = {
                onTransactionsClicked()
            }
        )
        QuickActionButton(
            title = "Manage Inventory",
            subtitle = "Add, edit, or remove items",
            icon = Icons.Rounded.Inventory,
            onClick = {
                onInventoryClicked()
            }
        )
        QuickActionButton(
            title = "Add New Item",
            subtitle = "Create a product entry",
            icon = Icons.Filled.Add,
            onClick = {
                onAddNewItemClicked()
            }
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    BazaarTheme {
        DashboardScreen(
            productList = emptyList(),
            onTransactionsClicked = {},
            onTotalItemsClicked = {},
            onAddNewItemClicked = {},
            lowStockCount = 0,
            outOfStockCount = 0
        )
    }
}