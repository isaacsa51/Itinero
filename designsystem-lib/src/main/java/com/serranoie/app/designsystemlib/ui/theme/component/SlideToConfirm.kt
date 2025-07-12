/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SlideToConfirm.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 30 junio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeProgress
import androidx.wear.compose.material.SwipeableDefaults
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.sliderHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SlideToConfirm(
    isLoading: Boolean,
    onAcceptSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    currentStatus: Boolean,
    onCancelPressed: () -> Unit,
    hint: String = "Swipe to confirm",
): Boolean {
    val hapticFeedback = LocalHapticFeedback.current
    val swipeState = rememberSwipeableState(
        initialValue = Anchor.Start,
        confirmStateChange = { anchor ->
            if (anchor == Anchor.End) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onAcceptSwipe()
                true
            } else {
                false
            }
        },
    )

    val swipeFraction by remember {
        derivedStateOf { calculateSwipeFraction(swipeState.progress) }
    }

    LaunchedEffect(isLoading) {
        swipeState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    Track(
        swipeState = swipeState,
        swipeFraction = swipeFraction,
        enabled = !isLoading,
        modifier = modifier,
    ) {
        Hint(
            text = hint,
            swipeFraction = swipeFraction,
            isLoading = isLoading,
            onCancelPressed = onCancelPressed,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(PaddingValues(horizontal = Thumb.Size + smallPadding))
                .fillMaxWidth(),
        )

        Thumb(
            isLoading = isLoading,
            modifier = Modifier.offset {
                IntOffset(swipeState.offset.value.roundToInt(), 0)
            },
            onCancelPressed = onCancelPressed,
        )
    }

    return swipeState.offset.value == Track.EndOfTrackPx
}

@OptIn(ExperimentalWearMaterialApi::class)
fun calculateSwipeFraction(progress: SwipeProgress<Anchor>): Float {
    val atAnchor = progress.from == progress.to
    val fromStart = progress.from == Anchor.Start
    return if (atAnchor) {
        if (fromStart) 0f else 1f
    } else {
        if (fromStart) progress.fraction else 1f - progress.fraction
    }
}

enum class Anchor { Start, End }

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun Track(
    swipeState: SwipeableState<Anchor>,
    swipeFraction: Float,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit),
) {
    val density = LocalDensity.current
    var fullWidth by remember { mutableIntStateOf(0) }
    var endOfTrackPx by remember { mutableFloatStateOf(0f) } // Store endOfTrackPx as state

    val horizontalPadding = smallPadding

    val startOfTrackPx = 0f

    val snapThreshold = 0.8f
    val thresholds = { from: Anchor, _: Anchor ->
        if (from == Anchor.Start) {
            FractionalThreshold(snapThreshold)
        } else {
            FractionalThreshold(1f - snapThreshold)
        }
    }

    LaunchedEffect(fullWidth) {
        if (fullWidth > 0) {
            endOfTrackPx = with(density) { fullWidth - (2 * horizontalPadding + Thumb.Size).toPx() }
        }
    }

    if (endOfTrackPx > 0) {
        Box(
            modifier = modifier
                .onSizeChanged { fullWidth = it.width }
                .height(sliderHeight)
                .fillMaxWidth()
                .swipeable(
                    enabled = enabled,
                    state = swipeState,
                    orientation = Orientation.Horizontal,
                    anchors = mapOf(
                        startOfTrackPx to Anchor.Start,
                        endOfTrackPx to Anchor.End,
                    ),
                    thresholds = thresholds, velocityThreshold = Track.VelocityThreshold,
                )
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(percent = 10),
                )
                .padding(
                    PaddingValues(
                        horizontal = horizontalPadding,
                        vertical = smallPadding,
                    ),
                ),
            content = content,
        )
    } else {
        // Placeholder or loading state while endOfTrackPx is being calculated
        Box(modifier = modifier
            .onSizeChanged { fullWidth = it.width }
            .height(sliderHeight)
            .fillMaxWidth())
    }
}

@Composable
fun Thumb(
    isLoading: Boolean,
    modifier: Modifier,
    onCancelPressed: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(Thumb.Size)
            .clickable { if (isLoading) onCancelPressed() }
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(commonCornerRadius),
            )
            .padding(smallPadding),
    ) {
        if (isLoading) {
            Icon(imageVector = Icons.Rounded.Done, contentDescription = "Mark it purchased")
        } else {
            Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = "Confirm")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Hint(
    text: String,
    swipeFraction: Float,
    isLoading: Boolean,
    onCancelPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hintText = if (isLoading) "Tap to cancel" else text
    val hintColor =
        if (isLoading) MaterialTheme.colorScheme.surface else calculateHintTextColor(swipeFraction)

    Text(
        text = hintText,
        color = hintColor,
        style = MaterialTheme.typography.labelLargeEmphasized,
        modifier = modifier
            .clickable { if (isLoading) onCancelPressed() }
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center),
    )
}

@Composable
fun calculateHintTextColor(swipeFraction: Float): Color {
    val endOfFadeFraction = 0.35f
    val fraction = (swipeFraction / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
        fraction,
    )
}

private object Thumb {
    val Size = 40.dp
}

private object Track {
    @OptIn(ExperimentalWearMaterialApi::class)
    val VelocityThreshold = SwipeableDefaults.VelocityThreshold * 10
    var EndOfTrackPx: Float = 0f
}

@ComponentPreview
@Composable
private fun Preview() {
    var isLoading by remember { mutableStateOf(false) }

    PreviewWrapper {
        Column(
            verticalArrangement = Arrangement.Absolute.spacedBy(smallPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column() {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Normal")
                    Spacer(modifier = Modifier.weight(1f))
                    Thumb(isLoading = false, modifier = Modifier, onCancelPressed = { })
                }

                SlideToConfirm(
                    isLoading = isLoading,
                    onAcceptSwipe = { isLoading = true },
                    onCancelPressed = { isLoading = false },
                    currentStatus = false,
                )

                Spacer(modifier = Modifier.padding(smallPadding))

                SlideToConfirm(
                    isLoading = !isLoading,
                    onAcceptSwipe = { isLoading = false },
                    onCancelPressed = { isLoading = false },
                    currentStatus = true,
                )

                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(percent = 50),
                    onClick = { isLoading = false },
                ) {
                    Text(text = "Cancel loading", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}