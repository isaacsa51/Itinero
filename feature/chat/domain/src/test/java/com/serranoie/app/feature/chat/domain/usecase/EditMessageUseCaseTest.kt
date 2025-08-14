package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EditMessageUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var editMessageUseCase: EditMessageUseCase

    @Before
    fun setUp() {
        chatRepository = mockk()
        editMessageUseCase = EditMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke should call repository updateMessage and return success`() = runTest {
        // Given
        val messageId = 123L
        val newMessage = "Updated message content"
        val authToken = "test-token"
        val expectedResult = Result.success("Message updated successfully")

        coEvery {
            chatRepository.updateMessage(
                messageId,
                newMessage,
                authToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(messageId, newMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Message updated successfully", result.getOrNull())
        coVerify(exactly = 1) { chatRepository.updateMessage(messageId, newMessage, authToken) }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val messageId = 123L
        val newMessage = "Updated message content"
        val authToken = "test-token"
        val exception = RuntimeException("Failed to update message")
        val expectedResult = Result.failure<String>(exception)

        coEvery {
            chatRepository.updateMessage(
                messageId,
                newMessage,
                authToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(messageId, newMessage, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.updateMessage(messageId, newMessage, authToken) }
    }

    @Test
    fun `invoke should handle empty message content`() = runTest {
        // Given
        val messageId = 456L
        val emptyMessage = ""
        val authToken = "test-token"
        val expectedResult = Result.success("Message updated successfully")

        coEvery {
            chatRepository.updateMessage(
                messageId,
                emptyMessage,
                authToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(messageId, emptyMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Message updated successfully", result.getOrNull())
        coVerify(exactly = 1) { chatRepository.updateMessage(messageId, emptyMessage, authToken) }
    }

    @Test
    fun `invoke should handle long message content`() = runTest {
        // Given
        val messageId = 789L
        val longMessage = "A".repeat(1000) // Very long message
        val authToken = "test-token"
        val expectedResult = Result.success("Message updated successfully")

        coEvery {
            chatRepository.updateMessage(
                messageId,
                longMessage,
                authToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(messageId, longMessage, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Message updated successfully", result.getOrNull())
        coVerify(exactly = 1) { chatRepository.updateMessage(messageId, longMessage, authToken) }
    }

    @Test
    fun `invoke should handle unauthorized edit attempt`() = runTest {
        // Given
        val messageId = 999L
        val newMessage = "Unauthorized edit attempt"
        val invalidToken = "invalid-token"
        val exception = SecurityException("Unauthorized to edit message")
        val expectedResult = Result.failure<String>(exception)

        coEvery {
            chatRepository.updateMessage(
                messageId,
                newMessage,
                invalidToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(messageId, newMessage, invalidToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.updateMessage(messageId, newMessage, invalidToken) }
    }

    @Test
    fun `invoke should handle message not found scenario`() = runTest {
        // Given
        val nonExistentMessageId = 404L
        val newMessage = "Trying to edit non-existent message"
        val authToken = "test-token"
        val exception = NoSuchElementException("Message not found")
        val expectedResult = Result.failure<String>(exception)

        coEvery {
            chatRepository.updateMessage(
                nonExistentMessageId,
                newMessage,
                authToken
            )
        } returns expectedResult

        // When
        val result = editMessageUseCase(nonExistentMessageId, newMessage, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) {
            chatRepository.updateMessage(
                nonExistentMessageId,
                newMessage,
                authToken
            )
        }
    }
}