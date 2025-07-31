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

import android.graphics.BlurMaskFilter
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.bounceClick
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

data class SwipeAction(
    val icon: androidx.compose.ui.graphics.painter.Painter? = null,
    val iconContent: @Composable (() -> Unit)? = null,
    val background: Color,
    val isUndo: Boolean = false,
    val onSwipe: () -> Unit
)

@Composable
fun SwipeableActionsBox(
    modifier: Modifier = Modifier,
    startActions: List<SwipeAction> = emptyList(),
    endActions: List<SwipeAction> = emptyList(),
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val threshold = 120f

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background actions
        if (offsetX > threshold && startActions.isNotEmpty()) {
            // Show start actions (swipe right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(startActions.first().background)
                    .padding(smallPadding), contentAlignment = Alignment.CenterStart
            ) {
                startActions.first().iconContent?.invoke()
                    ?: startActions.first().icon?.let { painter ->
                        Icon(
                            painter = painter, contentDescription = null, tint = Color.White
                        )
                    }
            }
        } else if (offsetX < -threshold && endActions.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(endActions.first().background)
                    .padding(smallPadding), contentAlignment = Alignment.CenterEnd
            ) {
                endActions.first().iconContent?.invoke()
                    ?: endActions.first().icon?.let { painter ->
                        Icon(
                            painter = painter, contentDescription = null, tint = Color.White
                        )
                    }
            }
        }

        // Main content
        Box(modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > threshold && startActions.isNotEmpty()) {
                            startActions.first().onSwipe()
                        } else if (offsetX < -threshold && endActions.isNotEmpty()) {
                            endActions.first().onSwipe()
                        }
                        offsetX = 0f // Reset position
                    }) { _, dragAmount ->
                    offsetX += dragAmount
                    // Limit the swipe distance
                    offsetX = offsetX.coerceIn(-200f, 200f)
                }
            }) {
            content()
        }
    }
}

data class SwipeCardAction(
    val icon: ImageVector, val background: Color, val onAction: () -> Unit, val toastMessage: String
)

