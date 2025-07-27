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

package com.serranoie.app.designsystemlib.ui.theme.component.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
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
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basicElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency
import com.serranoie.app.designsystemlib.ui.utils.bounceClick
import com.serranoie.app.designsystemlib.ui.utils.designBorder
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
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
 * @param color The background color of the card (used with Surface for proper tonal elevation)
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
 * @param onClick Callback when the card is clicked
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ICard(
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    onSwipe: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(commonCornerRadius),
    color: Color = if (isCompleted) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainer,
    borderWidth: Dp = borderStrokeWidth,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    swipeable: Boolean = false,
    dragThreshold: Float = 150f,
    headerTitle: String? = null,
    headerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    headerTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    headerIcon: ImageVector? = if (isCompleted) Icons.Default.CheckCircle else null,
    headerIconContentDescription: String? = if (isCompleted) "Completed" else null,
    swipeActionsConfig: SwipeActionsConfig? = null,
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val cardContent: @Composable () -> Unit = {
        Column {
            headerTitle?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = headerColor,
                            shape = RoundedCornerShape(
                                topStart = commonCornerRadius,
                                topEnd = commonCornerRadius
                            )
                        )
                        .standardPadding()
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
            color = color,
            borderWidth = borderWidth,
            borderColor = borderColor,
            dragThreshold = dragThreshold,
            swipeActionsConfig = swipeActionsConfig,
            content = cardContent,
            onClick = onClick
        )
    } else {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .designBorder(width = borderWidth, color = borderColor)
                .bounceClick { onClick() },
            shape = shape,
            color = color
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
    shape: Shape = RoundedCornerShape(commonCornerRadius * 2),
    color: Color = MaterialTheme.colorScheme.surface,
    borderWidth: Dp = borderStrokeWidth,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    dragThreshold: Float = 150f,
    swipeActionsConfig: SwipeActionsConfig? = null,
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val offsetXState = remember { mutableFloatStateOf(0f) }
    val localIsCompletedState = remember { mutableStateOf(isCompleted) }

    // Use swipeActionsConfig if provided, otherwise use default behavior
    val effectiveThreshold = swipeActionsConfig?.threshold ?: dragThreshold
    val backgroundIcon = swipeActionsConfig?.icon ?: Icons.Default.Done
    val backgroundIconTint =
        swipeActionsConfig?.iconTint ?: MaterialTheme.colorScheme.onTertiaryContainer
    val backgroundColorConfig =
        swipeActionsConfig?.background ?: MaterialTheme.colorScheme.tertiaryContainer

    // Animation states
    val scaleState = animateFloatAsState(
        targetValue = if (offsetXState.floatValue.absoluteValue > effectiveThreshold) 0.95f else 1f,
        label = "scale"
    )

    val bgColor = if (offsetXState.floatValue.absoluteValue > effectiveThreshold) {
        backgroundColorConfig
    } else {
        Color.Transparent
    }

    // Background check icon visibility
    val iconAlphaState = animateFloatAsState(
        targetValue = if (offsetXState.floatValue.absoluteValue > effectiveThreshold) 1f else 0f,
        label = "iconAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = extraSmallPadding)
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

        Surface(
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
                    }
                )
                .designBorder(width = borderWidth, color = borderColor)
                .bounceClick { onClick() },
            shape = shape,
            color = color
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
    ICard(isCompleted = isCompleted, swipeable = false, content = {
        Column(modifier = Modifier.standardPadding()) {
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
                text = expenseName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${formatCurrency(amountOwed)} • $membersCount people",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }, onClick = { })
}

@ComponentPreview
@Composable
private fun OutlinedCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            // Regular non-swipeable card without header
            ICard(isCompleted = false, swipeable = false, content = {
                Column(modifier = Modifier.standardPadding()) {
                    Text(
                        text = "Basic Card",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "This is a simple card without header",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }, onClick = { })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Card with colorful header
            ICard(
                isCompleted = false,
                swipeable = false,
                headerTitle = "Card with Header",
                headerColor = MaterialTheme.colorScheme.primaryContainer,
                headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "This card has a colorful header",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Swipeable card with header
            ICard(
                isCompleted = false,
                onSwipe = { /* Handle swipe action */ },
                swipeable = true,
                headerTitle = "Swipeable Card",
                headerIcon = Icons.Default.Done,
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "Swipe me to mark as completed",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Swipeable card with custom SwipeActionsConfig
            ICard(
                isCompleted = false,
                onSwipe = { /* Handle swipe action */ },
                swipeable = true,
                headerTitle = "Custom Swipe Card",
                headerIcon = Icons.Default.Done,
                swipeActionsConfig = SwipeActionsConfig(
                    threshold = 0.3f,
                    icon = Icons.Default.CheckCircle,
                    iconTint = MaterialTheme.colorScheme.primary,
                    background = MaterialTheme.colorScheme.primaryContainer,
                    stayDismissed = false,
                    onDismiss = { /* Custom dismiss action */ }),
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "Swipe me with custom config",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Completed swipeable card with header
            ICard(
                isCompleted = true,
                onSwipe = { /* Handle swipe action */ },
                swipeable = true,
                headerTitle = "Completed Card",
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "This card is already marked as completed",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Regular expense card (current user owes money)
            ExpenseCard(
                expenseName = "Dinner at La Taquería",
                membersCount = 4,
                amountOwed = 56.75,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            // Expense card where others owe the user
            ExpenseCard(
                expenseName = "Movie Tickets",
                membersCount = 3,
                amountOwed = 32.50,
                isYours = true,
                icon = Icons.Default.ConfirmationNumber
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            // Completed expense card
            ExpenseCard(
                expenseName = "Groceries",
                membersCount = 2,
                amountOwed = 45.20,
                isCompleted = true,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            // Completed expense card that was yours
            ExpenseCard(
                expenseName = "Uber ride with a very long name that should be truncated",
                membersCount = 3,
                amountOwed = 12.80,
                isYours = true,
                isCompleted = false,
                icon = Icons.Default.Restaurant
            )
        }
    }
}
