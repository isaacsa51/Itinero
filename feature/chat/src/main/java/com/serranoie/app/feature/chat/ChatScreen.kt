package com.serranoie.app.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.theme.component.JumpToBottom
import com.serranoie.app.designsystemlib.ui.theme.component.UserInput
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubbleWithAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import kotlinx.coroutines.launch

// Mock chat messages with realistic names for proper initials
val exampleUiState = ChatUiState(
    channelName = "Itinero name group here",
    channelMembers = 5,
    initialMessages = listOf(
        ChatMessage(
            id = "1",
            content = "Hey everyone! Welcome to the Itinero project discussion.",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:07 PM"
        ),
        ChatMessage(
            id = "2",
            content = "Thanks Isaac! Excited to be part of this project.",
            authorId = "andrea",
            authorName = "Andrea Mena",
            timestamp = "8:08 PM"
        ),
        ChatMessage(
            id = "3",
            content = "The design system looks amazing! Great work on the components.",
            authorId = "carlos",
            authorName = "Carlos Rodriguez",
            timestamp = "8:09 PM"
        ),
        ChatMessage(
            id = "4",
            content = "Thank you! We've put a lot of effort into making it comprehensive.",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:10 PM"
        ),
        ChatMessage(
            id = "5",
            content = "I particularly love the chat bubble components with the initials avatars.",
            authorId = "maria",
            authorName = "Maria Garcia",
            timestamp = "8:11 PM"
        ),
        ChatMessage(
            id = "6",
            content = "Yes! The consistent colors for each user are a nice touch.",
            authorId = "david",
            authorName = "David Thompson",
            timestamp = "8:12 PM"
        ),
        ChatMessage(
            id = "7",
            content = "Should we discuss the implementation details in our next meeting?",
            authorId = "andrea",
            authorName = "Andrea Mena",
            timestamp = "8:13 PM"
        ),
        ChatMessage(
            id = "8",
            content = "Absolutely! I'll prepare the technical documentation.",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:14 PM"
        ),
        ChatMessage(
            id = "9",
            content = "Perfect! Looking forward to it. The typography system is also well done.",
            authorId = "carlos",
            authorName = "Carlos Rodriguez",
            timestamp = "8:15 PM"
        ),
        ChatMessage(
            id = "10",
            content = "Can't wait to see this in production! ",
            authorId = "maria",
            authorName = "Maria Garcia",
            timestamp = "8:16 PM"
        )
    )
)

/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ChatUiState] that contains messages to display
 * @param currentUserId ID of the current user to determine message ownership
 * @param onBackPressed Called when the user taps the back button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState = exampleUiState,
    currentUserId: String = "isaac", // Default for previews
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                onBackPressed = onBackPressed
            )
        },
        contentWindowInsets = TopAppBarDefaults.windowInsets,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Messages(
                messages = uiState.messages,
                currentUserId = currentUserId,
                navigateToProfile = { user ->
                    // Handle navigation to profile
                },
                scrollState = scrollState,
                modifier = Modifier.weight(1f)
            )
            UserInput(
                onMessageSent = { content ->
                    // Handle message sending
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                modifier = Modifier.imePadding()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = { }
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMediumEmphasized
                )
                Text(
                    text = "$channelMembers members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
        actions = {
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

@Composable
fun Messages(
    messages: List<ChatMessage>,
    currentUserId: String,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
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
            items(messages.reversed()) { message ->
                MessageItem(
                    message = message,
                    isUserMe = message.authorId == currentUserId,
                    onAuthorClick = navigateToProfile
                )
            }
        }

        // Jump to bottom
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
    onAuthorClick: (String) -> Unit
) {
    ChatBubbleWithAvatar(
        message = message.content,
        isUserMe = isUserMe,
        timestamp = message.timestamp,
        authorName = message.authorName,
        onMessageClick = { /* Handle message click */ },
        onAvatarClick = { authorName -> onAuthorClick(authorName) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
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

data class ChatUiState(
    val channelName: String,
    val channelMembers: Int,
    val initialMessages: List<ChatMessage>
) {
    val messages: List<ChatMessage> = initialMessages.sortedBy { it.timestamp }
}

@Preview(showBackground = true)
@Composable
fun DayHeaderPrev() {
    ItineroTheme {
        DayHeader("20 Aug 2023")
    }
}

@ThemePreviews
@Composable
private fun ChatScreenPreview() {
    PreviewWrapper {
        ChatScreen()
    }
}
