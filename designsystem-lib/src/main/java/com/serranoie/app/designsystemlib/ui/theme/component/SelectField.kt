package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.iconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.selectFieldContentPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.selectFieldElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.selectFieldIconSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.selectFieldLabelBottomPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.selectFieldLabelStartPadding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A custom field for selections like dropdowns and date pickers that looks like
 * an outlined text field but is optimized for selection rather than text input.
 */
@Composable
fun SelectField(
    value: String,
    onSelect: () -> Unit,
    label: String,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleHeader: Boolean? = true,
    containerColor: Color? = MaterialTheme.colorScheme.surfaceContainer,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier) {
        if (titleHeader == true) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    bottom = selectFieldLabelBottomPadding
                )
            )
        }

        Surface(
            shape = RoundedCornerShape(commonCornerRadius),
            border = BorderStroke(borderStrokeWidth, borderColor),
            tonalElevation = selectFieldElevation,
            color = containerColor ?: Color.Transparent,
            modifier = Modifier.clickable(
                enabled = enabled,
                indication = null,
                interactionSource = interactionSource
            ) { onSelect() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(selectFieldContentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(iconSize)
                    )
                }

                Spacer(modifier = Modifier.width(selectFieldIconSpacing))

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(selectFieldIconSpacing))
                    trailingIcon()
                }
            }
        }
    }
}

@Preview
@Composable
private fun SelectFieldPreview() {
    PreviewWrapper {
        Column(Modifier.padding(basePadding)) {
            SelectField(
                value = "Food",
                onSelect = { },
                label = "Category",
                titleHeader = true,
                leadingIcon = androidx.compose.material.icons.Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.height(basePadding))

            SelectField(
                value = "2023-07-15",
                onSelect = { },
                titleHeader = false,
                label = "Date",
                leadingIcon = androidx.compose.material.icons.Icons.Default.CalendarToday
            )
        }
    }
}
