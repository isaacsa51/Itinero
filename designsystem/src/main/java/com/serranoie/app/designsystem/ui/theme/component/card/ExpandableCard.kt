package com.serranoie.app.designsystem.ui.theme.component.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serranoie.app.designsystem.ui.ComponentPreview
import com.serranoie.app.designsystem.ui.PreviewWrapper

/**
 * A customizable expandable card component that can show/hide content.
 *
 * @param title The title text to display in the header
 * @param isExpanded Whether the card is expanded or collapsed
 * @param onExpandedChange Callback to handle expand/collapse state changes
 * @param headerIcon Optional icon to display at the start of the header
 * @param containerColor Background color of the card
 * @param contentColor Text and icon color
 * @param cardShape Shape of the card
 * @param titleStyle Text style for the title
 * @param showDivider Whether to show a divider between header and content
 * @param useRippleEffect Whether to show a ripple effect when clicking
 * @param headerContent Optional custom header content
 * @param content The content to be shown when expanded
 */
@Composable
fun ExpandableCard(
    title: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    headerIcon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    cardShape: Shape = RoundedCornerShape(8.dp),
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
    showDivider: Boolean = false,
    headerContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = cardShape)
            .clip(cardShape)
            .background(containerColor)
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, cardShape)
                } else {
                    Modifier
                }
            )
            .clickable() {
                onExpandedChange(!isExpanded)
            }
            .padding(16.dp)
    ) {
        // Header section
        if (headerContent != null) {
            headerContent()
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Optional start icon
                if (headerIcon != null) {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = null,
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                // Title text
                Text(
                    text = title,
                    style = titleStyle,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                
                // Expand/collapse arrow icon
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    tint = contentColor,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
                )
            }
        }

        // Expandable content section
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .defaultMinSize(minHeight = 24.dp)
            ) {
                // Optional divider between header and content
                if (showDivider && isExpanded) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = contentColor.copy(alpha = 0.2f)
                    )
                }
                
                // Content
                content()
            }
        }
    }
}

@ComponentPreview
@Composable
private fun ExpandableSectionPreview() {
    PreviewWrapper{
        var isExpanded by remember { mutableStateOf(true) }

        Column(modifier = Modifier.padding(16.dp)) {
            ExpandableCard(
                title = "Trip Details",
                isExpanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                Text(
                    text = "This is the expandable content area. You can put any composable here.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            var isSecondExpanded by remember { mutableStateOf(true) }

            ExpandableCard(
                title = "Notifications",
                isExpanded = isSecondExpanded,
                onExpandedChange = { isSecondExpanded = it },
                headerIcon = Icons.Default.Notifications,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                showDivider = true
            ) {
                Text("You have 3 unread notifications")

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var isFirstExpanded by remember { mutableStateOf(true) }

            ExpandableCard(
                title = "Location",
                isExpanded = isFirstExpanded,
                onExpandedChange = { isFirstExpanded = it },
                headerIcon = Icons.Default.Place,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                cardShape = RoundedCornerShape(16.dp),
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            ) {
                Text(
                    text = "123 Main Street, Springfield",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Tap to open in maps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Example with custom header content
            ExpandableCard(
                title = "", // Not used when headerContent is provided
                isExpanded = isSecondExpanded,
                onExpandedChange = { isSecondExpanded = it },
                headerContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Advanced Settings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )

                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            ) {
                TextField(
                    value = "Configuration parameter",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}