package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard

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
    shape: Shape = RoundedCornerShape(8.dp),
    durationMillis: Int = 1500,
    gradientWidth: Float = 500f
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
        MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        Color(0x994A8DD8),
        Color(0xAD784CF0),
        Color(0xAD4A8DD8),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
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


@Composable
private fun ExampleCard(
    title: String, subtitle: String, description: String
) {
    ICard(
        swipeable = false, isCompleted = false, modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.shimmerable()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.shimmerable()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.shimmerable()
            )
        }
    }
}

@ComponentPreview
@Composable
private fun InteractiveShimmerPreview() {
    var isLoading by remember { mutableStateOf(true) }

    PreviewWrapper {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isLoading) "Shimmer Effect Active" else "Normal Content",
                style = MaterialTheme.typography.headlineSmall
            )

            ShimmerProvider(isLoading = isLoading) {
                ExampleCard(
                    title = "Interactive Example",
                    subtitle = "Tap to toggle shimmer effect",
                    description = "This card demonstrates how the shimmer effect works when loading state changes."
                )
            }

            Text(
                text = "Shimmer Color Variations", style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Primary to Secondary", style = MaterialTheme.typography.bodyLarge
            )
            ShimmerProvider(isLoading = true) {
                ICard(
                    swipeable = false, isCompleted = false, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Primary to Secondary shimmer",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .shimmerable(
                                startColor = MaterialTheme.colorScheme.primaryContainer,
                                endColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                    )
                }
            }

            Text(
                text = "Tertiary to Surface", style = MaterialTheme.typography.bodyLarge
            )
            ShimmerProvider(isLoading = true) {
                ICard(
                    swipeable = false, isCompleted = false, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tertiary to Surface shimmer",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .shimmerable(
                                startColor = MaterialTheme.colorScheme.tertiaryContainer,
                                endColor = MaterialTheme.colorScheme.surface
                            )
                    )
                }
            }

            Text(
                text = "Subtle Outline Shimmer", style = MaterialTheme.typography.bodyLarge
            )
            ShimmerProvider(isLoading = true) {
                ICard(
                    swipeable = false, isCompleted = false, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Subtle outline-based shimmer effect",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .shimmerable(
                                startColor = MaterialTheme.colorScheme.surface,
                                endColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Text(text = "AI suggestion/loader shimmer", style = MaterialTheme.typography.bodyLarge)

            ShimmerProvider(isLoading = true) {
                ICard(
                    swipeable = false, isCompleted = false, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Subtle outline-based shimmer effect",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .AIShimmer()
                    )
                }
            }
        }
    }
}
