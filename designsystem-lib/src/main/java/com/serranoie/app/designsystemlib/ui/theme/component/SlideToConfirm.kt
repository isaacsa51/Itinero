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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.SLIDE_TO_CONFIRM_ANIMATION_DURATION
import com.serranoie.app.designsystemlib.ui.utils.Constants.SLIDE_TO_CONFIRM_THRESHOLD
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.largePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.slideToConfirmDoneIconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.slideToConfirmHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.slideToConfirmIconSize
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import kotlin.math.roundToInt

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
            .clip(RoundedCornerShape(commonCornerRadius))
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
            modifier = Modifier.size(slideToConfirmIconSize),
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
            durationMillis = SLIDE_TO_CONFIRM_ANIMATION_DURATION,
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
            .clip(RoundedCornerShape(commonCornerRadius))
            .background(backgroundColor)
            .animateContentSize()
            .then(
                if (swipeComplete || isComplete) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .requiredHeight(slideToConfirmHeight),
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
                            FractionalThreshold(SLIDE_TO_CONFIRM_THRESHOLD)
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
                    .standardPadding(horizontal = extraSmallPadding, vertical = extraSmallPadding),
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
                    .size(slideToConfirmDoneIconSize),
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