/**
 * A card component with optional colorful header and optional animated border.
 *
 * @param modifier The modifier to be applied to the card
 * @param isCompleted Whether the card is marked as completed
 * @param shape The shape of the card
 * @param color The background color of the card (used with Surface for proper tonal elevation)
 * @param borderWidth The width of the border around the card
 * @param borderColor The color of the border around the card
 * @param headerTitle Title to display in the colorful header (if null, no header is shown)
 * @param headerColor Background color for the header
 * @param headerTextColor Text color for the header
 * @param headerIcon Optional icon to display in the header
 * @param headerIconContentDescription Content description for the header icon
 * @param content The content of the card
 * @param onClick Optional callback when the card is clicked (if null, card is not clickable)
 * @param showAnimatedBorder If true, shows an animated glitter border with static gradient base around the card. (default: false)
 * @param animatedBorderColors The list of colors for the animated border gradient.
 * @param animatedBorderWidth The width of the animated border in dp
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ICard(
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    shape: Shape = RoundedCornerShape(commonCornerRadius),
    color: Color = if (isCompleted) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainer,
    borderWidth: Dp = borderStrokeWidth,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    headerTitle: String? = null,
    headerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    headerTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    headerIcon: ImageVector? = if (isCompleted) Icons.Default.CheckCircle else null,
    headerIconContentDescription: String? = if (isCompleted) "Completed" else null,
    content: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    showAnimatedBorder: Boolean = false,
    animatedBorderColors: List<Color> = listOf(
        Color(0xFF8EE8FF),
        Color(0xFFBD92FB),
        Color(0xFFEF5CB0),
        Color(0xFF822EFF),
        Color(0xFF8EE8FF)
    ),
    animatedBorderWidth: Dp = 3.dp
) {
    val cardContent: @Composable () -> Unit = {
        Column {
            headerTitle?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = headerColor, shape = RoundedCornerShape(
                                topStart = commonCornerRadius, topEnd = commonCornerRadius
                            )
                        )
                        .padding(horizontal = basePadding, vertical = smallPadding + 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMediumEmphasized,
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

    // For animated border, we draw our own border/glitter using Modifier.drawBehind + an always-on animated sweep gradient
    val animatedModifier =
        if (showAnimatedBorder) {
            val infiniteTransition = rememberInfiniteTransition(label = "border_anim")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2600, easing = LinearEasing)
                ),
                label = "border_angle"
            )
            Modifier.drawBehind {
                val borderPx = animatedBorderWidth.toPx()
                val w = size.width
                val h = size.height
                val corner = 6.dp.toPx()
                val center = Offset(w / 2, h / 2)
                val radius = max(w, h) / 2f

                val angleRad = Math.toRadians(angle.toDouble())
                val directionAngle = angleRad + Math.PI / 4

                val startOffsetX = -radius * cos(directionAngle)
                val startOffsetY = -radius * sin(directionAngle)
                val endOffsetX = radius * cos(directionAngle)
                val endOffsetY = radius * sin(directionAngle)

                // Blurred background border for glow effect
                val blurPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = borderPx * 1.5f
                    shader = LinearGradientShader(
                        from = Offset(
                            (center.x + startOffsetX).toFloat(),
                            (center.y + startOffsetY).toFloat()
                        ),
                        to = Offset(
                            (center.x + endOffsetX).toFloat(),
                            (center.y + endOffsetY).toFloat()
                        ),
                        colors = animatedBorderColors,
                        colorStops = null
                    )
                    maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL)
                }

                // Static gradient border
                val staticGradientPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = borderPx
                    shader = LinearGradientShader(
                        from = Offset(0f, 0f),
                        to = Offset(w, h),
                        colors = animatedBorderColors,
                        colorStops = null
                    )
                }

                // Sharp foreground border with sweeping gradient
                val mainPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 16f
                    shader = LinearGradientShader(
                        from = Offset(
                            (center.x + startOffsetX).toFloat(),
                            (center.y + startOffsetY).toFloat()
                        ),
                        to = Offset(
                            (center.x + endOffsetX).toFloat(),
                            (center.y + endOffsetY).toFloat()
                        ),
                        colors = animatedBorderColors,
                        colorStops = null
                    )
                }

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawRoundRect(
                        borderPx,
                        borderPx,
                        w - borderPx,
                        h - borderPx,
                        corner,
                        corner,
                        staticGradientPaint
                    )

                    // Draw blurred background border for glow
                    canvas.nativeCanvas.drawRoundRect(
                        borderPx / 2f,
                        borderPx / 2f,
                        w - borderPx / 2f,
                        h - borderPx / 2f,
                        corner,
                        corner,
                        blurPaint
                    )

                    // Draw main sharp border with animated gradient
                    canvas.nativeCanvas.drawRoundRect(
                        borderPx / 2f,
                        borderPx / 2f,
                        w - borderPx / 2f,
                        h - borderPx / 2f,
                        corner,
                        corner,
                        mainPaint
                    )
                }
            }
        } else {
            Modifier
        }

    Surface(
        modifier = if (onClick != null) {
            modifier
                .then(animatedModifier)
                .fillMaxWidth()
                .bounceClick { onClick() }
        } else {
            modifier
                .then(animatedModifier)
                .fillMaxWidth()
        },
        shape = shape,
        color = color,
        border = if (showAnimatedBorder) null else BorderStroke(borderWidth, borderColor)
    ) {
        cardContent()
    }
}

/**
 * A swipeable card component that wraps ICard with SwipeToDismissBox functionality.
 *
 * @param modifier The modifier to be applied to the swipeable container
 * @param content The content to display in the card body
 * @param leftSwipeAction Action to perform when swiping left (EndToStart)
 * @param rightSwipeAction Action to perform when swiping right (StartToEnd)
 * @param positionalThreshold The threshold at which a swipe is considered complete (default 25%)
 * @param cardModifier The modifier to be applied to the inner ICard
 * @param isCompleted Whether the card is marked as completed
 * @param headerTitle Optional header title for the card
 * @param headerColor Background color for the header
 * @param headerTextColor Text color for the header
 * @param headerIcon Optional icon to display in the header
 * @param onClick Optional callback when the card is clicked
 */
