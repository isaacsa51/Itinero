package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupContainerPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupDividerPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupExpandedPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupIconSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupItemPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupItemSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupSmallCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupTitleBottomPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.settingsGroupVerticalPadding
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

/**
 * A flexible settings group container that can hold any composable content.
 *
 * @param modifier Modifier to be applied to the container
 * @param title Optional title displayed above the group
 * @param outline Whether to show an outline border around the group
 * @param content The composable content to be displayed inside the group
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlexibleListGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    outline: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .standardPadding()
            .padding(vertical = settingsGroupVerticalPadding)
    ) {
        title?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLargeEmphasized,
                modifier = Modifier.padding(bottom = settingsGroupTitleBottomPadding)
            )
        }

        Surface(
            shape = RoundedCornerShape(commonCornerRadius),
            tonalElevation = settingsGroupElevation,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = if (outline) BorderStroke(
                borderStrokeWidth, MaterialTheme.colorScheme.outlineVariant
            ) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * A standard settings item with title, subtitle, and customizable content.
 *
 * @param title The main title text
 * @param subtitle Optional subtitle text
 * @param onClick Click handler for the item
 * @param leadingIcon Optional leading icon composable
 * @param trailingContent Optional trailing content composable (defaults to arrow icon)
 * @param showDivider Whether to show a divider below this item
 */
@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    showDivider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(settingsGroupItemPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            if (leadingIcon != null) {
                Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            trailingContent?.invoke() ?: Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(settingsGroupDividerPadding))
        }
    }
}

/**
 * A completely customizable settings item that provides only the clickable container.
 *
 * @param onClick Click handler for the item
 * @param content The custom content layout
 */
