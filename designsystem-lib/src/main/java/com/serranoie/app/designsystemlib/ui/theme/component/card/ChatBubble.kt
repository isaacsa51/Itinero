/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatBubble.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 17 julio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.iconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.animateVisibility
import com.serranoie.app.designsystemlib.ui.utils.bounceClick
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

/**
 * A reusable chat bubble component that displays messages with proper styling
 * and optional image attachments.
 *
 * @param message The text content of the message
 * @param isUserMe Whether this message is from the current user
 * @param timestamp The timestamp to display (only shown for user messages)
 * @param imageRes Optional image resource to display with the message
 * @param onMessageClick Callback for when the message text is clicked
 * @param onLinkClick Callback for when a link in the message is clicked
 * @param backgroundColor Optional custom background color for the bubble
 * @param textColor Optional custom text color
 * @param shape Optional custom shape for the bubble
 * @param showTimestamp Whether to show the timestamp
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChatBubble(
    message: String,
    isUserMe: Boolean,
    timestamp: String,
    imageRes: Int? = null,
    onMessageClick: (String) -> Unit = {},
    onLinkClick: (String) -> Unit = {},
    backgroundColor: Color? = null,
    textColor: Color? = null,
    shape: Shape? = null,
    showTimestamp: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bubbleShape = shape ?: getChatBubbleShape(isUserMe)
    val bubbleColor = backgroundColor ?: if (isUserMe) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = textColor ?: if (isUserMe) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
        modifier = modifier
    ) {
        // Message bubble
        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            modifier = Modifier
                .bounceClick { onMessageClick(message) }
        ) {
            ChatBubbleContent(
                message = message,
                contentColor = contentColor,
                onMessageClick = onMessageClick,
                onLinkClick = onLinkClick
            )
        }

        imageRes?.let { image ->
            Spacer(modifier = Modifier.height(extraSmallPadding))
            Surface(
                color = bubbleColor,
                shape = bubbleShape,
            ) {
                Image(
                    painter = painterResource(image),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(bubbleShape),
                    contentDescription = "Image attachment"
                )
            }
        }

        if (isUserMe && showTimestamp) {
            Spacer(modifier = Modifier.height(extraSmallPadding))
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .standardPadding(horizontal = smallPadding, vertical = 0.dp)
                    .animateVisibility(isVisible = showTimestamp)
            )
        }
    }
}

/**
 * Chat bubble with avatar support for displaying messages with user avatars
 *
 * @param message The text content of the message
 * @param isUserMe Whether this message is from the current user
 * @param timestamp The timestamp to display
 * @param avatarRes Avatar resource ID for the user
 * @param authorName Name of the message author
 * @param showAvatar Whether to show the avatar (typically false for user messages)
 * @param showAuthor Whether to show the author name
 * @param onMessageClick Callback for when the message text is clicked
 * @param onAvatarClick Callback for when the avatar is clicked
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChatBubbleWithAvatar(
    message: String,
    isUserMe: Boolean,
    timestamp: String,
    authorName: String = "",
    showAvatar: Boolean = !isUserMe,
    showAuthor: Boolean = !isUserMe,
    onMessageClick: (String) -> Unit = {},
    onAvatarClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMe) {
            if (showAvatar) {
                ChatAvatar(
                    authorName = authorName,
                    onClick = { onAvatarClick(authorName) },
                    modifier = Modifier.padding(end = smallPadding)
                )
            } else {
                Spacer(modifier = Modifier.width(iconSize * 2)) // Avatar size + padding
            }
        }

        // Message content - no width constraints for user messages
        Column(
            horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
            modifier = if (isUserMe) {
                Modifier
            } else {
                // For other messages, use weight but don't fill
                Modifier.weight(1f, fill = false)
            }
        ) {
            // Author name for other users
            if (showAuthor && !isUserMe && authorName.isNotEmpty()) {
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(bottom = extraSmallPadding, start = smallPadding)
                        .animateVisibility(isVisible = showAuthor)
                )
            }

            // Chat bubble
            ChatBubble(
                message = message,
                isUserMe = isUserMe,
                timestamp = timestamp,
                onMessageClick = onMessageClick,
                showTimestamp = showAuthor || isUserMe
            )
        }
    }
}

/**
 * Chat conversation component that displays multiple messages with proper grouping
 *
 * @param messages List of chat messages to display
 * @param currentUserId ID of the current user to determine message alignment
 * @param onMessageClick Callback for when a message is clicked
 * @param onAvatarClick Callback for when an avatar is clicked
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun ChatConversation(
    messages: List<ChatMessage>,
    currentUserId: String,
    onMessageClick: (String) -> Unit = {},
    onAvatarClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        messages.forEachIndexed { index, message ->
            val isUserMe = message.authorId == currentUserId
            val previousMessage = messages.getOrNull(index - 1)
            val nextMessage = messages.getOrNull(index + 1)

            // Show avatar only for the last message in a group from the same author
            val showAvatar = nextMessage?.authorId != message.authorId

            // Show author name only for the first message in a group from the same author
            val showAuthor = previousMessage?.authorId != message.authorId

            ChatBubbleWithAvatar(
                message = message.content,
                isUserMe = isUserMe,
                timestamp = message.timestamp,
                authorName = message.authorName,
                showAvatar = showAvatar,
                showAuthor = showAuthor,
                onMessageClick = onMessageClick,
                onAvatarClick = onAvatarClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Circular avatar component for chat messages that displays user initials
 */
