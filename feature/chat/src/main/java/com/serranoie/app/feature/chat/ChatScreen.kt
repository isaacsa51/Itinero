package com.serranoie.app.feature.chat

import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.theme.component.JumpToBottom
import com.serranoie.app.designsystemlib.ui.theme.component.MessageData
import com.serranoie.app.designsystemlib.ui.theme.component.ReplyData
import com.serranoie.app.designsystemlib.ui.theme.component.UserInput
import com.serranoie.app.designsystemlib.ui.theme.component.card.BubbleTypingIndicator
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubbleWithAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import kotlinx.coroutines.launch

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
    onSendMessage: (MessageData) -> Unit,
    onRetryConnection: () -> Unit,
    onClearError: () -> Unit,
    onTypingStarted: () -> Unit,
    onTypingStopped: () -> Unit,
    onBubbleSwipe: (ChatMessage) -> Unit = {},
    onEditMessage: (messageId: String, newText: String) -> Unit = { _, _ -> },
    onDeleteMessages: (ids: List<String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var replyData by remember { mutableStateOf<ReplyData?>(null) }

    // Selection state: store selected message IDs
    var selectedMessages by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Editing state
    var editingMessageId by remember { mutableStateOf<String?>(null) }
    var editingInitialText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupCode) {
        onInitializeChat(groupCode, groupName, memberCount)
    }

    // Handle errors
    LaunchedEffect(error) {
        error?.let {
            val result = snackbarHostState.showSnackbar(
                message = it, actionLabel = "Retry", duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetryConnection()
                onClearError()
            }
        }
    }

    Scaffold(
        topBar = {
            val clipboard = LocalClipboardManager.current
            AnimatedContent(
                targetState = selectedMessages.isNotEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)).togetherWith(
                        fadeOut(
                            animationSpec = tween(200)
                        )
                    )
                }
            ) { selectionActive ->
                if (selectionActive) {
                    val selectedCount = selectedMessages.size
                    val selectedAreAllMine = selectedMessages.all { id ->
                        uiState.messages.any { it.id == id && it.authorId == currentUserId }
                    }
                    val showCopy = !selectedAreAllMine
                    SelectionTopBar(
                        selectedCount = selectedCount,
                        showEditDelete = selectedAreAllMine,
                        showCopy = showCopy,
                        onClearSelection = { selectedMessages = emptySet() },
                        onDeleteMessages = {
                            onDeleteMessages(selectedMessages.toList())
                            selectedMessages = emptySet()
                        },
                        onEditMessage = {
                            if (selectedMessages.size == 1) {
                                val id = selectedMessages.first()
                                val msg = uiState.messages.firstOrNull { it.id == id }
                                if (msg != null) {
                                    editingMessageId = id
                                    editingInitialText = msg.content
                                    replyData =
                                        ReplyData(id, msg.authorName, msg.content, isEditing = true)
                                }
                            }
                            selectedMessages = emptySet()
                        },
                        onCopyMessages = {
                            val textToCopy = uiState.messages
                                .filter { selectedMessages.contains(it.id) }
                                .joinToString(separator = "\n") { it.content }
                            clipboard.setText(AnnotatedString(textToCopy))
                            selectedMessages = emptySet()
                        }
                    )
                } else {
                    ChannelNameBar(
                        channelName = uiState.channelName,
                        channelMembers = uiState.channelMembers,
                        isConnected = isConnected,
                        onBackPressed = onBackPressed
                    )
                }
            }
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
                typingUsers = typingUsers,
                selectedMessages = selectedMessages,
                onBubbleSwipe = { message ->
                    replyData = ReplyData(message.id, message.authorName, message.content)
                    Log.d(
                        "ChatScreen",
                        "UI: User is replying to messageId=${message.id}, author=${message.authorName}, content=${message.content}"
                    )
                    onBubbleSwipe(message)
                },
                onMessageLongPress = { message ->
                    selectedMessages = if (selectedMessages.contains(message.id)) {
                        selectedMessages - message.id
                    } else {
                        selectedMessages + message.id
                    }
                },
                onMessageClick = { message ->
                    if (selectedMessages.isNotEmpty()) {
                        selectedMessages = if (selectedMessages.contains(message.id)) {
                            selectedMessages - message.id
                        } else {
                            selectedMessages + message.id
                        }
                    }
                },
                isLoading = isLoading
            )
            UserInput(
                onMessageSent = { messageData ->
                    val editingId = editingMessageId
                    if (editingId != null) {
                        onEditMessage(editingId, messageData.message)
                        editingMessageId = null
                        editingInitialText = null
                        replyData = null
                    } else {
                        Log.d(
                            "ChatScreen",
                            "UI: Sending message with reply data - message='${messageData.message}', replyToMessageId=${messageData.replyToMessageId}"
                        )
                        onSendMessage(messageData)
                    }
                },
                onTypingStarted = onTypingStarted,
                onTypingStopped = onTypingStopped,
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                replyData = replyData,
                onCancelReply = {
                    replyData = null
                    editingMessageId = null
                    editingInitialText = null
                },
                initialText = editingInitialText,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    showEditDelete: Boolean,
    showCopy: Boolean,
    onClearSelection: () -> Unit,
    onDeleteMessages: () -> Unit,
    onEditMessage: () -> Unit,
    onCopyMessages: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMediumEmphasized
            )
        },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Clear selection")
            }
        },
        actions = {
            if (showCopy) {
                IconButton(onClick = onCopyMessages) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy message(s)")
                }
            }
            if (showEditDelete) {
                if (selectedCount == 1) {
                    IconButton(onClick = onEditMessage) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit message")
                    }
                }
                IconButton(onClick = onDeleteMessages) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete message(s)")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chat_title_bar"),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = channelName,
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        modifier = Modifier.testTag("chat_title")
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
                        },
                        modifier = Modifier.testTag("chat_member_status")
                    )
                }
            }
        }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ), modifier = modifier
    )
}

