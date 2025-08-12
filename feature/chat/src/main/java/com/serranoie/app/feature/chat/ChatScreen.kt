package com.serranoie.app.feature.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.theme.component.JumpToBottom
import com.serranoie.app.designsystemlib.ui.theme.component.UserInput
import com.serranoie.app.designsystemlib.ui.theme.component.card.BubbleTypingIndicator
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubbleWithAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import kotlinx.coroutines.launch

/**
 * Main chat screen with integrated lambda functions
 * Handles real-time messaging, loading states, and error management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    groupCode: String,
    groupName: String,
    memberCount: Int,
    currentUserId: String,
    onBackPressed: () -> Unit,
    uiState: ChatScreenUiState,
    isLoading: Boolean,
    isConnected: Boolean,
    error: String?,
    typingUsers: Set<String>,
    onInitializeChat: (String, String, Int) -> Unit,
    onSendMessage: (String) -> Unit,
    onRetryConnection: () -> Unit,
    onClearError: () -> Unit,
    onTypingStarted: () -> Unit,
    onTypingStopped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize chat when the screen loads
    LaunchedEffect(groupCode) {
        onInitializeChat(groupCode, groupName, memberCount)
    }

    // Handle errors
    LaunchedEffect(error) {
        error?.let {
            val result = snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Retry",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetryConnection()
                onClearError()
            }
        }
    }

    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                isConnected = isConnected,
                onBackPressed = onBackPressed
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = TopAppBarDefaults.windowInsets,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            Messages(
                messages = uiState.messages,
                currentUserId = currentUserId,
                scrollState = scrollState,
                modifier = Modifier.weight(1f),
                typingUsers = typingUsers
            )
            UserInput(
                onMessageSent = onSendMessage,
                onTypingStarted = onTypingStarted,
                onTypingStopped = onTypingStopped,
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    isConnected: Boolean = true,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = { }
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = channelName,
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                    Text(
                        text = when {
                            !isConnected -> "$channelMembers members • Offline"
                            else -> "$channelMembers members"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isConnected) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

/**
 * Helper function to get user-friendly date strings for day headers
 */
private fun getDateString(rawTimestamp: String): String {
    return try {
        // First try to parse as Long (milliseconds)
        val timeValue = rawTimestamp.toLongOrNull()
        val messageDate: java.util.Date = if (timeValue != null) {
            java.util.Date(timeValue)
        } else {
            // Try to parse as ISO 8601 format (2025-08-08T11:53:55.6130846)
            try {
                val isoFormatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val localDateTime = java.time.LocalDateTime.parse(rawTimestamp, isoFormatter)
                val instant = localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
                java.util.Date.from(instant)
            } catch (e: Exception) {
                // Fallback - use today's date
                java.util.Date()
            }
        }

        // Get today's date for comparison
        val today = java.util.Calendar.getInstance()
        val messageCalendar = java.util.Calendar.getInstance().apply { time = messageDate }

        when {
            // Same day
            today.get(java.util.Calendar.YEAR) == messageCalendar.get(java.util.Calendar.YEAR) &&
                    today.get(java.util.Calendar.DAY_OF_YEAR) == messageCalendar.get(java.util.Calendar.DAY_OF_YEAR) -> {
                "Today"
            }
            // Yesterday
            today.get(java.util.Calendar.YEAR) == messageCalendar.get(java.util.Calendar.YEAR) &&
                    today.get(java.util.Calendar.DAY_OF_YEAR) - messageCalendar.get(java.util.Calendar.DAY_OF_YEAR) == 1 -> {
                "Yesterday"
            }
            // This year but older
            today.get(java.util.Calendar.YEAR) == messageCalendar.get(java.util.Calendar.YEAR) -> {
                java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
                    .format(messageDate)
            }
            // Different year
            else -> {
                java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(messageDate)
            }
        }
    } catch (e: Exception) {
        // Default fallback
        "Today"
    }
}

/**
 * Helper function to determine if a day header should be shown
 */
private fun shouldShowDayHeader(
    currentMessage: ChatMessage,
    previousMessage: ChatMessage?
): Boolean {
    if (previousMessage == null) return false // Don't show header for the first message

    val currentDateString = getDateString(currentMessage.rawTimestamp)
    val previousDateString = getDateString(previousMessage.rawTimestamp)

    // Don't show header if current date is "Today"
    if (currentDateString == "Today") return false

    // Show header if dates are different
    return currentDateString != previousDateString
}

