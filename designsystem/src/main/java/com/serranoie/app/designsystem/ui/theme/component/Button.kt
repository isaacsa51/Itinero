package com.serranoie.app.designsystem.ui.theme.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.ComponentPreview
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.theme.ItineroTheme

enum class ButtonImportance {
    Primary, Secondary, Tertiary, Error
}

@Composable
fun IButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    importance: ButtonImportance = ButtonImportance.Primary,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
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
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    importance: ButtonImportance = ButtonImportance.Primary
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
        importance = importance
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
    shape: Shape = RoundedCornerShape(size = 8.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
fun IOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    importance: ButtonImportance = ButtonImportance.Primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        border = BorderStroke(
            width = IButtonDefaults.OutlinedButtonBorderWidth,
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
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    importance: ButtonImportance = ButtonImportance.Primary // Default importance
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
        importance = importance // Pass importance
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
            alpha = 0.38f
        )

        ButtonImportance.Secondary -> if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.38f
        )

        ButtonImportance.Tertiary -> if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.38f
        )

        ButtonImportance.Error -> if (enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.38f
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
) {
    val ITextButtonColors = textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ITextButtonColors,
        shape = RoundedCornerShape(size = 8.dp),
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
private fun IButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
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
        text()
    }
}

@ComponentPreview
@Composable
fun TravelerButtonPreview() {
    PreviewWrapper {
        Column {
            IButton(
                onClick = {},
                text = { Text("Test Button") },
                importance = ButtonImportance.Primary
            )

            IButton(
                onClick = {},
                text = { Text("Test Button") },
                importance = ButtonImportance.Secondary
            )

            IButton(
                onClick = {},
                text = { Text("Test Button") },
                importance = ButtonImportance.Tertiary
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
                text = { Text("Test Button") },
                importance = ButtonImportance.Primary
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Test Button") },
                importance = ButtonImportance.Secondary
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Test Button") },
                importance = ButtonImportance.Tertiary
            )

            IOutlineButton(
                onClick = {},
                text = { Text("Error Button") },
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

object IButtonDefaults {
    // TODO: File bug
    // OutlinedButton border color doesn't respect disabled state by default
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f

    // TODO: File bug
    // OutlinedButton default border width isn't exposed via ButtonDefaults
    val OutlinedButtonBorderWidth = 1.dp
}