package com.serranoie.app.feature.chat.data.repository

import app.cash.turbine.test
import com.serranoie.app.feature.chat.data.mappers.toDomain
import com.serranoie.app.feature.chat.data.remote.api.ChatApiService
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.data.remote.dto.UpdateMessageDto
import com.serranoie.app.feature.chat.data.remote.websocket.ChatWebSocketService
import com.serranoie.app.feature.chat.data.remote.websocket.WebSocketEvent
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChatRepositoryImplTest {

    private lateinit var apiService: ChatApiService
    private lateinit var webSocketService: ChatWebSocketService
    private lateinit var chatRepository: ChatRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        webSocketService = mockk()
        chatRepository = ChatRepositoryImpl(apiService, webSocketService)
    }

    @Test
    fun `getMessages should return success when API service succeeds`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val messageDtos = listOf(
            ChatMessageDto(
                id = 1L,
                groupCode = groupCode,
                authorId = "123",
                authorName = "John Doe",
                message = "Hello World",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToId = null
            )
        )

        coEvery { apiService.getMessages(groupCode, authToken, limit, offset) } returns messageDtos

        // When
        val result = chatRepository.getMessages(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isSuccess)
        val messages = result.getOrNull()!!
        assertEquals(1, messages.size)
        assertEquals(1L, messages[0].id)
        assertEquals("Hello World", messages[0].message)
        coVerify(exactly = 1) { apiService.getMessages(groupCode, authToken, limit, offset) }
    }

    @Test
    fun `getMessages should return failure when API service throws exception`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val exception = RuntimeException("Network error")

        coEvery { apiService.getMessages(groupCode, authToken, limit, offset) } throws exception

        // When
        val result = chatRepository.getMessages(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.getMessages(groupCode, authToken, limit, offset) }
    }

    @Test
    fun `updateMessage should return success when API service succeeds`() = runTest {
        // Given
        val messageId = 123L
        val newMessage = "Updated message"
        val authToken = "test-token"
        val expectedResponse = "Message updated successfully"
        val updateDto = UpdateMessageDto(newMessage = newMessage)

        coEvery {
            apiService.updateMessage(
                messageId,
                updateDto,
                authToken
            )
        } returns expectedResponse

        // When
        val result = chatRepository.updateMessage(messageId, newMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify(exactly = 1) { apiService.updateMessage(messageId, updateDto, authToken) }
    }

    @Test
    fun `updateMessage should return failure when API service throws exception`() = runTest {
        // Given
        val messageId = 123L
        val newMessage = "Updated message"
        val authToken = "test-token"
        val exception = RuntimeException("Update failed")
        val updateDto = UpdateMessageDto(newMessage = newMessage)

        coEvery { apiService.updateMessage(messageId, updateDto, authToken) } throws exception

        // When
        val result = chatRepository.updateMessage(messageId, newMessage, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.updateMessage(messageId, updateDto, authToken) }
    }

    @Test
    fun `deleteMessage should return success when API service succeeds`() = runTest {
        // Given
        val messageId = 123L
        val authToken = "test-token"
        val expectedResponse = "Message deleted successfully"

        coEvery { apiService.deleteMessage(messageId, authToken) } returns expectedResponse

        // When
        val result = chatRepository.deleteMessage(messageId, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify(exactly = 1) { apiService.deleteMessage(messageId, authToken) }
    }

    @Test
    fun `deleteMessage should return failure when API service throws exception`() = runTest {
        // Given
        val messageId = 123L
        val authToken = "test-token"
        val exception = RuntimeException("Delete failed")

        coEvery { apiService.deleteMessage(messageId, authToken) } throws exception

        // When
        val result = chatRepository.deleteMessage(messageId, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.deleteMessage(messageId, authToken) }
    }

    @Test
    fun `connectToChat should emit MessageReceived event when WebSocket receives message`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val messageDto = ChatMessageDto(
                id = 1L,
                groupCode = groupCode,
                authorId = "123",
                authorName = "John Doe",
                message = "Hello World",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToId = null
            )
            val webSocketEvent = WebSocketEvent.MessageReceived(messageDto.toDomain())
            val webSocketFlow = flowOf(webSocketEvent)

            coEvery { webSocketService.connectToChat(groupCode, authToken) } returns webSocketFlow

            // When & Then
            chatRepository.connectToChat(groupCode, authToken).test {
                val chatEvent = awaitItem()
                assertTrue(chatEvent is ChatEvent.MessageReceived)
                val receivedMessage = (chatEvent as ChatEvent.MessageReceived).message
                assertEquals(1L, receivedMessage.id)
                assertEquals("Hello World", receivedMessage.message)
                awaitComplete()
            }

            coVerify(exactly = 1) { webSocketService.connectToChat(groupCode, authToken) }
        }

    @Test
    fun `connectToChat should emit TypingStarted event when WebSocket receives typing start`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val typingIndicator = com.serranoie.app.feature.chat.data.remote.dto.TypingIndicator(
                userId = 456L,
                userName = "Jane Doe",
                isTyping = true
            )
            val webSocketEvent = WebSocketEvent.TypingStart(typingIndicator)
            val webSocketFlow = flowOf(webSocketEvent)

            coEvery { webSocketService.connectToChat(groupCode, authToken) } returns webSocketFlow

            // When & Then
            chatRepository.connectToChat(groupCode, authToken).test {
                val chatEvent = awaitItem()
                assertTrue(chatEvent is ChatEvent.TypingStarted)
                val typingStarted = chatEvent as ChatEvent.TypingStarted
                assertEquals(456L, typingStarted.userId)
                assertEquals("Jane Doe", typingStarted.userName)
                awaitComplete()
            }

            coVerify(exactly = 1) { webSocketService.connectToChat(groupCode, authToken) }
        }

    @Test
    fun `connectToChat should emit UserJoined event when WebSocket receives user joined`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val userId = 789
            val userName = "Bob Smith"
            val webSocketEvent = WebSocketEvent.UserJoined(userId, userName)
            val webSocketFlow = flowOf(webSocketEvent)

            coEvery { webSocketService.connectToChat(groupCode, authToken) } returns webSocketFlow

            // When & Then
            chatRepository.connectToChat(groupCode, authToken).test {
                val chatEvent = awaitItem()
                assertTrue(chatEvent is ChatEvent.UserJoined)
                val userJoined = chatEvent as ChatEvent.UserJoined
                assertEquals(789L, userJoined.userId)
                assertEquals("Bob Smith", userJoined.userName)
                awaitComplete()
            }

            coVerify(exactly = 1) { webSocketService.connectToChat(groupCode, authToken) }
        }

    @Test
    fun `connectToChat should emit MessageDeleted event when WebSocket receives message deletion`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val authToken = "test-token"
            val messageId = 123L
            val userId = 456
            val userName = "John Doe"
            val webSocketEvent = WebSocketEvent.MessageDeleted(messageId, userId, userName)
            val webSocketFlow = flowOf(webSocketEvent)

            coEvery { webSocketService.connectToChat(groupCode, authToken) } returns webSocketFlow

            // When & Then
            chatRepository.connectToChat(groupCode, authToken).test {
                val chatEvent = awaitItem()
                assertTrue(chatEvent is ChatEvent.MessageDeleted)
                val messageDeleted = chatEvent as ChatEvent.MessageDeleted
                assertEquals(123L, messageDeleted.messageId)
                assertEquals(456L, messageDeleted.userId)
                assertEquals("John Doe", messageDeleted.userName)
                awaitComplete()
            }

            coVerify(exactly = 1) { webSocketService.connectToChat(groupCode, authToken) }
        }

    @Test
    fun `sendMessage should return success when WebSocket service succeeds`() = runTest {
        // Given
        val message = ChatMessage(
            id = 1L,
            groupCode = "TEST_GROUP",
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )
        val authToken = "test-token"

        coEvery { webSocketService.sendMessage(message, authToken) } returns Unit

        // When
        val result = chatRepository.sendMessage(message, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { webSocketService.sendMessage(message, authToken) }
    }

    @Test
    fun `sendMessage should return failure when WebSocket service throws exception`() = runTest {
        // Given
        val message = ChatMessage(
            id = 1L,
            groupCode = "TEST_GROUP",
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )
        val authToken = "test-token"
        val exception = RuntimeException("Send failed")

        coEvery { webSocketService.sendMessage(message, authToken) } throws exception

        // When
        val result = chatRepository.sendMessage(message, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { webSocketService.sendMessage(message, authToken) }
    }

    @Test
    fun `sendTypingEvent should return success for typing start`() = runTest {
        // Given
        val isTyping = true
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"

        coEvery { webSocketService.sendTypingStart(groupCode, authToken) } returns Unit

        // When
        val result = chatRepository.sendTypingEvent(isTyping, groupCode, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { webSocketService.sendTypingStart(groupCode, authToken) }
        coVerify(exactly = 0) { webSocketService.sendTypingStop(any(), any()) }
    }

    @Test
    fun `sendTypingEvent should return success for typing stop`() = runTest {
        // Given
        val isTyping = false
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"

        coEvery { webSocketService.sendTypingStop(groupCode, authToken) } returns Unit

        // When
        val result = chatRepository.sendTypingEvent(isTyping, groupCode, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { webSocketService.sendTypingStop(groupCode, authToken) }
        coVerify(exactly = 0) { webSocketService.sendTypingStart(any(), any()) }
    }

    @Test
    fun `editMessageOverSocket should return success when WebSocket service succeeds`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val messageId = 123L
        val newMessage = "Edited message"
        val authToken = "test-token"

        coEvery {
            webSocketService.sendEditMessage(
                groupCode,
                messageId,
                newMessage,
                authToken
            )
        } returns Unit

        // When
        val result =
            chatRepository.editMessageOverSocket(groupCode, messageId, newMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            webSocketService.sendEditMessage(
                groupCode,
                messageId,
                newMessage,
                authToken
            )
        }
    }

    @Test
    fun `editMessageOverSocket should return failure when WebSocket service throws exception`() =
        runTest {
            // Given
            val groupCode = "TEST_GROUP"
            val messageId = 123L
            val newMessage = "Edited message"
            val authToken = "test-token"
            val exception = RuntimeException("Edit failed")

            coEvery {
                webSocketService.sendEditMessage(
                    groupCode,
                    messageId,
                    newMessage,
                    authToken
                )
            } throws exception

            // When
            val result =
                chatRepository.editMessageOverSocket(groupCode, messageId, newMessage, authToken)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify(exactly = 1) {
                webSocketService.sendEditMessage(
                    groupCode,
                    messageId,
                    newMessage,
                    authToken
                )
            }
        }
}