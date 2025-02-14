package org.serranoie.app.itinero.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.serranoie.app.itinero.ui.theme.ItineroTheme


@Composable
fun ItineroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape = RoundedCornerShape(size = 6.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape,
        content = content
    )
}

@Composable
fun ItineroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(size = 6.dp),
) {
    ItineroButton(
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
        ItineroButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
fun ItineroOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(size = 6.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        /*border = BorderStroke(
            width = TravelerButtonDefaults.OutlinedButtonBorderWidth,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = TravelerButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),*/
        contentPadding = contentPadding,
        content = content,
        shape = shape,
    )
}

@Composable
fun ItineroOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(size = 6.dp),
) {
    ItineroOutlineButton(
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
        ItineroButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
fun ItineroTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content,
    )
}

@Composable
fun ItineroTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    ItineroTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        ItineroButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
private fun ItineroButtonContent(
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
        ItineroButton(
            onClick = {},
            text = { Text("Test Button") }
        )
    }
}

@Preview
@Composable
fun TravelerOutlineButtonPreview() {
    ItineroTheme {
        ItineroOutlineButton(
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
            ItineroTextButton(
                onClick = {},
                text = { Text("Test Button") }
            )
        }
    }
}

@Preview
@Composable
fun TravelerLeadingIconPreview() {
    ItineroTheme {
        ItineroButton(
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

object ItineroButtonDefaults {
    // TODO: File bug
    // OutlinedButton border color doesn't respect disabled state by default
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f

    // TODO: File bug
    // OutlinedButton default border width isn't exposed via ButtonDefaults
    val OutlinedButtonBorderWidth = 1.dp
}
