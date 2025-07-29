package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.utils.Constants.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.buttonHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.buttonIconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.buttonLargeHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.buttonSmallHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Utils.weakHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ButtonImportance {
    Primary, Secondary, Tertiary, Error
}

@Composable
fun IButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: RoundedCornerShape = RoundedCornerShape(size = commonCornerRadius),
    importance: ButtonImportance = ButtonImportance.Primary,
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonBounceScale"
    )

    val onClickWithFeedback = {
        if (enabled && enableHapticFeedback) {
            view.weakHapticFeedback()
        }
        onClick()
    }

    Button(
        onClick = {
            isPressed = true
            onClickWithFeedback()
            // Reset pressed state after a short delay
            coroutineScope.launch {
                delay(150)
                isPressed = false
            }
        },
        modifier = modifier
            .height(height)
            .scale(scale),
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape,
        colors = buttonColorsForImportance(importance),
        content = content,
    )
}

@Composable
fun IButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(size = commonCornerRadius),
    importance: ButtonImportance = ButtonImportance.Primary,
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true
) {
    IButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
        shape = shape,
        importance = importance,
        height = height,
        enableHapticFeedback = enableHapticFeedback
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
private fun buttonColorsForImportance(importance: ButtonImportance): ButtonColors {
    return when (importance) {
        ButtonImportance.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

        ButtonImportance.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )

        ButtonImportance.Tertiary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ButtonImportance.Error -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        )
    }
}

@Composable
fun IIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector,
    shape: Shape = RoundedCornerShape(size = commonCornerRadius),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true
) {
    val view = LocalView.current

    val onClickWithFeedback = {
        if (enabled && enableHapticFeedback) {
            view.weakHapticFeedback()
        }
        onClick()
    }

    Button(
        onClick = onClickWithFeedback,
        modifier = modifier
            .height(height),
        enabled = enabled,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.size(buttonIconSize)
        )
    }
}


@Composable
fun IOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(size = commonCornerRadius),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    importance: ButtonImportance = ButtonImportance.Primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonBounceScale"
    )

    val onClickWithFeedback = {
        if (enabled && enableHapticFeedback) {
            view.weakHapticFeedback()
        }
        onClick()
    }

    OutlinedButton(
        onClick = {
            isPressed = true
            onClickWithFeedback()
            // Reset pressed state after a short delay
            coroutineScope.launch {
                delay(150)
                isPressed = false
            }
        },
        modifier = modifier
            .height(height)
            .scale(scale),
        enabled = enabled,
        border = BorderStroke(
            width = borderStrokeWidth,
            color = borderColorForImportance(importance, enabled),
        ),
        contentPadding = contentPadding,
        content = content,
        shape = shape,
        colors = outlinedButtonColorsForImportance(importance, backgroundColor)
    )
}

@Composable
fun IOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable() (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(size = commonCornerRadius),
    importance: ButtonImportance = ButtonImportance.Primary,
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true
) {
    IOutlineButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
        importance = importance,
        height = height,
        enableHapticFeedback = enableHapticFeedback
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
private fun outlinedButtonColorsForImportance(
    importance: ButtonImportance, backgroundColor: Color?
): ButtonColors {
    return ButtonDefaults.outlinedButtonColors(
        contentColor = when (importance) {
            ButtonImportance.Primary -> MaterialTheme.colorScheme.primary
            ButtonImportance.Secondary -> MaterialTheme.colorScheme.secondary
            ButtonImportance.Tertiary -> MaterialTheme.colorScheme.onSurfaceVariant
            ButtonImportance.Error -> MaterialTheme.colorScheme.error
        }, containerColor = backgroundColor ?: Color.Transparent
    )
}

@Composable
private fun borderColorForImportance(importance: ButtonImportance, enabled: Boolean): Color {
    return when (importance) {
        ButtonImportance.Primary -> if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface.copy(
            alpha = DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
        )

        ButtonImportance.Secondary -> if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface.copy(
            alpha = DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
        )

        ButtonImportance.Tertiary -> if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface.copy(
            alpha = DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
        )

        ButtonImportance.Error -> if (enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(
            alpha = DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
        )
    }
}

@Composable
fun ITextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable() (() -> Unit)? = null,
    height: Dp = buttonHeight,
    enableHapticFeedback: Boolean = true
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonBounceScale"
    )

    val ITextButtonColors = textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    val onClickWithFeedback = {
        if (enabled && enableHapticFeedback) {
            view.weakHapticFeedback()
        }
        onClick()
    }

    TextButton(
        onClick = {
            isPressed = true
            onClickWithFeedback()
            // Reset pressed state after a short delay
            coroutineScope.launch {
                delay(150)
                isPressed = false
            }
        },
        modifier = modifier
            .height(height)
            .scale(scale),
        enabled = enabled,
        colors = ITextButtonColors,
        shape = RoundedCornerShape(size = commonCornerRadius),
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun IButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = buttonIconSize)) {
            leadingIcon()
        }
    }

    Box(
        Modifier.padding(
            start = if (leadingIcon != null) {
                ButtonDefaults.IconSpacing
            } else {
                0.dp
            },
        ),
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.labelLargeEmphasized) {
            text()
        }
    }
}

@ComponentPreview
@Composable
fun TravelerButtonPreview() {
    PreviewWrapper {
        Column {
            IButton(
                onClick = {},
                text = { Text("Standard Button") },
                importance = ButtonImportance.Primary
            )

            IButton(
                onClick = {},
                text = { Text("Small Button") },
                importance = ButtonImportance.Secondary,
                height = buttonSmallHeight
            )

            IButton(
                onClick = {},
                text = { Text("Large Button") },
                importance = ButtonImportance.Tertiary,
                height = buttonLargeHeight
            )

            IButton(
                onClick = {},
                text = { Text("Error Button") },
                importance = ButtonImportance.Error
            )
        }
    }
}

@ComponentPreview
@Composable
fun TravelerOutlineButtonPreview() {
    PreviewWrapper {
        Column {
            IOutlineButton(
                onClick = {},
                text = { Text("Standard Outline") },
                importance = ButtonImportance.Primary
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Small Outline") },
                importance = ButtonImportance.Secondary,
                height = buttonSmallHeight
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Large Outline") },
                importance = ButtonImportance.Tertiary,
                height = buttonLargeHeight
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Error Outline") },
                importance = ButtonImportance.Error
            )
        }
    }
}

@Preview
@Composable
fun TravelerTextButtonPreview() {
    ItineroTheme {
        Surface {
            ITextButton(onClick = {}, text = { Text("Test Button") })
        }
    }
}

@Preview
@Composable
fun TravelerLeadingIconPreview() {
    ItineroTheme {
        IButton(
            onClick = {},
            text = { Text("Test Button") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Favorite, contentDescription = null
                )
            },
        )
    }
}

object IButtonDefaults