@Composable
fun Messages(
    messages: List<ChatMessage>,
    currentUserId: String,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    typingUsers: Set<String> = emptySet()
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Show typing indicator at the bottom (first in reversed list)
            item(key = "typing_indicator") {
                // Fixed height container to prevent layout shifts
                Box(
                    modifier = Modifier
                        .height(if (typingUsers.isNotEmpty()) 72.dp else 0.dp)
                        .animateContentSize(animationSpec = tween(durationMillis = 300))
                ) {
                    AnimatedVisibility(
                        visible = typingUsers.isNotEmpty(),
                        enter = fadeIn(animationSpec = tween(200)) + slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(250)
                        ),
                        exit = fadeOut(animationSpec = tween(100)) + slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(150)
                        )
                    ) {
                        Column {
                            // Animated typing users text
                            AnimatedContent(
                                targetState = typingUsers.joinToString(", "),
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(150)).togetherWith(
                                        fadeOut(animationSpec = tween(150))
                                    )
                                }
                            ) { typingUsersText ->
                                Text(
                                    text = "$typingUsersText ${if (typingUsers.size == 1) "is" else "are"} typing...",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(
                                        horizontal = smallPadding,
                                        vertical = extraSmallPadding
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                BubbleTypingIndicator()
                            }
                        }
                    }
                }
            }

            val reversedMessages = messages.reversed()
            itemsIndexed(
                items = reversedMessages,
                key = { index, message -> message.id }
            ) { index, message ->
                val showDayHeader = shouldShowDayHeader(
                    message,
                    if (index > 0) reversedMessages[index - 1] else null
                )

                if (showDayHeader) {
                    DayHeader(getDateString(message.rawTimestamp))
                }
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeOut(animationSpec = tween(durationMillis = 200)),
                    modifier = Modifier.animateItem()
                ) {
                    MessageItem(
                        message = message,
                        isUserMe = message.authorId == currentUserId,
                    )
                }
            }
        }

        val jumpThreshold = with(LocalDensity.current) { 120.dp.toPx() }
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MessageItem(
    message: ChatMessage,
    isUserMe: Boolean,
) {
    ChatBubbleWithAvatar(
        message = message.content,
        isUserMe = isUserMe,
        timestamp = message.timestamp,
        authorName = message.authorName,
        onMessageClick = { /* Handle message click */ },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier.weight(1f),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

data class ChatScreenUiState(
    val channelName: String,
    val channelMembers: Int,
    val initialMessages: List<ChatMessage>
) {
    val messages: List<ChatMessage> = initialMessages
}

@Preview(showBackground = true)
@Composable
fun DayHeaderPrev() {
    ItineroTheme {
        DayHeader("20 Aug 2023")
    }
}

@DevicePreview
@Composable
private fun ChatScreenPreview() {
    PreviewWrapper {
        // Preview with mock data for design system testing
        val mockUiState = ChatScreenUiState(
            channelName = "Design Preview",
            channelMembers = 3,
            initialMessages = listOf(
                ChatMessage(
                    id = "1",
                    content = "This is a preview message",
                    authorId = "1",
                    authorName = "Preview User",
                    timestamp = "12:00 PM",
                    rawTimestamp = "1672531200000"
                ),
                ChatMessage(
                    id = "2",
                    content = "Another preview message",
                    authorId = "2",
                    authorName = "Another User",
                    timestamp = "11:30 PM",
                    rawTimestamp = "1672531200000",
                ),
                ChatMessage(
                    id = "3",
                    content = "Yet another preview message",
                    authorId = "1",
                    authorName = "Preview User",
                    timestamp = "11:00 PM",
                    rawTimestamp = "1"
                ),
                ChatMessage(
                    id = "4",
                    content = "Last preview message",
                    authorId = "3",
                    authorName = "User Admin",
                    timestamp = "10:30 PM",
                    rawTimestamp = "16725"
                )
            )
        )

        ChatScreenContent(
            uiState = mockUiState,
            currentUserId = "1",
            isLoading = false,
            isConnected = true,
            error = null,
            typingUsers = setOf("Test User", "Another User"),
            onBackPressed = {},
            onMessageSent = {},
            onRetryConnection = {},
            onClearError = {},
            onTypingStarted = {},
            onTypingStopped = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreenContent(
    uiState: ChatScreenUiState,
    currentUserId: String,
    isLoading: Boolean,
    isConnected: Boolean,
    error: String?,
    typingUsers: Set<String>,
    onBackPressed: () -> Unit,
    onMessageSent: (String) -> Unit,
    onRetryConnection: () -> Unit,
    onClearError: () -> Unit,
    onTypingStarted: () -> Unit,
    onTypingStopped: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle errors
    LaunchedEffect(error) {
        error?.let {
            val result = snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Retry",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetryConnection()
                onClearError()
            }
        }
    }

    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                isConnected = isConnected,
                onBackPressed = onBackPressed
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = TopAppBarDefaults.windowInsets,
        modifier = Modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && uiState.messages.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading messages...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                else -> {
                    // Chat content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Messages(
                            messages = uiState.messages,
                            currentUserId = currentUserId,
                            scrollState = scrollState,
                            modifier = Modifier.weight(1f),
                            typingUsers = typingUsers
                        )
                        UserInput(
                            onMessageSent = onMessageSent,
                            onTypingStarted = onTypingStarted,
                            onTypingStopped = onTypingStopped,
                            resetScroll = {
                                scope.launch {
                                    scrollState.scrollToItem(0)
                                }
                            },
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
