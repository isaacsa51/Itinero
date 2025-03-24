package com.serranoie.app.designsystem.ui.theme.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.theme.ItineroTheme

@Composable
fun IButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp),
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape,
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
        shape = shape
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
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
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
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
    content: @Composable RowScope.() -> Unit,
) {
    val ITextButtonColors = textButtonColors(
        contentColor = MaterialTheme.colorScheme.onBackground,
        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp),
        enabled = enabled,
        border = BorderStroke(
            width = IButtonDefaults.OutlinedButtonBorderWidth,
            color = if (enabled) {
                MaterialTheme.colorScheme.outlineVariant
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = IButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),
        contentPadding = contentPadding,
        content = content,
        shape = shape,
        colors = ITextButtonColors
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
        }
    ) {
        IButtonContent(
            text = text,
            leadingIcon = leadingIcon,
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
    colors: ButtonColors,
) {
    val ITextButtonColors = textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ITextButtonColors ?: colors,
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
        Modifier
            .padding(
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

@Preview
@Composable
fun TravelerButtonPreview() {
    ItineroTheme {
        IButton(
            onClick = {},
            text = { Text("Test Button") }
        )
    }
}

@Preview
@Composable
fun TravelerOutlineButtonPreview() {
    ItineroTheme {
        IOutlineButton(
            onClick = {},
            text = { Text("Test Button") }
        )
    }
}

@Preview
@Composable
fun TravelerTextButtonPreview() {
    ItineroTheme {
        Surface {
            ITextButton(
                onClick = {},
                text = { Text("Test Button") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null
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