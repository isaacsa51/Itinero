package com.serranoie.app.designsystem.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.ComponentPreview
import com.serranoie.app.designsystem.ui.PreviewWrapper

/**
 * A flexible settings group container that can hold any composable content.
 *
 * @param modifier Modifier to be applied to the container
 * @param title Optional title displayed above the group
 * @param content The composable content to be displayed inside the group
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlexibleSettingsGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLargeEmphasized,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * A basic settings group container with automatic styling.
 *
 * @param modifier Modifier to be applied to the container
 * @param content The composable content to be displayed inside the group
 */
@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            content()
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
fun SettingsGroupItem(
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            if (leadingIcon != null) {
                Spacer(modifier = Modifier.width(16.dp))
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
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

/**
 * A completely customizable settings item that provides only the clickable container.
 * You have full control over the layout within this item.
 *
 * @param onClick Click handler for the item
 * @param content The custom content layout
 */
@Composable
fun CustomSettingsItem(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

// Legacy support - can be removed if not needed elsewhere
@Composable
fun ListExample(sections: List<SettingItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(sections) { index, item ->
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                sections.lastIndex -> RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                else -> RoundedCornerShape(1.dp)
            }

            Surface(
                shape = shape,
                tonalElevation = 5.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .clickable { item.onClick() }
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = item.icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                        item.subtitle?.let {
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
    }
}

data class SettingItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Preview Components
@ComponentPreview
@Composable
fun FlexibleSettingsGroupPreview() {
    PreviewWrapper {
        LazyColumn {
            item {
                // Example 1: Using standard SettingsGroupItem
                FlexibleSettingsGroup(
                    title = "Standard Items"
                ) {
                    SettingsGroupItem(
                        title = "Setting 1",
                        subtitle = "Description",
                        onClick = { },
                        showDivider = true
                    )
                    SettingsGroupItem(
                        title = "Setting 2",
                        onClick = { }
                    )
                }
            }

            item {
                // Example 2: Custom content with any composables
                FlexibleSettingsGroup(
                    title = "Custom Content"
                ) {
                    CustomSettingsItem(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
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

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Any other composable can go here
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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
        }
    }
}

@ComponentPreview
@Composable
fun SettingsGroupPreview() {
    PreviewWrapper {
        SettingsGroup {
            SettingsGroupItem(
                title = "Notifications",
                subtitle = "Manage your notification preferences",
                onClick = { },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                showDivider = true
            )

            SettingsGroupItem(
                title = "Privacy",
                subtitle = "Control your privacy settings",
                onClick = { },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@ComponentPreview
@Composable
fun SettingsPreview() {
    val settings = listOf(
        SettingItem("Google", "Services and preferences", Icons.Default.ArrowForward, {}),
        SettingItem(
            "Network and Internet",
            "Mobile, Wi-Fi, hotspot",
            Icons.Default.ArrowForward,
            {}),
        SettingItem("Connected devices", "Bluetooth, pairing", Icons.Default.ArrowForward, {}),
        SettingItem("Apps", "Assistant, recent apps, default apps", Icons.Default.ArrowForward, {})
    )
    PreviewWrapper {
        ListExample(sections = settings)
    }
}