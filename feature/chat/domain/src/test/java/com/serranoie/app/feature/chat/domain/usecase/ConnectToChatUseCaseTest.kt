package com.serranoie.app.feature.chat.domain.usecase

import app.cash.turbine.test
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConnectToChatUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var connectToChatUseCase: ConnectToChatUseCase

    @Before
    fun setUp() {
        chatRepository = mockk()
        connectToChatUseCase = ConnectToChatUseCase(chatRepository)
    }

    @Test
    fun `invoke should emit MessageReceived events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val message = ChatMessage(
            id = 1L,
            groupCode = groupCode,
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )
        val chatEvent = ChatEvent.MessageReceived(message)
        val eventFlow = flowOf(chatEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.MessageReceived)
            assertEquals(message, (receivedEvent as ChatEvent.MessageReceived).message)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should emit TypingStarted events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val userId = 456L
        val userName = "Jane Doe"
        val typingEvent = ChatEvent.TypingStarted(userId, userName)
        val eventFlow = flowOf(typingEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.TypingStarted)
            val typingStarted = receivedEvent as ChatEvent.TypingStarted
            assertEquals(userId, typingStarted.userId)
            assertEquals(userName, typingStarted.userName)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should emit TypingStopped events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val userId = 456L
        val userName = "Jane Doe"
        val typingEvent = ChatEvent.TypingStopped(userId, userName)
        val eventFlow = flowOf(typingEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.TypingStopped)
            val typingStopped = receivedEvent as ChatEvent.TypingStopped
            assertEquals(userId, typingStopped.userId)
            assertEquals(userName, typingStopped.userName)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should emit UserJoined events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val userId = 789L
        val userName = "Bob Smith"
        val userJoinedEvent = ChatEvent.UserJoined(userId, userName)
        val eventFlow = flowOf(userJoinedEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.UserJoined)
            val userJoined = receivedEvent as ChatEvent.UserJoined
            assertEquals(userId, userJoined.userId)
            assertEquals(userName, userJoined.userName)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should emit MessageDeleted events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val messageId = 123L
        val userId = 456L
        val userName = "John Doe"
        val deleteEvent = ChatEvent.MessageDeleted(messageId, userId, userName)
        val eventFlow = flowOf(deleteEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.MessageDeleted)
            val messageDeleted = receivedEvent as ChatEvent.MessageDeleted
            assertEquals(messageId, messageDeleted.messageId)
            assertEquals(userId, messageDeleted.userId)
            assertEquals(userName, messageDeleted.userName)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should emit MessageEdited events from repository`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val messageId = 123L
        val newMessage = "Edited message"
        val userId = 456L
        val userName = "John Doe"
        val editEvent = ChatEvent.MessageEdited(messageId, newMessage, userId, userName)
        val eventFlow = flowOf(editEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val receivedEvent = awaitItem()
            assertTrue(receivedEvent is ChatEvent.MessageEdited)
            val messageEdited = receivedEvent as ChatEvent.MessageEdited
            assertEquals(messageId, messageEdited.messageId)
            assertEquals(newMessage, messageEdited.newMessage)
            assertEquals(userId, messageEdited.userId)
            assertEquals(userName, messageEdited.userName)
            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should handle multiple events in sequence`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val message = ChatMessage(
            id = 1L,
            groupCode = groupCode,
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )
        val messageEvent = ChatEvent.MessageReceived(message)
        val typingEvent = ChatEvent.TypingStarted(456L, "Jane Doe")
        val eventFlow = flowOf(messageEvent, typingEvent)

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns eventFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val firstEvent = awaitItem()
            assertTrue(firstEvent is ChatEvent.MessageReceived)

            val secondEvent = awaitItem()
            assertTrue(secondEvent is ChatEvent.TypingStarted)

            awaitComplete()
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `invoke should handle flow errors`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val exception = RuntimeException("Connection failed")
        val errorFlow = flow<ChatEvent> {
            throw exception
        }

        coEvery { chatRepository.connectToChat(groupCode, authToken) } returns errorFlow

        // When & Then
        connectToChatUseCase(groupCode, authToken).test {
            val error = awaitError()
            assertEquals(exception, error)
        }

        coVerify(exactly = 1) { chatRepository.connectToChat(groupCode, authToken) }
    }

    @Test
    fun `sendTypingEvent should call repository with correct parameters for typing start`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val isTyping = true
            val expectedResult = Result.success(Unit)

            coEvery {
                chatRepository.sendTypingEvent(
                    isTyping,
                    groupCode,
                    authToken
                )
            } returns expectedResult

            // When
            val result = connectToChatUseCase.sendTypingEvent(isTyping, groupCode, authToken)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { chatRepository.sendTypingEvent(isTyping, groupCode, authToken) }
        }

    @Test
    fun `sendTypingEvent should call repository with correct parameters for typing stop`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val isTyping = false
            val expectedResult = Result.success(Unit)

            coEvery {
                chatRepository.sendTypingEvent(
                    isTyping,
                    groupCode,
                    authToken
                )
            } returns expectedResult

            // When
            val result = connectToChatUseCase.sendTypingEvent(isTyping, groupCode, authToken)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { chatRepository.sendTypingEvent(isTyping, groupCode, authToken) }
        }
}