package com.bazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.models.Product
import com.bazaar.theme.BazaarTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListItem(
    product: Product,
    onEditClick: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onSwipeLeft: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> { // Swipe Right to Left (Delete)
                    onDelete(product.apply { isDeleted = true })
                    true // Returning true allows the dismiss animation to complete.
                }
                SwipeToDismissBoxValue.StartToEnd -> { // Swipe Left to Right
                    onSwipeLeft(product)
                    false // Return false to snap back to the original position.
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.90f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.padding(vertical = 5.dp, horizontal = 8.dp),
        enableDismissFromStartToEnd = false, // Enables left-to-right swipe
        enableDismissFromEndToStart = true,  // Enables right-to-left swipe
        backgroundContent = {

//            val color by animateColorAsState(
//                targetValue = when (dismissState.targetValue) {
//                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFD32F2F) // A more distinct red for delete
//                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
//                    SwipeToDismissBoxValue.Settled -> Color.Transparent
//                },
//                animationSpec = tween(300),
//                label = "background color"
//            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
            ) {
                // We determine the icon and its alignment based on the targetValue.
                // It's crucial to handle both directions within the same composable block
                // to ensure Compose updates it correctly during the swipe.
//                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
//                    Icon(
//                        Icons.Default.Delete,
//                        contentDescription = "Delete Icon",
//                        tint = Color.White,
//                        modifier = Modifier.align(Alignment.CenterEnd) // Align icon to the end
//                    )
//                } else if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
//                    Icon(
//                        Icons.Default.Archive, // Changed to a different dummy icon
//                        contentDescription = "Left Swipe Action",
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.align(Alignment.CenterStart) // Align icon to the start
//                    )
//                }
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.align(Alignment.CenterEnd).size(28.dp) // Align icon to the end
                )
            }
        }
    ) {
        
        ProductContent(product = product, onEditClick = onEditClick)
    }
}


@Composable
fun ProductContent(product: Product, onEditClick: (Product) -> Unit) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            draggedElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = {
            onEditClick(product)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (product.quantity > 0) {
                            Text(
                                text = "Qty: ${product.quantity}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        if (product.weight > 0) {
                            Text(
                                text = "Weight: ${product.weight} ${product.weightUnit.lowercase()}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = currencyFormat.format(product.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if(product.quantity < product.thresholdValue) {
                LowStockIndicator(
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
private fun ProductListItemPreview() {
    val product =
        Product(
            id = "0",
            name = "Premium Wireless Headphones with Extra Bass",
            quantity = 15,
            price = 249.99,
            weight = 500.0,
            weightUnit = "gm",
            createdOn = System.currentTimeMillis()
        )
    BazaarTheme {
        ProductListItem(
            product = product,
            onEditClick = {},
            onDelete = {},
            onSwipeLeft = {
                // TODO: Implement left swipe action
            }
        )
    }
}