@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    leftSwipeAction: SwipeCardAction? = null,
    rightSwipeAction: SwipeCardAction? = null,
    positionalThreshold: Float = 0.25f,
    cardModifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    headerTitle: String? = null,
    headerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    headerTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    headerIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var lastSwipeTime by remember { mutableStateOf(0L) }
    val swipeDebounceMs = 500L // Prevent multiple swipes within 500ms

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSwipeTime < swipeDebounceMs) {
            return@rememberSwipeToDismissBoxState false
        }

        when (it) {
            SwipeToDismissBoxValue.StartToEnd -> {
                rightSwipeAction?.let { action ->
                    lastSwipeTime = currentTime
                    action.onAction()
                    Toast.makeText(context, action.toastMessage, Toast.LENGTH_SHORT).show()
                }
                false // Return false to reset the card position
            }

            SwipeToDismissBoxValue.EndToStart -> {
                leftSwipeAction?.let { action ->
                    lastSwipeTime = currentTime
                    action.onAction()
                    Toast.makeText(context, action.toastMessage, Toast.LENGTH_SHORT).show()
                }
                false // Return false to reset the card position
            }

            SwipeToDismissBoxValue.Settled -> false
        }
    }, positionalThreshold = { it * positionalThreshold })

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = rightSwipeAction != null,
        enableDismissFromEndToStart = leftSwipeAction != null,
        backgroundContent = {
            SwipeDismissBackground(
                dismissState = dismissState,
                leftSwipeAction = leftSwipeAction,
                rightSwipeAction = rightSwipeAction
            )
        },
        content = {
            ICard(
                modifier = cardModifier.fillMaxWidth(),
                isCompleted = isCompleted,
                headerTitle = headerTitle,
                headerColor = headerColor,
                headerTextColor = headerTextColor,
                headerIcon = headerIcon,
                onClick = onClick,
                content = {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        content()
                    }
                })
        })
}

/**
 * Background component for SwipeableCard that shows different colors and icons
 * based on swipe direction.
 */
@Composable
private fun SwipeDismissBackground(
    dismissState: SwipeToDismissBoxState,
    leftSwipeAction: SwipeCardAction? = null,
    rightSwipeAction: SwipeCardAction? = null
) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> rightSwipeAction?.background ?: Color.Transparent
        SwipeToDismissBoxValue.EndToStart -> leftSwipeAction?.background ?: Color.Transparent
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(commonCornerRadius))
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right swipe action (shows on left side when swiping right)
        rightSwipeAction?.let { action ->
            Icon(
                imageVector = action.icon,
                contentDescription = "right swipe action",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier)

        // Left swipe action (shows on right side when swiping left)
        leftSwipeAction?.let { action ->
            Icon(
                imageVector = action.icon,
                contentDescription = "left swipe action",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@ComponentPreview
@Composable
private fun OutlinedCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            // Regular non-swipeable card without header
            ICard(isCompleted = false, content = {
                Column(modifier = Modifier.standardPadding()) {
                    Text(
                        text = "Basic Card", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "This is a simple card without header",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Card with colorful header
            ICard(
                isCompleted = false,
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
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            // Completed card with icon
            ICard(
                isCompleted = true,
                headerTitle = "Completed Task",
                headerIcon = Icons.Default.CheckCircle,
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "Task Status: Complete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Card with ticket icon
            ICard(
                isCompleted = false,
                headerTitle = "Event Ticket",
                headerColor = MaterialTheme.colorScheme.tertiaryContainer,
                headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                headerIcon = Icons.Default.ConfirmationNumber,
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "Concert Tonight", style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "The Grand Theater - 8:00 PM",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Seat: A-23",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                })

            Spacer(modifier = Modifier.padding(smallPadding))

            // Swipeable card example
            SwipeableCard(
                content = {
                    Text(
                        text = "Swipe left to archive, right to delete",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                headerTitle = "Interactive Card",
                headerColor = MaterialTheme.colorScheme.primaryContainer,
                headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                leftSwipeAction = SwipeCardAction(
                    icon = Icons.Default.CheckCircle,
                    background = MaterialTheme.colorScheme.primaryContainer,
                    onAction = { /* Handle archive */ },
                    toastMessage = "Task archived!"
                ),
                rightSwipeAction = SwipeCardAction(
                    icon = Icons.Default.Delete,
                    background = MaterialTheme.colorScheme.errorContainer,
                    onAction = { /* Handle delete */ },
                    toastMessage = "Task deleted!"
                )
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            SwipeableCard(
                content = {
                    Text(
                        text = "Swipe left to archive, right to delete",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                headerTitle = "Interactive Card",
                isCompleted = true,
                leftSwipeAction = SwipeCardAction(
                    icon = Icons.Default.CheckCircle,
                    background = MaterialTheme.colorScheme.primaryContainer,
                    onAction = { /* Handle archive */ },
                    toastMessage = "Task archived!"
                ),
                headerIcon = Icons.Default.CheckCircle,
            )

            Spacer(modifier = Modifier.padding(smallPadding))

            ICard(
                content = {
                    Column(modifier = Modifier.standardPadding()) {
                        Text(
                            text = "Animated Glitter Border",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Static gradient base + animated sweeping effect",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                showAnimatedBorder = true
            )
        }
    }
}
