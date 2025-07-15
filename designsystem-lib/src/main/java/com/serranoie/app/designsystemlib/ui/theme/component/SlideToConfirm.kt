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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeProgress
import androidx.wear.compose.material.SwipeableDefaults
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.largePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.sliderHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SlideToConfirm(
    text: String,
    isComplete: Boolean,
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    doneImageVector: ImageVector = Icons.Rounded.Done,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiary,
): Boolean {
    val hapticFeedback = LocalHapticFeedback.current
    val swipeState = rememberSwipeableState(
        initialValue = Anchor.Start,
        confirmStateChange = { anchor ->
            if (anchor == Anchor.End) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onSwipe()
                true
            } else {
                false
            }
        },
    )

    val swipeFraction by remember {
        derivedStateOf { calculateSwipeFraction(swipeState.progress) }
    }

    LaunchedEffect(isComplete) {
        swipeState.animateTo(if (isComplete) Anchor.End else Anchor.Start)
    }

    Track(
        swipeState = swipeState,
        swipeFraction = swipeFraction,
        enabled = !isComplete,
        backgroundColor = backgroundColor,
        modifier = modifier,
        isComplete = isComplete,
    ) {
        AnimatedVisibility(
            visible = !isComplete,
            exit = fadeOut()
        ) {
            Hint(
                text = text,
                swipeFraction = swipeFraction,
                isLoading = false,
                onCancelPressed = { },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(PaddingValues(horizontal = Thumb.Size + smallPadding))
                    .fillMaxWidth(),
            )
        }

        if (!isComplete) {
            Thumb(
                isLoading = false,
                modifier = Modifier.offset {
                    IntOffset(swipeState.offset.value.roundToInt(), 0)
                },
                onCancelPressed = { },
            )
        }

        AnimatedVisibility(
            visible = isComplete,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sliderHeight)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(percent = 10)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = doneImageVector,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(44.dp),
                )
            }
        }
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
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    isComplete: Boolean = false,
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
            endOfTrackPx = with(density) {
                fullWidth - (horizontalPadding + horizontalPadding + Thumb.Size).toPx()
            }
        }
    }

    if (endOfTrackPx > 0 || isComplete) {
        Box(
            modifier = modifier
                .onSizeChanged { fullWidth = it.width }
                .height(sliderHeight)
                .fillMaxWidth()
                .then(
                    if (!isComplete) {
                        Modifier.swipeable(
                            enabled = enabled,
                            state = swipeState,
                            orientation = Orientation.Horizontal,
                            anchors = mapOf(
                                startOfTrackPx to Anchor.Start,
                                endOfTrackPx to Anchor.End,
                            ),
                            thresholds = thresholds,
                            velocityThreshold = Track.VelocityThreshold,
                        )
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (!isComplete) {
                        Modifier
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(percent = 10),
                            )
                            .padding(
                                PaddingValues(
                                    horizontal = horizontalPadding,
                                    vertical = smallPadding,
                                ),
                            )
                    } else {
                        Modifier
                    }
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


@Composable
fun SwipeIndicator(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(
                ratio = 1.0F,
                matchHeightConstraintsFirst = true,
            )
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = backgroundColor,
            modifier = Modifier.size(36.dp),
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwipeButton(
    text: String,
    isComplete: Boolean,
    doneImageVector: ImageVector = Icons.Rounded.Done,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiary,
    onSwipe: () -> Unit,
) {
    var containerWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val widthInPx = with(density) {
        containerWidth.toPx()
    }

    val swipeableState = rememberSwipeableState(0)
    val (swipeComplete, setSwipeComplete) = remember {
        mutableStateOf(false)
    }
    val alpha: Float by animateFloatAsState(
        targetValue = if (swipeComplete || isComplete) {
            0F
        } else {
            1F
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing,
        )
    )

    // Update anchors when width changes
    LaunchedEffect(widthInPx) {
        if (widthInPx > 0) {
            swipeableState.snapTo(0)
        }
    }

    LaunchedEffect(
        key1 = swipeableState.currentValue,
    ) {
        if (swipeableState.currentValue == 1) {
            setSwipeComplete(true)
            onSwipe()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged { size ->
                containerWidth = with(density) { size.width.toDp() }
            }
            .padding(
                vertical = basePadding
            )
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .animateContentSize()
            .then(
                if (swipeComplete || isComplete) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .requiredHeight(64.dp),
    ) {
        if (widthInPx > 0 && !isComplete) {
            val anchors = mapOf(
                0F to 0,
                widthInPx to 1,
            )

            SwipeIndicator(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .alpha(alpha)
                    .offset {
                        IntOffset(swipeableState.offset.value.roundToInt(), 0)
                    }
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ ->
                            FractionalThreshold(0.3F)
                        },
                        orientation = Orientation.Horizontal,
                    ),
                backgroundColor = backgroundColor,
            )
        }

        AnimatedVisibility(
            visible = !isComplete,
            exit = fadeOut()
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.labelLargeEmphasized,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha)
                    .padding(
                        horizontal = largePadding,
                    )
                    .offset {
                        IntOffset(swipeableState.offset.value.roundToInt(), 0)
                    },
            )
        }

        AnimatedVisibility(
            visible = swipeComplete && !isComplete,
        ) {
            LoadingIndicator(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
            )
        }
        AnimatedVisibility(
            visible = isComplete,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Icon(
                imageVector = doneImageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(44.dp),
            )
        }
    }
}

@ComponentPreview
@Composable
fun SwipeButtonPreview() {
    PreviewWrapper {
        Column {
            SwipeButton(
                text = "Swipe to confirm",
                isComplete = false,
                onSwipe = {}
            )

            SwipeButton(
                text = "Swipe to confirm",
                isComplete = true,
                onSwipe = {}
            )
        }
    }
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
                    text = "Swipe to confirm",
                    isComplete = false,
                    onSwipe = { isLoading = true },
                    modifier = Modifier,
                    doneImageVector = Icons.Rounded.Done,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                )

                Spacer(modifier = Modifier.padding(smallPadding))

                SlideToConfirm(
                    text = "Swipe to confirm",
                    isComplete = !isLoading,
                    onSwipe = { isLoading = false },
                    modifier = Modifier,
                    doneImageVector = Icons.Rounded.Done,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
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