@Composable
fun CustomSettingsItem(
    onClick: () -> Unit, content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(settingsGroupItemPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/**
 * A padded list group container with rounded corners that handles item positioning automatically.
 *
 * @param modifier Modifier to be applied to the container
 * @param title Optional title displayed above the group
 * @param content The composable content to be displayed inside the group
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PaddedListGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(settingsGroupContainerPadding)) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLargeEmphasized,
                modifier = Modifier.padding(bottom = settingsGroupTitleBottomPadding)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(settingsGroupItemSpacing)
        ) {
            content()
        }
    }
}

/**
 * A padded list item with automatic corner rounding based on position.
 *
 * @param title The main title text
 * @param subtitle Optional subtitle text
 * @param icon Leading icon
 * @param onClick Click handler for the item
 * @param position The position of this item in the list (affects corner rounding)
 * @param outline Whether to show an outline border around the item
 */
@Composable
fun PaddedListItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit,
    position: PaddedListItemPosition = PaddedListItemPosition.Middle,
    outline: Boolean = true
) {
    val shape = when (position) {
        PaddedListItemPosition.First -> RoundedCornerShape(
            topStart = commonCornerRadius,
            topEnd = commonCornerRadius
        )

        PaddedListItemPosition.Last -> RoundedCornerShape(
            bottomStart = commonCornerRadius,
            bottomEnd = commonCornerRadius
        )

        PaddedListItemPosition.Single -> RoundedCornerShape(commonCornerRadius)
        PaddedListItemPosition.Middle -> RoundedCornerShape(settingsGroupSmallCornerRadius)
    }

    Surface(
        shape = shape,
        tonalElevation = settingsGroupElevation,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (outline) BorderStroke(
            borderStrokeWidth, MaterialTheme.colorScheme.outlineVariant
        ) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(settingsGroupItemPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * A customizable padded list item with automatic corner rounding.
 *
 * @param onClick Click handler for the item
 * @param position The position of this item in the list (affects corner rounding)
 * @param content The custom content layout
 */
@Composable
fun CustomPaddedListItem(
    onClick: () -> Unit,
    position: PaddedListItemPosition = PaddedListItemPosition.Middle,
    outline: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val shape = when (position) {
        PaddedListItemPosition.First -> RoundedCornerShape(
            topStart = commonCornerRadius,
            topEnd = commonCornerRadius
        )

        PaddedListItemPosition.Last -> RoundedCornerShape(
            bottomStart = commonCornerRadius,
            bottomEnd = commonCornerRadius
        )

        PaddedListItemPosition.Single -> RoundedCornerShape(commonCornerRadius)
        PaddedListItemPosition.Middle -> RoundedCornerShape(settingsGroupSmallCornerRadius)
    }

    Surface(
        shape = shape,
        tonalElevation = settingsGroupElevation,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (outline) BorderStroke(
            borderStrokeWidth, MaterialTheme.colorScheme.outlineVariant
        ) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(settingsGroupItemPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * A customizable expandable padded list item with automatic corner rounding.
 *
 * @param isExpanded Whether the item is currently expanded
 * @param onToggleExpanded Callback when the item is clicked to toggle expansion
 * @param position The position of this item in the list (affects corner rounding)
 * @param defaultContent The content to show when collapsed
 * @param expandedContent The content to show when expanded
 */
@Composable
fun CustomPaddedExpandableItem(
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    outline: Boolean = true,
    position: PaddedListItemPosition = PaddedListItemPosition.Middle,
    defaultContent: @Composable RowScope.() -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    val shape = when (position) {
        PaddedListItemPosition.First -> RoundedCornerShape(
            topStart = commonCornerRadius,
            topEnd = commonCornerRadius
        )

        PaddedListItemPosition.Last -> RoundedCornerShape(
            bottomStart = commonCornerRadius,
            bottomEnd = commonCornerRadius
        )

        PaddedListItemPosition.Single -> RoundedCornerShape(commonCornerRadius)
        PaddedListItemPosition.Middle -> RoundedCornerShape(settingsGroupSmallCornerRadius)
    }

    Surface(
        shape = shape,
        tonalElevation = settingsGroupElevation,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (outline) BorderStroke(
            borderStrokeWidth,
            MaterialTheme.colorScheme.outlineVariant
        ) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        Column {
            // Default content - always clickable
            Row(
                modifier = Modifier
                    .clickable { onToggleExpanded() }
                    .padding(settingsGroupItemPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = defaultContent
            )

            // Expanded content - only shown when expanded
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(settingsGroupExpandedPadding),
                    content = expandedContent
                )
            }
        }
    }
}

/**
 * Enum to define the position of an item in a padded list for proper corner rounding.
 */
enum class PaddedListItemPosition {
    First, Middle, Last, Single
}

data class SettingItem(
    val title: String, val subtitle: String? = null, val icon: ImageVector, val onClick: () -> Unit
)

@ComponentPreview
@Composable
fun FlexibleSettingsGroupPreview() {
    PreviewWrapper {
        LazyColumn {
            item {
                // Example 1: Using standard SettingsGroupItem
                FlexibleListGroup(
                    title = "Standard Items"
                ) {
                    ListItem(
                        title = "Setting 1",
                        subtitle = "Description",
                        onClick = { },
                        showDivider = true
                    )
                    ListItem(
                        title = "Setting 2", onClick = { })
                }
            }

            item {
                // Example 2: Custom content with any composables
                FlexibleListGroup(
                    title = "Custom Content"
                ) {
                    CustomSettingsItem(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Custom Item with Icon",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "This shows custom layout",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "Value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(settingsGroupDividerPadding))

                    // Any other composable can go here
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(settingsGroupItemPadding)
                    ) {
                        Text(
                            text = "You can put any composable content here!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                FlexibleListGroup(
                    title = "Padded Item Variations"
                ) {
                    // Example with leading icon and single position
                    PaddedListItem(
                        title = "Notifications",
                        subtitle = "App, system, and emergency",
                        icon = Icons.Default.Settings,
                        onClick = {},
                        position = PaddedListItemPosition.First
                    )

                    CustomPaddedListItem(
                        onClick = { /* Custom action */ }, position = PaddedListItemPosition.Middle
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Advanced Settings",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Developer options and diagnostics",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Example of trailing content: badge or text
                        Text(
                            text = "Beta",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Example with no subtitle and last position
                    PaddedListItem(
                        title = "Reset Settings",
                        icon = Icons.Default.Settings,
                        onClick = {},
                        position = PaddedListItemPosition.Last
                    )
                }
            }
        }
    }
}

@ComponentPreview
@Composable
fun PaddedListGroupPreview() {
    PreviewWrapper {
        LazyColumn {
            item {
                PaddedListGroup(
                    title = "Settings"
                ) {
                    PaddedListItem(
                        title = "Google",
                        subtitle = "Services and preferences",
                        icon = Icons.Default.Settings,
                        onClick = { },
                        position = PaddedListItemPosition.First
                    )
                    PaddedListItem(
                        title = "Network and Internet",
                        subtitle = "Mobile, Wi-Fi, hotspot",
                        icon = Icons.Default.Settings,
                        onClick = { },
                        position = PaddedListItemPosition.Middle
                    )
                    PaddedListItem(
                        title = "Connected devices",
                        subtitle = "Bluetooth, pairing",
                        icon = Icons.Default.Settings,
                        onClick = { },
                        position = PaddedListItemPosition.Last
                    )
                }
            }

            item {
                PaddedListGroup(
                    title = "Custom Content"
                ) {
                    CustomPaddedListItem(
                        onClick = { }, position = PaddedListItemPosition.First
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Custom Padded Item",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "This shows custom layout with padding",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "Value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    var isExpanded by remember { mutableStateOf(false) }
                    CustomPaddedExpandableItem(
                        isExpanded = isExpanded,
                        onToggleExpanded = { isExpanded = !isExpanded },
                        position = PaddedListItemPosition.Last,
                        defaultContent = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Expandable Item",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Click to expand/collapse",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        expandedContent = {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "This is the expanded content!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(settingsGroupIconSpacing))
                            Text(
                                text = "You can put any composable content here when expanded.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        })
                }
            }
        }
    }
}
