package com.serranoie.app.feature.chat.data.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serranoie.app.feature.chat.data.local.entity.ChatMessageEntity
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatMessageMappersInstrumentedTest {

    @Test
    fun testDtoToDomainMapping() {
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
            replyToId = null
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
    fun testEntityToDomainMapping() {
        // Given
        val entity = ChatMessageEntity(
            id = 1L,
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
    fun testDomainToEntityMapping() {
        // Given
        val domain = ChatMessage(
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

        // When
        val result = domain.toEntity()

        // Then
        assertEquals(1L, result.id)
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
    fun testListMappingOperations() {
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
                messageType = "IMAGE",
                timestamp = "2025-01-01T10:05:00",
                isEdited = true,
                replyToId = 1L
            )
        )

        // When
        val domainList = dtoList.toDomain()
        val entityList = domainList.toEntity()
        val backToDomainList = entityList.toDomainFromEntity()

        // Then
        assertEquals(2, domainList.size)
        assertEquals(2, entityList.size)
        assertEquals(2, backToDomainList.size)

        // Verify first message
        assertEquals(1L, domainList[0].id)
        assertEquals(MessageType.TEXT, domainList[0].messageType)

        // Verify second message
        assertEquals(2L, domainList[1].id)
        assertEquals(MessageType.IMAGE, domainList[1].messageType)
        assertEquals(true, domainList[1].isEdited)
        assertEquals(1L, domainList[1].replyToMessageId)

        // Verify round-trip consistency
        assertEquals(domainList[0].message, backToDomainList[0].message)
        assertEquals(domainList[1].message, backToDomainList[1].message)
    }
}