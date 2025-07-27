package com.serranoie.app.designsystemlib.ui.theme.component.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.basicElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.iconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.bounceClick
import com.serranoie.app.designsystemlib.ui.utils.designBorder
import com.serranoie.app.designsystemlib.ui.utils.elevationShadow
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

/**
 * A customizable expandable card component that can show/hide content.
 *
 * @param title The title text to display in the header
 * @param isExpanded Whether the card is expanded or collapsed
 * @param onExpandedChange Callback to handle expand/collapse state changes
 * @param modifier Modifier to apply to the card
 * @param headerIcon Optional icon to display at the start of the header
 * @param containerColor Background color of the card
 * @param contentColor Text and icon color
 * @param cardShape Shape of the card
 * @param borderStroke Border stroke of the card
 * @param tonalElevation Tonal elevation for the card's surface
 * @param titleStyle Text style for the title
 * @param showDivider Whether to show a divider between header and content
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
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    cardShape: Shape = RoundedCornerShape(commonCornerRadius),
    borderStroke: BorderStroke? = BorderStroke(
        borderStrokeWidth,
        MaterialTheme.colorScheme.outlineVariant
    ),
    tonalElevation: Dp = basicElevation,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
    showDivider: Boolean = false,
    headerContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "expandArrowRotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .then(
                if (borderStroke != null) {
                    Modifier.designBorder(
                        width = borderStroke.width,
                        color = borderStroke.brush as? Color
                            ?: MaterialTheme.colorScheme.outlineVariant
                    )
                } else {
                    Modifier
                }
            )
            .bounceClick { onExpandedChange(!isExpanded) }
            .elevationShadow(elevation = tonalElevation),
        color = containerColor,
        tonalElevation = tonalElevation
    ) {
        Column(modifier = Modifier.standardPadding()) {
            if (headerContent != null) {
                headerContent()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (headerIcon != null) {
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(iconSize)
                        )
                        Spacer(modifier = Modifier.width(basePadding))
                    }

                    Text(
                        text = title,
                        style = titleStyle,
                        color = contentColor,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        tint = contentColor,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(iconSize)
                            .graphicsLayer(rotationZ = rotationAngle)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = smallPadding * 1.5f)
                        .defaultMinSize(minHeight = iconSize)
                ) {
                    if (showDivider && isExpanded) {
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.padding(bottom = smallPadding * 1.5f),
                            color = contentColor.copy(alpha = 0.2f)
                        )
                    }

                    content()
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun ExpandableSectionPreview() {
    PreviewWrapper {
        var isExpanded by remember { mutableStateOf(true) }

        Column(modifier = Modifier.standardPadding()) {
            ExpandableCard(
                title = "Trip Details",
                isExpanded = isExpanded,
                onExpandedChange = { isExpanded = it }) {
                Text(
                    text = "This is the expandable content area. You can put any composable here.",
                    modifier = Modifier.padding(top = smallPadding)
                )
            }

            Spacer(modifier = Modifier.height(basePadding))

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
                        .padding(top = smallPadding)
                ) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(basePadding))

            var isThirdExpanded by remember { mutableStateOf(true) }

            ExpandableCard(
                title = "Location",
                isExpanded = isThirdExpanded,
                onExpandedChange = { isThirdExpanded = it },
                headerIcon = Icons.Default.Place,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                cardShape = RoundedCornerShape(commonCornerRadius * 2),
                tonalElevation = basicElevation,
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold, 
                    letterSpacing = 0.5.sp
                )
            ) {
                Text(
                    text = "123 Main Street, Springfield",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = smallPadding)
                )

                Text(
                    text = "Tap to open in maps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(basePadding))

            var isFourthExpanded by remember { mutableStateOf(false) }

            ExpandableCard(
                title = "", // Not used when headerContent is provided
                isExpanded = isFourthExpanded,
                onExpandedChange = { isFourthExpanded = it },
                headerContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(iconSize)
                        )

                        Text(
                            text = "Advanced Settings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .weight(1f)
                                .standardPadding(horizontal = smallPadding * 1.5f, vertical = 0.dp)
                        )

                        CircularProgressIndicator(
                            modifier = Modifier.size(basePadding), 
                            strokeWidth = 2.dp
                        )
                    }
                }) {
                TextField(
                    value = "Configuration parameter",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { }, modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }

                    Spacer(modifier = Modifier.width(smallPadding))

                    Button(
                        onClick = { }, modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}