@Composable
fun ChatAvatar(
    authorName: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    InitialsAvatar(
        name = authorName,
        modifier = modifier
    )
}

/**
 * Avatar component that displays user initials with consistent colors
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InitialsAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val initials = getInitials(name)
    val backgroundColor = getConsistentColor(name)
    val textColor = getContrastColor(backgroundColor)
    val borderColor = getDarkerColor(backgroundColor)

    Box(
        modifier = modifier
            .size(iconSize * 2) // Using iconSize constant for consistency
            .clip(CircleShape)
            .background(backgroundColor)
            .border(width = borderStrokeWidth, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = textColor,
        )
    }
}

/**
 * Generates initials from a full name
 * Examples: "Isaac Serrano" -> "IS", "Andrea Mena" -> "AM", "John" -> "J"
 */
private fun getInitials(name: String): String {
    return name.trim()
        .split(" ")
        .take(2) // Take only first two words (first name and last name)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" } // Fallback if name is empty
}

/**
 * Generates a consistent color based on the user's name
 * Same name will always produce the same color
 */
@Composable
private fun getConsistentColor(name: String): Color {
    val colors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63), // Pink
        Color(0xFF00BCD4), // Cyan
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFF3F51B5), // Indigo
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF009688), // Teal
        Color(0xFFF44336), // Red
        Color(0xFFCDDC39), // Lime
        Color(0xFFFF6F00), // Amber
        Color(0xFF8BC34A), // Light Green
        Color(0xFF673AB7), // Deep Purple
    )

    // Use hashCode to get consistent index for the same name
    val colorIndex = kotlin.math.abs(name.hashCode()) % colors.size
    return colors[colorIndex]
}

/**
 * Determines the appropriate text color (white or black) based on background color
 */
private fun getContrastColor(backgroundColor: Color): Color {
    // Calculate luminance using the formula: 0.299*R + 0.587*G + 0.114*B
    val luminance =
        0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
    return if (luminance > 0.5) Color.Black else Color.White
}

/**
 * Generates a darker color based on the input color
 */
private fun getDarkerColor(color: Color): Color {
    return Color(
        red = color.red * 0.8f,
        green = color.green * 0.8f,
        blue = color.blue * 0.8f,
        alpha = color.alpha
    )
}

/**
 * Internal composable that handles the message content with clickable text support
 */
