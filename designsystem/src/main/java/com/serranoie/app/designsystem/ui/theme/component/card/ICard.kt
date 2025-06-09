/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ICard.kt
 - Project: Itinero
 - Module: Itinero.designsystem.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 June 2025
 */

package com.serranoie.app.designsystem.ui.theme.component.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.ComponentPreview
import com.serranoie.app.designsystem.ui.PreviewWrapper
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

data class SwipeActionsConfig(
    val threshold: Float,
    val icon: ImageVector,
    val iconTint: Color,
    val background: Color,
    val stayDismissed: Boolean,
    val onDismiss: () -> Unit,
)

/**
 * A card component with optional swipeable functionality and colorful header.
 *
 * @param modifier The modifier to be applied to the card
 * @param isCompleted Whether the card is marked as completed
 * @param onSwipe Callback when the card is swiped (only used in swipeable variant)
 * @param shape The shape of the card
 * @param colors The colors of the card
 * @param elevation The elevation of the card
 * @param borderWidth The width of the border around the card
 * @param borderColor The color of the border around the card
 * @param swipeable Whether the card should be swipeable
 * @param dragThreshold The threshold at which a swipe is considered complete
 * @param headerTitle Title to display in the colorful header (if null, no header is shown)
 * @param headerColor Background color for the header
 * @param headerTextColor Text color for the header
 * @param headerIcon Optional icon to display in the header
 * @param headerIconContentDescription Content description for the header icon
 * @param swipeActionsConfig Configuration for swipe actions (if null, uses default swipe behavior)
 * @param content The content of the card
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ICard(
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    onSwipe: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: CardColors = CardDefaults.elevatedCardColors(
        containerColor = if (isCompleted) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.surface
    ),
    elevation: Dp = 2.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    swipeable: Boolean = false,
    dragThreshold: Float = 150f,
    headerTitle: String? = null,
    headerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    headerTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    headerIcon: ImageVector? = if (isCompleted) Icons.Default.CheckCircle else null,
    headerIconContentDescription: String? = if (isCompleted) "Completed" else null,
    swipeActionsConfig: SwipeActionsConfig? = null,
    content: @Composable () -> Unit
) {
    val cardContent: @Composable () -> Unit = {
        Column {
            // Add colorful header if title is provided
            headerTitle?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = headerColor,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = headerTextColor
                        )

                        // Show icon if provided
                        headerIcon?.let {
                            Icon(
                                imageVector = headerIcon,
                                contentDescription = headerIconContentDescription,
                                tint = headerTextColor
                            )
                        }
                    }
                }
            }

            // Main content
            content()
        }
    }

    if (swipeable && onSwipe != null) {
        ISwipeableCard(
            modifier = modifier,
            isCompleted = isCompleted,
            onSwipe = onSwipe,
            shape = shape,
            colors = colors,
            elevation = elevation,
            borderWidth = borderWidth,
            borderColor = borderColor,
            dragThreshold = dragThreshold,
            swipeActionsConfig = swipeActionsConfig,
            content = cardContent
        )
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = borderWidth, color = borderColor, shape = shape
                ), shape = shape, elevation = CardDefaults.cardElevation(elevation), colors = colors
        ) {
            cardContent()
        }
    }
}

@Deprecated("SwipeableOutlinedCard is deprecated. Use SwipeToDismiss with SwipeActions instead.")
@Composable
private fun ISwipeableCard(
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    onSwipe: () -> Unit,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: Dp = 2.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    dragThreshold: Float = 150f,
    swipeActionsConfig: SwipeActionsConfig? = null,
    content: @Composable () -> Unit
) {
    val offsetXState = remember { mutableStateOf(0f) }
    val localIsCompletedState = remember { mutableStateOf(isCompleted) }

    // Use swipeActionsConfig if provided, otherwise use default behavior
    val effectiveThreshold = swipeActionsConfig?.let { it.threshold * 300f } ?: dragThreshold
    val backgroundIcon = swipeActionsConfig?.icon ?: Icons.Default.Done
    val backgroundIconTint =
        swipeActionsConfig?.iconTint ?: MaterialTheme.colorScheme.onTertiaryContainer
    val backgroundColorConfig =
        swipeActionsConfig?.background ?: MaterialTheme.colorScheme.tertiaryContainer

    // Animation states
    val scaleState = animateFloatAsState(
        targetValue = if (offsetXState.value.absoluteValue > effectiveThreshold) 0.95f else 1f,
        label = "scale"
    )

    val bgColor = if (offsetXState.value.absoluteValue > effectiveThreshold) {
        backgroundColorConfig
    } else {
        Color.Transparent
    }

    // Background check icon visibility
    val iconAlphaState = animateFloatAsState(
        targetValue = if (offsetXState.value.absoluteValue > effectiveThreshold) 1f else 0f,
        label = "iconAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Background that appears during swipe
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(bgColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = backgroundIcon,
                contentDescription = "Mark as done",
                tint = backgroundIconTint,
                modifier = Modifier.alpha(iconAlphaState.value)
            )
        }

        // The actual card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetXState.value.roundToInt(), 0) }
                .scale(scaleState.value)
                .alpha(1f)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // Only allow dragging if not already completed
                        if (!localIsCompletedState.value) {
                            offsetXState.value = offsetXState.value + delta
                        }
                    },
                    onDragStopped = {
                        if (offsetXState.value.absoluteValue > effectiveThreshold) {
                            localIsCompletedState.value = true
                            swipeActionsConfig?.onDismiss?.invoke() ?: onSwipe()
                        }
                        // Reset position with animation
                        offsetXState.value = 0f
                    })
                .border(
                    width = borderWidth, color = borderColor, shape = shape
                ), shape = shape, elevation = CardDefaults.cardElevation(elevation), colors = colors
        ) {
            content()
        }
    }
}

@Composable
private fun ExpenseCard(
    expenseName: String,
    membersCount: Int,
    amountOwed: Double,
    isYours: Boolean = false,
    isCompleted: Boolean = false,
    icon: ImageVector
) {
    ICard(
        swipeable = false, isCompleted = isCompleted
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isYours) "You are owed" else "You owe",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isYours) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = expenseName, style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${String.format("%.2f", amountOwed)} • $membersCount people",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@ComponentPreview
@Composable
private fun OutlinedCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.padding(16.dp)) {
            // Regular non-swipeable card without header
            ICard(
                swipeable = false, isCompleted = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Basic Card", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "This is a simple card without header",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Card with colorful header
            ICard(
                swipeable = false,
                isCompleted = false,
                headerTitle = "Card with Header",
                headerColor = MaterialTheme.colorScheme.primaryContainer,
                headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "This card has a colorful header",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Swipeable card with header
            ICard(
                swipeable = true,
                isCompleted = false,
                onSwipe = { /* Handle swipe action */ },
                headerTitle = "Swipeable Card",
                headerIcon = Icons.Default.Done
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Swipe me to mark as completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Swipeable card with custom SwipeActionsConfig
            ICard(
                swipeable = true,
                isCompleted = false,
                onSwipe = { /* Handle swipe action */ },
                headerTitle = "Custom Swipe Card",
                headerIcon = Icons.Default.Done,
                swipeActionsConfig = SwipeActionsConfig(
                    threshold = 0.3f,
                    icon = Icons.Default.CheckCircle,
                    iconTint = MaterialTheme.colorScheme.primary,
                    background = MaterialTheme.colorScheme.primaryContainer,
                    stayDismissed = false,
                    onDismiss = { /* Custom dismiss action */ })) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Swipe me with custom config",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Completed swipeable card with header
            ICard(
                swipeable = true,
                isCompleted = true,
                onSwipe = { /* Handle swipe action */ },
                headerTitle = "Completed Card"
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "This card is already marked as completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Regular expense card (current user owes money)
            ExpenseCard(
                expenseName = "Dinner at La Taquería",
                membersCount = 4,
                amountOwed = 56.75,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Expense card where others owe the user
            ExpenseCard(
                expenseName = "Movie Tickets",
                membersCount = 3,
                amountOwed = 32.50,
                isYours = true,
                icon = Icons.Default.ConfirmationNumber
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Completed expense card
            ExpenseCard(
                expenseName = "Groceries",
                membersCount = 2,
                amountOwed = 45.20,
                isCompleted = true,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Completed expense card that was yours
            ExpenseCard(
                expenseName = "Uber ride",
                membersCount = 3,
                amountOwed = 12.80,
                isYours = true,
                isCompleted = false,
                icon = Icons.Filled.Restaurant
            )
        }
    }
}
