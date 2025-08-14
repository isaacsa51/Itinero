package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetMessagesUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var getMessagesUseCase: GetMessagesUseCase

    @Before
    fun setUp() {
        chatRepository = mockk()
        getMessagesUseCase = GetMessagesUseCase(chatRepository)
    }

    @Test
    fun `invoke should call repository getMessages and return success`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val messages = listOf(
            ChatMessage(
                id = 1L,
                groupCode = groupCode,
                senderId = 123L,
                senderName = "John Doe",
                message = "Hello World",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToMessageId = null
            ),
            ChatMessage(
                id = 2L,
                groupCode = groupCode,
                senderId = 456L,
                senderName = "Jane Doe",
                message = "Hi there!",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T10:05:00",
                isEdited = false,
                replyToMessageId = null
            )
        )
        val expectedResult = Result.success(messages)

        coEvery {
            chatRepository.getMessages(
                groupCode,
                authToken,
                limit,
                offset
            )
        } returns expectedResult

        // When
        val result = getMessagesUseCase(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(messages, result.getOrNull())
        coVerify(exactly = 1) { chatRepository.getMessages(groupCode, authToken, limit, offset) }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<List<ChatMessage>>(exception)

        coEvery {
            chatRepository.getMessages(
                groupCode,
                authToken,
                limit,
                offset
            )
        } returns expectedResult

        // When
        val result = getMessagesUseCase(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.getMessages(groupCode, authToken, limit, offset) }
    }

    @Test
    fun `invoke should handle empty message list`() = runTest {
        // Given
        val groupCode = "EMPTY_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val emptyMessages = emptyList<ChatMessage>()
        val expectedResult = Result.success(emptyMessages)

        coEvery {
            chatRepository.getMessages(
                groupCode,
                authToken,
                limit,
                offset
            )
        } returns expectedResult

        // When
        val result = getMessagesUseCase(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyMessages, result.getOrNull())
        assertTrue(result.getOrNull()!!.isEmpty())
        coVerify(exactly = 1) { chatRepository.getMessages(groupCode, authToken, limit, offset) }
    }

    @Test
    fun `invoke should handle custom limit and offset`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val customLimit = 10
        val customOffset = 20
        val messages = listOf(
            ChatMessage(
                id = 21L,
                groupCode = groupCode,
                senderId = 789L,
                senderName = "Bob Smith",
                message = "Message 21",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T11:00:00",
                isEdited = false,
                replyToMessageId = null
            )
        )
        val expectedResult = Result.success(messages)

        coEvery {
            chatRepository.getMessages(
                groupCode,
                authToken,
                customLimit,
                customOffset
            )
        } returns expectedResult

        // When
        val result = getMessagesUseCase(groupCode, authToken, customLimit, customOffset)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(messages, result.getOrNull())
        coVerify(exactly = 1) {
            chatRepository.getMessages(
                groupCode,
                authToken,
                customLimit,
                customOffset
            )
        }
    }

    @Test
    fun `invoke should handle messages with replies`() = runTest {
        // Given
        val groupCode = "TEST_GROUP"
        val authToken = "test-token"
        val limit = 50
        val offset = 0
        val messages = listOf(
            ChatMessage(
                id = 1L,
                groupCode = groupCode,
                senderId = 123L,
                senderName = "John Doe",
                message = "Original message",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToMessageId = null
            ),
            ChatMessage(
                id = 2L,
                groupCode = groupCode,
                senderId = 456L,
                senderName = "Jane Doe",
                message = "Reply to original",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T10:05:00",
                isEdited = false,
                replyToMessageId = 1L
            )
        )
        val expectedResult = Result.success(messages)

        coEvery {
            chatRepository.getMessages(
                groupCode,
                authToken,
                limit,
                offset
            )
        } returns expectedResult

        // When
        val result = getMessagesUseCase(groupCode, authToken, limit, offset)

        // Then
        assertTrue(result.isSuccess)
        val resultMessages = result.getOrNull()!!
        assertEquals(2, resultMessages.size)
        assertEquals(1L, resultMessages[1].replyToMessageId)
        coVerify(exactly = 1) { chatRepository.getMessages(groupCode, authToken, limit, offset) }
    }
}