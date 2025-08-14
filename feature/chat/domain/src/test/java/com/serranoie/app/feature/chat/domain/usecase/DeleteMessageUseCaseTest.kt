package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteMessageUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var deleteMessageUseCase: DeleteMessageUseCase

    @Before
    fun setUp() {
        chatRepository = mockk()
        deleteMessageUseCase = DeleteMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke should call repository deleteMessage and return success`() = runTest {
        // Given
        val messageId = 123L
        val authToken = "test-token"
        val expectedResult = Result.success("Message deleted successfully")

        coEvery { chatRepository.deleteMessage(messageId, authToken) } returns expectedResult

        // When
        val result = deleteMessageUseCase(messageId, authToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Message deleted successfully", result.getOrNull())
        coVerify(exactly = 1) { chatRepository.deleteMessage(messageId, authToken) }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val messageId = 123L
        val authToken = "test-token"
        val exception = RuntimeException("Failed to delete message")
        val expectedResult = Result.failure<String>(exception)

        coEvery { chatRepository.deleteMessage(messageId, authToken) } returns expectedResult

        // When
        val result = deleteMessageUseCase(messageId, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.deleteMessage(messageId, authToken) }
    }

    @Test
    fun `invoke should handle unauthorized deletion`() = runTest {
        // Given
        val messageId = 456L
        val authToken = "invalid-token"
        val exception = SecurityException("Unauthorized")
        val expectedResult = Result.failure<String>(exception)

        coEvery { chatRepository.deleteMessage(messageId, authToken) } returns expectedResult

        // When
        val result = deleteMessageUseCase(messageId, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.deleteMessage(messageId, authToken) }
    }

    @Test
    fun `invoke should handle message not found scenario`() = runTest {
        // Given
        val nonExistentMessageId = 999L
        val authToken = "test-token"
        val exception = NoSuchElementException("Message not found")
        val expectedResult = Result.failure<String>(exception)

        coEvery {
            chatRepository.deleteMessage(
                nonExistentMessageId,
                authToken
            )
        } returns expectedResult

        // When
        val result = deleteMessageUseCase(nonExistentMessageId, authToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { chatRepository.deleteMessage(nonExistentMessageId, authToken) }
    }
}