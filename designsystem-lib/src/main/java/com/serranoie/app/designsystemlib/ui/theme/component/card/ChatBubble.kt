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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
 * @param replyAuthorName Optional author name of the message being replied to
 * @param replyMessage Optional message content being replied to
 * @param onLongClick Callback for when the message bubble is long-clicked
 * @param showEditedLabel Whether to show the "Edited" label
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
    replyAuthorName: String? = null,
    replyMessage: String? = null,
    onLongClick: (() -> Unit)? = null,
    showEditedLabel: Boolean = false,
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
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start, modifier = modifier
    ) {
        Surface(
            color = bubbleColor, shape = bubbleShape, modifier = Modifier.wrapContentWidth()
        ) {
            Column {
                if (replyAuthorName != null && replyMessage != null) {
                    ReplyMessageBubble(
                        replyAuthorName = replyAuthorName,
                        replyMessage = replyMessage,
                        modifier = Modifier.padding(
                            start = basePadding * 0.75f,
                            end = basePadding * 0.75f,
                            top = basePadding * 0.75f
                        )
                    )
                }

                ChatBubbleContent(
                    message = message,
                    contentColor = contentColor,
                    onMessageClick = onMessageClick,
                    onLinkClick = onLinkClick,
                    onLongClick = onLongClick
                )

                if (showEditedLabel) {
                    Box(
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = "Edited",
                            style = MaterialTheme.typography.labelSmallEmphasized,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(
                                    top = 0.dp,
                                    start = basePadding,
                                    end = basePadding,
                                    bottom = smallPadding
                                )
                        )
                    }
                }
            }
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
                style = MaterialTheme.typography.labelSmall,
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
 * @param authorId ID of the message author
 * @param showAvatar Whether to show the avatar (typically false for user messages)
 * @param showAuthor Whether to show the author name
 * @param onMessageClick Callback for when the message text is clicked
 * @param onAvatarClick Callback for when the avatar is clicked
 * @param onBubbleSwipe Callback for when a message bubble is swiped (receives full ChatMessage)
 * @param replyAuthorName Optional author name of the message being replied to
 * @param replyMessage Optional message content being replied to
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChatBubbleWithAvatar(
    message: String,
    isUserMe: Boolean,
    timestamp: String,
    authorName: String = "",
    authorId: String = "",
    messageId: String = "",
    showAvatar: Boolean = !isUserMe,
    showAuthor: Boolean = !isUserMe,
    onMessageClick: (String) -> Unit = {},
    onBubbleSwipe: (ChatMessage) -> Unit = {},
    replyAuthorName: String? = null,
    replyMessage: String? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    isDeleted: Boolean = false,
    deletedByName: String? = null,
    showEditedLabel: Boolean = false,
) {
    val swipeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val maxOffsetPx = with(LocalDensity.current) { 90.dp.toPx() }

    val actionTriggered = remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMe) {
            if (showAvatar) {
                ChatAvatar(
                    authorName = authorName, modifier = Modifier.padding(end = smallPadding)
                )
            } else {
                Spacer(modifier = Modifier.width(iconSize * 2))
            }
        }

        Column(
            horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
            modifier = if (isUserMe) {
                Modifier
            } else {
                if (replyAuthorName != null && replyMessage != null) {
                    Modifier
                } else {
                    Modifier.weight(1f, fill = false)
                }
            }
        ) {
            if (showAuthor && !isUserMe && authorName.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Left,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.animateVisibility(isVisible = showAuthor)
                    )
                    Text(
                        text = " · $timestamp",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.animateVisibility(isVisible = showAuthor)
                    )
                }
                Spacer(modifier = Modifier.height(extraSmallPadding))
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(swipeOffset.value.roundToInt(), 0) }
                    .then(if (!isUserMe) Modifier.pointerInput(message) {
                        detectHorizontalDragGestures(onDragStart = {
                            actionTriggered.value = false
                        }, onDragEnd = {
                            coroutineScope.launch {
                                swipeOffset.animateTo(0f)
                            }
                            actionTriggered.value = false
                        }) { change, dragAmount ->
                            change.consume()
                            val newOffset = (swipeOffset.value + dragAmount).coerceAtLeast(0f)

                            if (newOffset > maxOffsetPx && !actionTriggered.value) {
                                actionTriggered.value = true
                                val chatMessage = ChatMessage(
                                    id = messageId,
                                    content = message,
                                    authorId = authorId,
                                    authorName = authorName,
                                    timestamp = timestamp,
                                    rawTimestamp = timestamp,
                                    replyToMessageId = null,
                                    replyAuthorName = replyAuthorName,
                                    replyMessage = replyMessage
                                )
                                onBubbleSwipe(chatMessage)
                            }

                            val cappedOffset = newOffset.coerceAtMost(maxOffsetPx * 1.2f)

                            coroutineScope.launch {
                                swipeOffset.snapTo(cappedOffset)
                            }
                        }
                    } else Modifier)
                    .combinedClickable(
                        onClick = {
                            onMessageClick(message)
                        }, onLongClick = onLongClick
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = getChatBubbleShape(isUserMe)
                        )
                        else Modifier
                    )
            ) {
                if (isDeleted) {
                    DeletedMessageBubble(
                        deletedByName = deletedByName ?: authorName,
                        isUserMe = isUserMe,
                        timestamp = timestamp
                    )
                } else {
                    if (!isUserMe && swipeOffset.value > 18f) {
                        val iconScale = if (swipeOffset.value > maxOffsetPx) 1.2f else 1f
                        val iconAlpha = (swipeOffset.value / maxOffsetPx).coerceAtMost(1f)

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "Reply",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 4.dp, end = 6.dp)
                                .graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                    alpha = iconAlpha
                                })
                    }
                    ChatBubble(
                        message = message,
                        isUserMe = isUserMe,
                        timestamp = timestamp,
                        onMessageClick = { },
                        replyAuthorName = replyAuthorName,
                        replyMessage = replyMessage,
                        showTimestamp = false,
                        onLongClick = onLongClick,
                        showEditedLabel = showEditedLabel
                    )
                }
            }

            if (isUserMe) {
                Spacer(modifier = Modifier.height(extraSmallPadding))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = smallPadding)
                )
            }
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
 * @param onBubbleSwipe Callback for when a message bubble is swiped
 * @param selectedMessageId ID of the currently selected message, null if none selected
 * @param onMessageLongClick Callback for when a message is long-clicked
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun ChatConversation(
    messages: List<ChatMessage>,
    currentUserId: String,
    onMessageClick: (String) -> Unit = {},
    onAvatarClick: (String) -> Unit = {},
    onBubbleSwipe: (ChatMessage) -> Unit = {},
    selectedMessageId: String? = null,
    onMessageLongClick: (ChatMessage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        messages.forEachIndexed { index, message ->
            val isUserMe = message.authorId == currentUserId
            val previousMessage = messages.getOrNull(index - 1)
            val nextMessage = messages.getOrNull(index + 1)

            val showAvatar = nextMessage?.authorId != message.authorId

            val showAuthor = previousMessage?.authorId != message.authorId

            ChatBubbleWithAvatar(
                message = message.content,
                isUserMe = isUserMe,
                timestamp = message.timestamp,
                authorName = message.authorName,
                authorId = message.authorId,
                messageId = message.id,
                showAvatar = showAvatar,
                showAuthor = showAuthor,
                onMessageClick = onMessageClick,
                onBubbleSwipe = onBubbleSwipe,
                replyAuthorName = message.replyAuthorName,
                replyMessage = message.replyMessage,
                isSelected = message.id == selectedMessageId,
                onLongClick = {
                    onMessageLongClick(message)
                },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ChatAvatar(
    authorName: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier
) {
    InitialsAvatar(
        name = authorName, modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InitialsAvatar(
    name: String, modifier: Modifier = Modifier
) {
    val initials = getInitials(name)
    val backgroundColor = getConsistentColor(name)
    val textColor = getContrastColor(backgroundColor)
    val borderColor = getDarkerColor(backgroundColor)

    Box(
        modifier = modifier
            .size(iconSize * 2)
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

private fun getInitials(name: String): String {
    return name.trim().split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("").ifEmpty { "?" }
}

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

    val colorIndex = kotlin.math.abs(name.hashCode()) % colors.size
    return colors[colorIndex]
}

private fun getContrastColor(backgroundColor: Color): Color {
    val luminance =
        0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
    return if (luminance > 0.5) Color.Black else Color.White
}

private fun getDarkerColor(color: Color): Color {
    return Color(
        red = color.red * 0.8f,
        green = color.green * 0.8f,
        blue = color.blue * 0.8f,
        alpha = color.alpha
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ChatBubbleContent(
    message: String,
    contentColor: Color,
    onMessageClick: (String) -> Unit,
    onLinkClick: (String) -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val styledMessage = remember(message, primaryColor) {
        buildAnnotatedString {
            val urlPattern = Regex(
                """https?://(?:[-\w.])+(?:\:[0-9]+)?(?:/(?:[\w/_.])*(?:\?(?:[\w&=%.])*)?(?:\#(?:[\w.])*)?)?"""
            )

            var lastIndex = 0
            urlPattern.findAll(message).forEach { matchResult ->
                val url = matchResult.value
                val start = matchResult.range.first
                val end = matchResult.range.last + 1

                if (start > lastIndex) {
                    append(message.substring(lastIndex, start))
                }

                pushStringAnnotation(tag = "URL", annotation = url)
                withStyle(
                    style = SpanStyle(
                        color = primaryColor, textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(url)
                }
                pop()

                lastIndex = end
            }

            if (lastIndex < message.length) {
                append(message.substring(lastIndex))
            }
        }
    }

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
        modifier = Modifier
            .standardPadding(horizontal = basePadding, vertical = smallPadding)
            .combinedClickable(
                onClick = { /* Handled by the main onClick below */ }, onLongClick = onLongClick
            ),
        onClick = { offset ->
            styledMessage.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val url = annotation.item
                    try {
                        uriHandler.openUri(url)
                        onLinkClick(url)
                    } catch (e: Exception) {
                        onLinkClick(url)
                    }
                } ?: run {
                onMessageClick(message)
            }
        })
}

private fun getChatBubbleShape(isUserMe: Boolean): Shape = if (isUserMe) {
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
 * A reply message bubble that displays the original message being replied to
 *
 * @param replyAuthorName Name of the original message author
 * @param replyMessage Text content of the original message being replied to
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReplyMessageBubble(
    replyAuthorName: String, replyMessage: String, modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF888888).copy(alpha = 0.25f),
        shape = RoundedCornerShape(commonCornerRadius),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(smallPadding)
        ) {
            Text(
                text = replyAuthorName,
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            Text(
                text = if (replyMessage.length > 100) {
                    "${replyMessage.take(100)}..."
                } else {
                    replyMessage
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeletedMessageBubble(
    deletedByName: String,
    isUserMe: Boolean,
    timestamp: String,
    showTimestamp: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start, modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer, shape = getChatBubbleShape(isUserMe)
        ) {
            Text(
                text = "$deletedByName deleted this message",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.standardPadding(
                    horizontal = basePadding, vertical = smallPadding
                )
            )
        }
    }
}

@Composable
fun BubbleTypingIndicator(
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = getChatBubbleShape(false),
    ) {
        Box(modifier = Modifier.standardPadding(basePadding)) {
            TypingAnimation()
        }
    }
}

@Composable
fun TypingAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 8.dp,
    circleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
    spaceBetween: Dp = 5.dp,
    travelDistance: Dp = 5.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) })

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    }, repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }
    val lastCircle = circleValues.size - 1

    Row(modifier = modifier) {
        circleValues.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer { translationY = -value * distance }
                    .background(color = circleColor, shape = CircleShape))
            if (index != lastCircle) Spacer(modifier = Modifier.width(spaceBetween))
        }
    }
}

data class ChatMessage(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val timestamp: String,
    val rawTimestamp: String = timestamp,
    val replyToMessageId: String? = null,
    val replyAuthorName: String? = null,
    val replyMessage: String? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedByName: String? = null
)

@ComponentPreview
@Composable
private fun ChatBubblePreview() {
    PreviewWrapper {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(16.dp)
        ) {
            val sampleMessages = listOf(
                ChatMessage(
                    id = "1",
                    content = "Hey everyone! Ready for today's meeting?",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:00 AM"
                ), ChatMessage(
                    id = "2",
                    content = "Yes, I'll be there in 5 minutes.",
                    authorId = "me",
                    authorName = "Me",
                    timestamp = "09:01 AM"
                )
            )

            ChatConversation(
                messages = sampleMessages,
                currentUserId = "me",
                onMessageClick = { message ->
                },
                onAvatarClick = { authorId ->
                },
                onBubbleSwipe = { message ->
                },
                selectedMessageId = null,
                onMessageLongClick = { message ->
                })

            ChatBubbleWithAvatar(
                message = "Great work!",
                isUserMe = false,
                timestamp = "09:03 AM",
                authorName = "Isaac Serrano",
                authorId = "isaac",
                messageId = "4",
                replyAuthorName = "Andrea Mena",
                replyMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Soluta laoreet nisl. Nisl option aliqua invidunt volutpat ut, feugait at clita placerat consetetur commodo suscipit diam lobortis nihil velit labore soluta voluptua vero velit, gubergren placerat eos nonumy sanctus sanctus dolores qui gubergren iusto deserunt volutpat dolore. Vel tation nam invidunt stet illum cum iure. Sunt in suscipit facer takimata amet dolor takimata. Officia cillum minim mollit labore aliqua in kasd. Facilisis ullamcorper sit."
            )

            ChatBubbleWithAvatar(
                message = "Thanks Isaac!",
                isUserMe = true,
                timestamp = "09:04 AM",
                authorName = "Me",
                authorId = "me",
                messageId = "5",
                replyAuthorName = "Isaac Serrano",
                replyMessage = "Great work, Andrea!",
            )

            ChatBubbleWithAvatar(
                message = "Actually, let me update that - I think we should start with the overview first!",
                isUserMe = false,
                timestamp = "09:06 AM",
                authorName = "Andrea Mena",
                authorId = "andrea",
                messageId = "7",
                showEditedLabel = true,
                modifier = Modifier
            )

            ChatBubbleWithAvatar(
                message = "Sounds good!",
                isUserMe = true,
                timestamp = "09:07 AM",
                authorName = "Me",
                authorId = "me",
                messageId = "8",
                showEditedLabel = true,
            )

            DeletedMessageBubble(
                deletedByName = "Isaac Serrano", isUserMe = false, timestamp = "09:06 AM"
            )

            DeletedMessageBubble(
                deletedByName = "Me", isUserMe = true, timestamp = "09:07 AM"
            )

            BubbleTypingIndicator()
        }
    }
}