@Composable
private fun ChatBubbleContent(
    message: String,
    contentColor: Color,
    onMessageClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    // For now, we'll use a simple AnnotatedString
    // In a real implementation, you'd use a proper message formatter
    val styledMessage = buildAnnotatedString {
        append(message)
    }

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
        modifier = Modifier.standardPadding(
            horizontal = basePadding * 0.75f, // Slightly less than base padding
            vertical = basePadding * 0.75f
        ),
        onClick = { offset ->
            // Handle clicks on the message
            onMessageClick(message)

            // Handle link clicks (you would implement proper link detection here)
            // For now, this is a placeholder for the link handling logic
        }
    )
}

/**
 * Generates the appropriate shape for the chat bubble based on sender
 */
private fun getChatBubbleShape(isUserMe: Boolean): Shape =
    if (isUserMe) {
        RoundedCornerShape(
            topStart = commonCornerRadius * 2.5f,
            topEnd = extraSmallPadding,
            bottomStart = commonCornerRadius * 2.5f,
            bottomEnd = commonCornerRadius * 2.5f
        )
    } else {
        RoundedCornerShape(
            topStart = extraSmallPadding,
            topEnd = commonCornerRadius * 2.5f,
            bottomStart = commonCornerRadius * 2.5f,
            bottomEnd = commonCornerRadius * 2.5f
        )
    }

/**
 * Author and timestamp header for messages from other users
 */
@Composable
private fun AuthorTimestamp(
    author: String,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = author,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Compact version of the chat bubble for use in lists or previews
 */
@Composable
fun CompactChatBubble(
    message: String,
    isUserMe: Boolean,
    maxLines: Int = 2,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isUserMe) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bubbleColor,
        shape = getChatBubbleShape(isUserMe),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            maxLines = maxLines,
            modifier = Modifier.standardPadding(
                horizontal = basePadding * 0.75f,
                vertical = basePadding * 0.75f
            )
        )
    }
}

/**
 * Data class representing a chat message
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val timestamp: String,
)

@ComponentPreview
@Composable
private fun ChatBubblePreview() {
    PreviewWrapper {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Chat Conversation
            Text(
                text = "Chat Conversation",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            val sampleMessages = listOf(
                ChatMessage(
                    id = "1",
                    content = "Hey everyone! Ready for today's meeting?",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:00 AM"
                ),
                ChatMessage(
                    id = "2",
                    content = "Yes, I'll be there in 5 minutes.",
                    authorId = "me",
                    authorName = "Me",
                    timestamp = "09:01 AM"
                ),
                ChatMessage(
                    id = "3",
                    content = "Perfect! I've prepared the presentation.",
                    authorId = "andrea",
                    authorName = "Andrea Mena",
                    timestamp = "09:02 AM"
                ),
                ChatMessage(
                    id = "4",
                    content = "Great work, Andrea! Looking forward to it.",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:03 AM"
                ),
                ChatMessage(
                    id = "5",
                    content = "Thanks Isaac! Should we start with the overview?",
                    authorId = "andrea",
                    authorName = "Andrea Mena",
                    timestamp = "09:04 AM"
                ),
                ChatMessage(
                    id = "6",
                    content = "Sounds good! I'll share my screen.",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:05 AM"
                )
            )

            ChatConversation(
                messages = sampleMessages,
                currentUserId = "me"
            )

            // Custom Styled Bubbles
            Text(
                text = "Custom Styled Bubbles",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChatBubble(
                    message = "This bubble has custom error styling",
                    isUserMe = false,
                    timestamp = "10:40 AM",
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    textColor = MaterialTheme.colorScheme.onErrorContainer
                )

                ChatBubble(
                    message = "This one has a custom rounded shape",
                    isUserMe = true,
                    timestamp = "10:41 AM",
                    shape = RoundedCornerShape(16.dp)
                )

                ChatBubble(
                    message = "No timestamp shown here",
                    isUserMe = false,
                    timestamp = "10:42 AM",
                    showTimestamp = false
                )
            }
        }
    }
}