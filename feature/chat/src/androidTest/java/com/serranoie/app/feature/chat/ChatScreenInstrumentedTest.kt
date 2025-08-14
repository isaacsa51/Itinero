package com.serranoie.app.feature.chat

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var uiState: ChatScreenUiState
    private var backPressedCalled = false
    private var messageSentCalled = false

    @Before
    fun setUp() {
        backPressedCalled = false
        messageSentCalled = false
        uiState = ChatScreenUiState(
            channelName = "Test Channel",
            channelMembers = 5,
            initialMessages = listOf(
                ChatMessage(
                    id = "1",
                    content = "Hello World",
                    authorId = "123",
                    authorName = "John Doe",
                    timestamp = "10:00 AM",
                    rawTimestamp = "1672531200000"
                ),
                ChatMessage(
                    id = "2",
                    content = "How are you?",
                    authorId = "456",
                    authorName = "Jane Doe",
                    timestamp = "10:01 AM",
                    rawTimestamp = "1672531260000"
                )
            )
        )
    }

    @Test
    fun chatScreen_displaysChannelName() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Test Channel")
            .assertExists()

        composeTestRule
            .onNodeWithText("5 members")
            .assertExists()
    }

    @Test
    fun chatScreen_displaysMessages() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Hello World")
            .assertExists()

        composeTestRule
            .onNodeWithText("How are you?")
            .assertExists()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertExists()

        composeTestRule
            .onNodeWithText("Jane Doe")
            .assertExists()
    }

    @Test
    fun chatScreen_backButtonTriggersCallback() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()

        // Then
        assert(backPressedCalled)
    }

    @Test
    fun chatScreen_displaysTypingIndicator() {
        // Given
        val typingUsers = setOf("Alice", "Bob")

        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = typingUsers,
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Alice, Bob are typing...")
            .assertExists()
    }

    @Test
    fun chatScreen_displaysOfflineStatus() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = false,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("5 members • Offline")
            .assertExists()
    }

    @Test
    fun chatScreen_displaysLoadingState() {
        // Given
        val emptyUiState = ChatScreenUiState(
            channelName = "Test Channel",
            channelMembers = 5,
            initialMessages = emptyList()
        )

        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = emptyUiState,
                    isLoading = true,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Loading messages...")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Type a message...")
            .assertExists()
    }

    @Test
    fun chatScreen_showsShimmerWhenLoadingWithEmptyMessages() {
        // Given
        val loadingUiState = ChatScreenUiState(
            channelName = "Test Channel",
            channelMembers = 5,
            initialMessages = emptyList()
        )

        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = loadingUiState,
                    isLoading = true,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then - Should not show the old loading spinner
        composeTestRule
            .onNodeWithText("Loading messages...")
            .assertDoesNotExist()

        // Channel name should still be visible during loading
        composeTestRule
            .onNodeWithText("Test Channel")
            .assertExists()

        // Message input should still be accessible during loading
        composeTestRule
            .onNodeWithText("Type a message...")
            .assertExists()
    }

    @Test
    fun chatScreen_showsMessagesWhenNotLoading() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then - Should show actual messages when not loading
        composeTestRule
            .onNodeWithText("Hello World")
            .assertExists()

        composeTestRule
            .onNodeWithText("How are you?")
            .assertExists()

        // Should not show any loading indicators
        composeTestRule
            .onNodeWithText("Loading messages...")
            .assertDoesNotExist()
    }

    @Test
    fun chatScreen_messageInputExists() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ChatScreen(
                    groupCode = "TEST_GROUP",
                    groupName = "Test Channel",
                    memberCount = 5,
                    currentUserId = "current_user",
                    onBackPressed = { backPressedCalled = true },
                    uiState = uiState,
                    isLoading = false,
                    isConnected = true,
                    error = null,
                    typingUsers = emptySet(),
                    onInitializeChat = { _, _, _ -> },
                    onSendMessage = { messageSentCalled = true },
                    onRetryConnection = { },
                    onClearError = { },
                    onTypingStarted = { },
                    onTypingStopped = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Type a message...")
            .assertExists()
    }
}