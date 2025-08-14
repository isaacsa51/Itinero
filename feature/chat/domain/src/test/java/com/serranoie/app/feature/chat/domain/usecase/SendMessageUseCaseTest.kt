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

class SendMessageUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var sendMessageUseCase: SendMessageUseCase

    @Before
    fun setUp() {
        chatRepository = mockk()
        sendMessageUseCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke should call repository sendMessage and return success`() = runTest {
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
        val expectedResult = Result.success(Unit)

        coEvery { chatRepository.sendMessage(message, authToken) } returns expectedResult

        // When
        val result = sendMessageUseCase(message, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { chatRepository.sendMessage(message, authToken) }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
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
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<Unit>(exception)

        coEvery { chatRepository.sendMessage(message, authToken) } returns expectedResult

        // When
        val result = sendMessageUseCase(message, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.sendMessage(message, authToken) }
    }

    @Test
    fun `invoke should handle message with reply correctly`() = runTest {
        // Given
        val replyMessage = ChatMessage(
            id = 2L,
            groupCode = "TEST_GROUP",
            senderId = 456L,
            senderName = "Jane Doe",
            message = "This is a reply",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:05:00",
            isEdited = false,
            replyToMessageId = 1L
        )
        val authToken = "test-token"
        val expectedResult = Result.success(Unit)

        coEvery { chatRepository.sendMessage(replyMessage, authToken) } returns expectedResult

        // When
        val result = sendMessageUseCase(replyMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { chatRepository.sendMessage(replyMessage, authToken) }
    }

    @Test
    fun `invoke should handle different message types`() = runTest {
        // Given
        val imageMessage = ChatMessage(
            id = 3L,
            groupCode = "TEST_GROUP",
            senderId = 789L,
            senderName = "Bob Smith",
            message = "image.jpg",
            messageType = MessageType.IMAGE,
            timestamp = "2025-01-01T10:10:00",
            isEdited = false,
            replyToMessageId = null
        )
        val authToken = "test-token"
        val expectedResult = Result.success(Unit)

        coEvery { chatRepository.sendMessage(imageMessage, authToken) } returns expectedResult

        // When
        val result = sendMessageUseCase(imageMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { chatRepository.sendMessage(imageMessage, authToken) }
    }
}