package com.serranoie.app.feature.chat

import app.cash.turbine.test
import com.serranoie.app.designsystemlib.ui.theme.component.MessageData
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import com.serranoie.app.feature.chat.domain.usecase.ConnectToChatUseCase
import com.serranoie.app.feature.chat.domain.usecase.DeleteMessageUseCase
import com.serranoie.app.feature.chat.domain.usecase.EditMessageOverSocketUseCase
import com.serranoie.app.feature.chat.domain.usecase.EditMessageUseCase
import com.serranoie.app.feature.chat.domain.usecase.GetMessagesUseCase
import com.serranoie.app.feature.chat.domain.usecase.SendMessageUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private lateinit var getMessagesUseCase: GetMessagesUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var connectToChatUseCase: ConnectToChatUseCase
    private lateinit var deleteMessageUseCase: DeleteMessageUseCase
    private lateinit var editMessageUseCase: EditMessageUseCase
    private lateinit var editMessageOverSocketUseCase: EditMessageOverSocketUseCase
    private lateinit var getCurrentUserId: () -> String
    private lateinit var getCurrentUserName: () -> String
    private lateinit var getAuthToken: suspend () -> String
    private lateinit var viewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getMessagesUseCase = mockk()
        sendMessageUseCase = mockk()
        connectToChatUseCase = mockk(relaxed = true)
        deleteMessageUseCase = mockk()
        editMessageUseCase = mockk()
        editMessageOverSocketUseCase = mockk()
        getCurrentUserId = mockk()
        getCurrentUserName = mockk()
        getAuthToken = mockk()

        every { getCurrentUserId() } returns "123"
        every { getCurrentUserName() } returns "Test User"
        coEvery { getAuthToken() } returns "test-token"

        every { connectToChatUseCase.disconnect() } just Runs

        viewModel = ChatViewModel(
            getMessagesUseCase,
            sendMessageUseCase,
            connectToChatUseCase,
            deleteMessageUseCase,
            editMessageUseCase,
            editMessageOverSocketUseCase,
            getCurrentUserId,
            getCurrentUserName,
            getAuthToken
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeChat should update UI state with correct values`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val groupName = "Test Group"
        val memberCount = 5
        val messages = listOf(
            ChatMessage(
                id = 1L,
                groupCode = groupCode,
                senderId = 456L,
                senderName = "John Doe",
                message = "Hello World",
                messageType = MessageType.TEXT,
                timestamp = "1672531200000",
                isEdited = false,
                replyToMessageId = null
            )
        )

        coEvery { getMessagesUseCase(groupCode, "test-token", 50, 0) } returns Result.success(
            messages
        )
        coEvery { connectToChatUseCase(groupCode, "test-token") } returns flowOf()

        // When
        viewModel.initializeChat(groupCode, groupName, memberCount)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val uiState = awaitItem()
            assertEquals(groupName, uiState.channelName)
            assertEquals(memberCount, uiState.channelMembers)
            assertEquals(1, uiState.initialMessages.size)
            assertEquals("Hello World", uiState.initialMessages[0].content)
            coVerify(exactly = 1) { getMessagesUseCase(groupCode, "test-token", 50, 0) }
        }
    }

    @Test
    fun `loadMessages should handle success and update UI state`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val messages = listOf(
            ChatMessage(
                id = 1L,
                groupCode = groupCode,
                senderId = 456L,
                senderName = "John Doe",
                message = "Hello",
                messageType = MessageType.TEXT,
                timestamp = "1672531200000",
                isEdited = false,
                replyToMessageId = null
            )
        )

        coEvery { getMessagesUseCase(groupCode, "test-token", 50, 0) } returns Result.success(
            messages
        )
        coEvery { connectToChatUseCase(groupCode, "test-token") } returns flowOf()

        // When
        viewModel.initializeChat(groupCode, "Test Group", 3)
        advanceUntilIdle()

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }

        viewModel.error.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `sendMessage should call use case with correct parameters`() = runTest {
        // Given
        val messageData = MessageData(
            message = "Hello World",
            replyToMessageId = null
        )

        coEvery { sendMessageUseCase(any(), "test-token") } returns Result.success(Unit)

        // When
        viewModel.sendMessage(messageData)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { sendMessageUseCase(any(), "test-token") }
    }

    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        val exception = RuntimeException("Test error")
        coEvery { getMessagesUseCase(any(), any(), any(), any()) } returns Result.failure(exception)
        coEvery { connectToChatUseCase(any(), any()) } returns flowOf()

        viewModel.initializeChat("TEST_GROUP", "Test Group", 3)
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.error.test {
            assertNull(awaitItem())
        }
    }
}