/**
 * Helper function to get user-friendly date strings for day headers
 */
private fun getDateString(rawTimestamp: String): String {
    return try {
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
            today.get(java.util.Calendar.YEAR) == messageCalendar.get(java.util.Calendar.YEAR) && today.get(
                java.util.Calendar.DAY_OF_YEAR
            ) == messageCalendar.get(java.util.Calendar.DAY_OF_YEAR) -> {
                "Today"
            }
            // Yesterday
            today.get(java.util.Calendar.YEAR) == messageCalendar.get(java.util.Calendar.YEAR) && today.get(
                java.util.Calendar.DAY_OF_YEAR
            ) - messageCalendar.get(java.util.Calendar.DAY_OF_YEAR) == 1 -> {
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
        "Today"
    }
}

private fun shouldShowDayHeader(
    currentMessage: ChatMessage, previousMessage: ChatMessage?
): Boolean {
    if (previousMessage == null) return false

    val currentDateString = getDateString(currentMessage.rawTimestamp)
    val previousDateString = getDateString(previousMessage.rawTimestamp)

    if (currentDateString == "Today") return false

    return currentDateString != previousDateString
}

@Composable
fun Messages(
    messages: List<ChatMessage>,
    currentUserId: String,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    typingUsers: Set<String> = emptySet(),
    selectedMessages: Set<String> = emptySet(),
    onBubbleSwipe: (ChatMessage) -> Unit = {},
    onMessageLongPress: (ChatMessage) -> Unit = {},
    onMessageClick: (ChatMessage) -> Unit = {},
    isLoading: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        if (isLoading) {
            ShimmerMessagesPlaceholder()
        } else {
            LazyColumn(
                reverseLayout = true,
                state = scrollState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "typing_indicator") {
                    Box(
                        modifier = Modifier
                            .height(if (typingUsers.isNotEmpty()) 72.dp else 0.dp)
                            .animateContentSize(animationSpec = tween(durationMillis = 300))
                            .testTag("chat_typing_indicator_container")
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
                                AnimatedContent(
                                    targetState = typingUsers.joinToString(", "), transitionSpec = {
                                        fadeIn(animationSpec = tween(150)).togetherWith(
                                            fadeOut(animationSpec = tween(150))
                                        )
                                    }) { typingUsersText ->
                                    Text(
                                        text = "$typingUsersText ${if (typingUsers.size == 1) "is" else "are"} typing...",
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(
                                                horizontal = smallPadding,
                                                vertical = extraSmallPadding
                                            )
                                            .testTag("chat_typing_indicator")
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
                    key = { index, message -> message.id }) { index, message ->
                    val showDayHeader = shouldShowDayHeader(
                        message, if (index > 0) reversedMessages[index - 1] else null
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
                            isSelected = selectedMessages.contains(message.id),
                            onBubbleSwipe = onBubbleSwipe,
                            onLongPress = { onMessageLongPress(message) },
                            onClick = { onMessageClick(message) }
                        )
                    }
                }
            }

            val jumpThreshold = with(LocalDensity.current) { 120.dp.toPx() }
            val jumpToBottomButtonEnabled by remember {
                derivedStateOf {
                    scrollState.firstVisibleItemIndex != 0 || scrollState.firstVisibleItemScrollOffset > jumpThreshold
                }
            }

            JumpToBottom(
                enabled = jumpToBottomButtonEnabled, onClicked = {
                    scope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }, modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@DevicePreview
@Composable
fun ShimmerMessagesPlaceholder(itemCount: Int = 11) {
    PreviewWrapper {
        ShimmerProvider(isLoading = true) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(itemCount) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (index % 3 == 0) Arrangement.End else Arrangement.Start
                    ) {
                        // Simulate different message lengths and positions
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (index % 2 == 0) 0.7f else 0.5f)
                                .height(if (index % 4 == 0) 80.dp else 56.dp)
                                .shimmerable()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: ChatMessage,
    isUserMe: Boolean,
    isSelected: Boolean = false,
    onBubbleSwipe: (ChatMessage) -> Unit = {},
    onLongPress: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    ChatBubbleWithAvatar(
        message = message.content,
        isUserMe = isUserMe,
        timestamp = message.timestamp,
        authorName = message.authorName,
        authorId = message.authorId,
        messageId = message.id,
        onMessageClick = { _ -> onClick() },
        onBubbleSwipe = onBubbleSwipe,
        replyAuthorName = message.replyAuthorName,
        replyMessage = message.replyMessage,
        isSelected = isSelected,
        onLongClick = onLongPress,
        isDeleted = message.isDeleted,
        deletedByName = message.deletedByName,
        showEditedLabel = message.isEdited,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_message_item_${message.id}")
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
    val channelName: String, val channelMembers: Int, val initialMessages: List<ChatMessage>
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
        val mockUiState = ChatScreenUiState(
            channelName = "Design Preview", channelMembers = 3, initialMessages = listOf(
                ChatMessage(
                    id = "1",
                    content = "This is a preview message",
                    authorId = "1",
                    authorName = "Preview User",
                    timestamp = "12:00 PM",
                    rawTimestamp = "1672531200000"
                ), ChatMessage(
                    id = "2",
                    content = "This should show a reply bubble",
                    authorId = "2",
                    authorName = "Another User",
                    timestamp = "11:30 PM",
                    rawTimestamp = "1672531200000",
                    replyAuthorName = "Test User",
                    replyMessage = "Test reply message"
                ), ChatMessage(
                    id = "3",
                    content = "Yet another preview message",
                    authorId = "1",
                    authorName = "Preview User",
                    timestamp = "11:00 PM",
                    rawTimestamp = "1"
                ), ChatMessage(
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
            onMessageSent = { messageData -> },
            onRetryConnection = {},
            onClearError = {},
            onTypingStarted = {},
            onTypingStopped = {},
            onBubbleSwipe = { message ->
            })
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
    onMessageSent: (MessageData) -> Unit,
    onRetryConnection: () -> Unit,
    onClearError: () -> Unit,
    onTypingStarted: () -> Unit,
    onTypingStopped: () -> Unit,
    onBubbleSwipe: (ChatMessage) -> Unit
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var replyData by remember { mutableStateOf<ReplyData?>(null) }

    var selectedMessages by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(error) {
        error?.let {
            val result = snackbarHostState.showSnackbar(
                message = it, actionLabel = "Retry", duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetryConnection()
                onClearError()
            }
        }
    }

    Scaffold(
        topBar = {
            val clipboard = LocalClipboardManager.current
            AnimatedContent(
                targetState = selectedMessages.isNotEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)).togetherWith(
                        fadeOut(
                            animationSpec = tween(200)
                        )
                    )
                }
            ) { selectionActive ->
                if (selectionActive) {
                    val selectedCount = selectedMessages.size
                    val selectedAreAllMine = selectedMessages.all { id ->
                        uiState.messages.any { it.id == id && it.authorId == currentUserId }
                    }
                    val showCopy = !selectedAreAllMine
                    SelectionTopBar(
                        selectedCount = selectedCount,
                        showEditDelete = selectedAreAllMine,
                        showCopy = showCopy,
                        onClearSelection = { selectedMessages = emptySet() },
                        onDeleteMessages = { selectedMessages = emptySet() },
                        onEditMessage = { /* preview noop */ },
                        onCopyMessages = {
                            val textToCopy = uiState.messages
                                .filter { selectedMessages.contains(it.id) }
                                .joinToString(separator = "\n") { it.content }
                            clipboard.setText(AnnotatedString(textToCopy))
                            selectedMessages = emptySet()
                        }
                    )
                } else {
                    ChannelNameBar(
                        channelName = uiState.channelName,
                        channelMembers = uiState.channelMembers,
                        isConnected = isConnected,
                        onBackPressed = onBackPressed
                    )
                }
            }
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Messages(
                    messages = uiState.messages,
                    currentUserId = currentUserId,
                    scrollState = scrollState,
                    modifier = Modifier.weight(1f),
                    typingUsers = typingUsers,
                    selectedMessages = selectedMessages,
                    onBubbleSwipe = { message ->
                        replyData =
                            ReplyData(message.id, message.authorName, message.content)
                        Log.d(
                            "ChatScreenPreview",
                            "UI: User is replying to messageId=${message.id}, author=${message.authorName}, content=${message.content}"
                        )
                        onBubbleSwipe(message)
                    },
                    onMessageLongPress = { message ->
                        selectedMessages = if (selectedMessages.contains(message.id)) {
                            selectedMessages - message.id
                        } else {
                            selectedMessages + message.id
                        }
                    },
                    onMessageClick = { message ->
                        if (selectedMessages.isNotEmpty()) {
                            selectedMessages = if (selectedMessages.contains(message.id)) {
                                selectedMessages - message.id
                            } else {
                                selectedMessages + message.id
                            }
                        }
                        // else, do normal click behavior if needed
                    },
                    isLoading = isLoading
                )
                UserInput(
                    onMessageSent = { messageData ->
                        Log.d(
                            "ChatScreenPreview",
                            "UI: Sending message with reply data - message='${messageData.message}', replyToMessageId=${messageData.replyToMessageId}"
                        )
                        onMessageSent(messageData)
                        replyData = null
                    },
                    onTypingStarted = onTypingStarted,
                    onTypingStopped = onTypingStopped,
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    replyData = replyData,
                    onCancelReply = { replyData = null },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
