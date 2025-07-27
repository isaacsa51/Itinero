package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_ALPHA_IDLE
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_ALPHA_RECORDING
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_ANIMATION_DURATION
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_COLOR_ANIMATION_DURATION
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_SCALE_IDLE
import com.serranoie.app.designsystemlib.ui.utils.Constants.RECORD_BUTTON_SCALE_RECORDING
import com.serranoie.app.designsystemlib.ui.utils.Constants.recordButtonMinHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.recordButtonMinSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.recordButtonPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.recordButtonSwipeThreshold
import com.serranoie.app.designsystemlib.ui.utils.Constants.recordButtonVerticalThreshold
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordButton(
    recording: Boolean,
    swipeOffset: () -> Float,
    onSwipeOffsetChange: (Float) -> Unit,
    onStartRecording: () -> Boolean,
    onFinishRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = recording, label = "record")
    val scale = transition.animateFloat(
        transitionSpec = { spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) RECORD_BUTTON_SCALE_RECORDING else RECORD_BUTTON_SCALE_IDLE }
    )
    val containerAlpha = transition.animateFloat(
        transitionSpec = { tween(RECORD_BUTTON_ANIMATION_DURATION) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) RECORD_BUTTON_ALPHA_RECORDING else RECORD_BUTTON_ALPHA_IDLE }
    )
    val iconColor = transition.animateColor(
        transitionSpec = { tween(RECORD_BUTTON_COLOR_ANIMATION_DURATION) },
        label = "record-scale",
        targetValueByState = { rec ->
            if (rec) contentColorFor(LocalContentColor.current)
            else LocalContentColor.current
        }
    )

    Box {
        // Background during recording
        Box(
            Modifier
                .matchParentSize()
                .aspectRatio(1f)
                .graphicsLayer {
                    alpha = containerAlpha.value
                    scaleX = scale.value; scaleY = scale.value
                }
                .clip(CircleShape)
                .background(LocalContentColor.current)
        )
        val scope = rememberCoroutineScope()
        val tooltipState = remember { TooltipState() }
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = {
                RichTooltip {
                    Text("Touch and hold to record")
                }
            },
            enableUserInput = false,
            state = tooltipState
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = "Record",
                tint = iconColor.value,
                modifier = modifier
                    .sizeIn(minWidth = recordButtonMinSize, minHeight = recordButtonMinHeight)
                    .padding(recordButtonPadding)
                    .clickable { }
                    .voiceRecordingGesture(
                        horizontalSwipeProgress = swipeOffset,
                        onSwipeProgressChanged = onSwipeOffsetChange,
                        onClick = { scope.launch { tooltipState.show() } },
                        onStartRecording = onStartRecording,
                        onFinishRecording = onFinishRecording,
                        onCancelRecording = onCancelRecording,
                    )
            )
        }
    }
}

private fun Modifier.voiceRecordingGesture(
    horizontalSwipeProgress: () -> Float,
    onSwipeProgressChanged: (Float) -> Unit,
    onClick: () -> Unit = {},
    onStartRecording: () -> Boolean = { false },
    onFinishRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    swipeToCancelThreshold: Dp = recordButtonSwipeThreshold,
    verticalThreshold: Dp = recordButtonVerticalThreshold,
): Modifier = this
    .pointerInput(Unit) { detectTapGestures { onClick() } }
    .pointerInput(Unit) {
        var offsetY = 0f
        var dragging = false
        val swipeToCancelThresholdPx = swipeToCancelThreshold.toPx()
        val verticalThresholdPx = verticalThreshold.toPx()

        detectDragGesturesAfterLongPress(
            onDragStart = {
                onSwipeProgressChanged(0f)
                offsetY = 0f
                dragging = true
                onStartRecording()
            },
            onDragCancel = {
                onCancelRecording()
                dragging = false
            },
            onDragEnd = {
                if (dragging) {
                    onFinishRecording()
                }
                dragging = false
            },
            onDrag = { change, dragAmount ->
                if (dragging) {
                    onSwipeProgressChanged(horizontalSwipeProgress() + dragAmount.x)
                    offsetY += dragAmount.y
                    val offsetX = horizontalSwipeProgress()
                    if (
                        offsetX < 0 &&
                        abs(offsetX) >= swipeToCancelThresholdPx &&
                        abs(offsetY) <= verticalThresholdPx
                    ) {
                        onCancelRecording()
                        dragging = false
                    }
                }
            }
        )
    }
