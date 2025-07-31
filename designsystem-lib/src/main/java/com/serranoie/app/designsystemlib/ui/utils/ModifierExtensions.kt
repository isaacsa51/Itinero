/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ModifierExtensions.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 23 julio 2025
 */

package com.serranoie.app.designsystemlib.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.basicElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius

/**
 * Applies standard padding using the design system's base padding.
 *
 * @param horizontal Horizontal padding value. Defaults to basePadding (16.dp)
 * @param vertical Vertical padding value. Defaults to basePadding (16.dp)
 */
fun Modifier.standardPadding(
    horizontal: Dp = basePadding,
    vertical: Dp = basePadding
): Modifier = this.padding(horizontal = horizontal, vertical = vertical)

/**
 * Creates a Box that centers its content both vertically and horizontally.
 */
@Composable
fun Modifier.centerContent(
    content: @Composable () -> Unit
): Unit {
    Box(
        modifier = this.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Animates the visibility of a composable using alpha transitions.
 * Provides a simple fade-in and fade-out effect based on a visibility condition.
 *
 * @param isVisible Boolean condition that determines visibility
 * @param durationMillis Duration of the fade animation in milliseconds
 */
@Composable
fun Modifier.animateVisibility(
    isVisible: Boolean,
    durationMillis: Int = 300
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis),
        label = "alphaAnimation"
    )
    return this.alpha(alpha)
}

/**
 * Animated visibility wrapper with common enter/exit transitions.
 */
@Composable
fun Modifier.animatedVisibility(
    visible: Boolean,
    enter: EnterTransition = androidx.compose.animation.fadeIn(animationSpec = tween(300)) + androidx.compose.animation.scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300)
    ),
    exit: ExitTransition = androidx.compose.animation.fadeOut(animationSpec = tween(300)) + androidx.compose.animation.scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(300)
    ),
    content: @Composable () -> Unit
): Unit {
    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
        modifier = this
    ) {
        content()
    }
}

/**
 * Slide in from bottom animation
 */
@Composable
fun Modifier.slideInFromBottom(
    visible: Boolean,
    content: @Composable () -> Unit
): Unit {
    AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(400)
        ) + androidx.compose.animation.fadeIn(animationSpec = tween(400)),
        exit = androidx.compose.animation.slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(400)
        ) + androidx.compose.animation.fadeOut(animationSpec = tween(400)),
        modifier = this
    ) {
        content()
    }
}

@Composable
fun Modifier.bounceClick(onClick: () -> Unit): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounceScale"
    )
    
    return this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}

@Composable
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = this.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick
)

/**
 * Add border to a composable, following theming
 */
@Composable
fun Modifier.designBorder(
    width: Dp = borderStrokeWidth,
    color: Color = MaterialTheme.colorScheme.outline
): Modifier = this.border(width, color, RoundedCornerShape(commonCornerRadius))

/**
 * Basic elevation shadow modifier.
 */
@Composable
fun Modifier.elevationShadow(elevation: Dp = basicElevation): Modifier =
    this.shadow(elevation, RoundedCornerShape(commonCornerRadius))

/**
 * State of the shimmer effect.
 */
data class ShimmerState(val isLoading: Boolean)

/**
 * Local composition local for the shimmer effect.
 */
val LocalShimmerState = compositionLocalOf { ShimmerState(isLoading = false) }

/**
 * Provider for the shimmer effect.
 */
@Composable
fun ShimmerProvider(
    isLoading: Boolean = true, content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalShimmerState provides ShimmerState(isLoading = isLoading), content = content
    )
}

@Composable
fun Modifier.shimmerable(
    startColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    endColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), // Adjusted for a common shimmer look
    shape: Shape = RoundedCornerShape(8.dp),
    durationMillis: Int = 1500, // Slightly longer duration can feel smoother
    gradientWidth: Float = 500f // Controls the width of the shimmer highlight
): Modifier {
    val shimmerState = LocalShimmerState.current
    val isLoading = shimmerState.isLoading
    if (!isLoading) return this

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, // Start with the gradient's beginning edge at the start of the composable
        targetValue = 1f + (gradientWidth / 1000f), // Ensure the gradient sweeps completely across
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing // Use LinearEasing for a smooth, continuous loop
            ), repeatMode = RepeatMode.Restart
        ), label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        startColor, endColor, startColor
    )

    return this
        .background(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                // Adjust start and end based on the component's size and translateAnim
                // This version makes the gradient move diagonally.
                // You might need to adjust based on the desired shimmer direction.
                start = Offset(
                    -gradientWidth + (translateAnim * (gradientWidth * 3)),
                    -gradientWidth + (translateAnim * (gradientWidth * 3))
                ), end = Offset(
                    translateAnim * (gradientWidth * 3), translateAnim * (gradientWidth * 3)
                )
            ), shape = shape
        )
        .drawWithContent {
            // Do not draw the actual content when shimmering
        }
}

@Composable
fun Modifier.AIShimmer(
    shape: Shape = RoundedCornerShape(8.dp), durationMillis: Int = 1500, gradientWidth: Float = 500f
): Modifier {
    val shimmerState = LocalShimmerState.current
    val isLoading = shimmerState.isLoading
    if (!isLoading) return this

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f + (gradientWidth / 1000f),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis, easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        Color(0x994A8DD8),
        Color(0xAD784CF0),
        Color(0xAD4A8DD8),
        Color(0xADD84A93),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )

    return this
        .background(
            brush = Brush.linearGradient(
                colors = shimmerColors, start = Offset(
                    -gradientWidth + (translateAnim * (gradientWidth * 2)),
                    -gradientWidth + (translateAnim * (gradientWidth * 2))
                ), end = Offset(
                    translateAnim * (gradientWidth * 2), translateAnim * (gradientWidth * 2)
                )
            ), shape = shape
        )
        .drawWithContent {
            // Do not draw the actual content when shimmering
        }
}

/**
 * Applies disabled alpha styling to any composable.
 */
fun Modifier.disabledAlpha(enabled: Boolean = true): Modifier =
    if (enabled) this else this.alpha(Constants.DISABLED_ALPHA)
