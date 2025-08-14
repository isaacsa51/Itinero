package com.serranoie.app.feature.chat.data.mappers

import com.serranoie.app.feature.chat.data.local.entity.ChatMessageEntity
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import org.junit.Assert.*
import org.junit.Test

class ChatMessageMappersTest {

    @Test
    fun `ChatMessageDto toDomain should map correctly with authorId and authorName`() {
        // Given
        val dto = ChatMessageDto(
            id = 1L,
            groupCode = "TEST_GROUP",
            authorId = "123",
            authorName = "John Doe",
            message = "Hello World",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = null,
            senderId = null,
            senderName = null
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(1L, result.id)
        assertEquals("TEST_GROUP", result.groupCode)
        assertEquals(123L, result.senderId)
        assertEquals("John Doe", result.senderName)
        assertEquals("Hello World", result.message)
        assertEquals(MessageType.TEXT, result.messageType)
        assertEquals("2025-01-01T10:00:00", result.timestamp)
        assertEquals(false, result.isEdited)
        assertEquals(null, result.replyToMessageId)
    }

    @Test
    fun `ChatMessageDto toDomain should map correctly with senderId and senderName fallback`() {
        // Given
        val dto = ChatMessageDto(
            id = 2L,
            groupCode = "TEST_GROUP",
            authorId = "",
            authorName = "",
            message = "Hello World",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = null,
            senderId = "456",
            senderName = "Jane Doe"
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(456L, result.senderId)
        assertEquals("Jane Doe", result.senderName)
    }

    @Test
    fun `ChatMessageDto toDomain should use Unknown for empty names`() {
        // Given
        val dto = ChatMessageDto(
            id = 3L,
            groupCode = "TEST_GROUP",
            authorId = "789",
            authorName = "",
            message = "Hello World",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = null,
            senderId = "",
            senderName = ""
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(789L, result.senderId)
        assertEquals("Unknown", result.senderName)
    }

    @Test
    fun `ChatMessageDto toDomain should handle invalid senderId`() {
        // Given
        val dto = ChatMessageDto(
            id = 4L,
            groupCode = "TEST_GROUP",
            authorId = "invalid",
            authorName = "John Doe",
            message = "Hello World",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = null,
            senderId = null,
            senderName = null
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(0L, result.senderId)
        assertEquals("John Doe", result.senderName)
    }

    @Test
    fun `ChatMessageDto toDomain should handle different message types`() {
        // Given
        val imageDto = ChatMessageDto(
            id = 5L,
            groupCode = "TEST_GROUP",
            authorId = "123",
            authorName = "John Doe",
            message = "image.jpg",
            messageType = "IMAGE",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = null
        )

        val fileDto = ChatMessageDto(
            id = 6L,
            groupCode = "TEST_GROUP",
            authorId = "456",
            authorName = "Jane Doe",
            message = "document.pdf",
            messageType = "FILE",
            timestamp = "2025-01-01T10:05:00",
            isEdited = false,
            replyToId = null
        )

        // When
        val imageResult = imageDto.toDomain()
        val fileResult = fileDto.toDomain()

        // Then
        assertEquals(MessageType.IMAGE, imageResult.messageType)
        assertEquals(MessageType.FILE, fileResult.messageType)
    }

    @Test
    fun `ChatMessageDto toDomain should handle reply messages`() {
        // Given
        val dto = ChatMessageDto(
            id = 7L,
            groupCode = "TEST_GROUP",
            authorId = "123",
            authorName = "John Doe",
            message = "This is a reply",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToId = 1L
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(1L, result.replyToMessageId)
    }

    @Test
    fun `ChatMessageEntity toDomain should map correctly`() {
        // Given
        val entity = ChatMessageEntity(
            id = 8L,
            groupCode = "TEST_GROUP",
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World",
            messageType = "TEXT",
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )

        // When
        val result = entity.toDomain()

        // Then
        assertEquals(8L, result.id)
        assertEquals("TEST_GROUP", result.groupCode)
        assertEquals(123L, result.senderId)
        assertEquals("John Doe", result.senderName)
        assertEquals("Hello World", result.message)
        assertEquals(MessageType.TEXT, result.messageType)
        assertEquals("2025-01-01T10:00:00", result.timestamp)
        assertEquals(false, result.isEdited)
        assertEquals(null, result.replyToMessageId)
    }

    @Test
    fun `ChatMessage toEntity should map correctly`() {
        // Given
        val domainMessage = ChatMessage(
            id = 9L,
            groupCode = "TEST_GROUP",
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = false,
            replyToMessageId = null
        )

        // When
        val result = domainMessage.toEntity()

        // Then
        assertEquals(9L, result.id)
        assertEquals("TEST_GROUP", result.groupCode)
        assertEquals(123L, result.senderId)
        assertEquals("John Doe", result.senderName)
        assertEquals("Hello World", result.message)
        assertEquals("TEXT", result.messageType)
        assertEquals("2025-01-01T10:00:00", result.timestamp)
        assertEquals(false, result.isEdited)
        assertEquals(null, result.replyToMessageId)
    }

    @Test
    fun `ChatMessage toDto should map correctly`() {
        // Given
        val domainMessage = ChatMessage(
            id = 10L,
            groupCode = "TEST_GROUP",
            senderId = 456L,
            senderName = "Jane Doe",
            message = "Hello World",
            messageType = MessageType.IMAGE,
            timestamp = "2025-01-01T10:00:00",
            isEdited = true,
            replyToMessageId = 5L
        )

        // When
        val result = domainMessage.toDto()

        // Then
        assertEquals(10L, result.id)
        assertEquals("TEST_GROUP", result.groupCode)
        assertEquals("456", result.authorId)
        assertEquals("Jane Doe", result.authorName)
        assertEquals("Hello World", result.message)
        assertEquals("IMAGE", result.messageType)
        assertEquals("2025-01-01T10:00:00", result.timestamp)
        assertEquals(true, result.isEdited)
        assertEquals(5L, result.replyToId)
    }

    @Test
    fun `List ChatMessageDto toDomain should map all items correctly`() {
        // Given
        val dtoList = listOf(
            ChatMessageDto(
                id = 1L,
                groupCode = "TEST_GROUP",
                authorId = "123",
                authorName = "John Doe",
                message = "Message 1",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToId = null
            ),
            ChatMessageDto(
                id = 2L,
                groupCode = "TEST_GROUP",
                authorId = "456",
                authorName = "Jane Doe",
                message = "Message 2",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:05:00",
                isEdited = false,
                replyToId = null
            )
        )

        // When
        val result = dtoList.toDomain()

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(2L, result[1].id)
        assertEquals("John Doe", result[0].senderName)
        assertEquals("Jane Doe", result[1].senderName)
    }

    @Test
    fun `List ChatMessageEntity toDomainFromEntity should map all items correctly`() {
        // Given
        val entityList = listOf(
            ChatMessageEntity(
                id = 1L,
                groupCode = "TEST_GROUP",
                senderId = 123L,
                senderName = "John Doe",
                message = "Message 1",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToMessageId = null
            ),
            ChatMessageEntity(
                id = 2L,
                groupCode = "TEST_GROUP",
                senderId = 456L,
                senderName = "Jane Doe",
                message = "Message 2",
                messageType = "TEXT",
                timestamp = "2025-01-01T10:05:00",
                isEdited = false,
                replyToMessageId = null
            )
        )

        // When
        val result = entityList.toDomainFromEntity()

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(2L, result[1].id)
        assertEquals("John Doe", result[0].senderName)
        assertEquals("Jane Doe", result[1].senderName)
    }

    @Test
    fun `List ChatMessage toEntity should map all items correctly`() {
        // Given
        val domainList = listOf(
            ChatMessage(
                id = 1L,
                groupCode = "TEST_GROUP",
                senderId = 123L,
                senderName = "John Doe",
                message = "Message 1",
                messageType = MessageType.TEXT,
                timestamp = "2025-01-01T10:00:00",
                isEdited = false,
                replyToMessageId = null
            ),
            ChatMessage(
                id = 2L,
                groupCode = "TEST_GROUP",
                senderId = 456L,
                senderName = "Jane Doe",
                message = "Message 2",
                messageType = MessageType.FILE,
                timestamp = "2025-01-01T10:05:00",
                isEdited = true,
                replyToMessageId = 1L
            )
        )

        // When
        val result = domainList.toEntity()

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(2L, result[1].id)
        assertEquals("TEXT", result[0].messageType)
        assertEquals("FILE", result[1].messageType)
        assertEquals(false, result[0].isEdited)
        assertEquals(true, result[1].isEdited)
        assertEquals(null, result[0].replyToMessageId)
        assertEquals(1L, result[1].replyToMessageId)
    }
}