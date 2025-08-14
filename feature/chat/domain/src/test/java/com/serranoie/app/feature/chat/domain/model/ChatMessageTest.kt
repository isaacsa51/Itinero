package com.serranoie.app.feature.chat.domain.model

import org.junit.Assert.*
import org.junit.Test

class ChatMessageTest {

    @Test
    fun `ChatMessage data class should create instance correctly`() {
        // Given
        val chatMessage = ChatMessage(
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

        // Then
        assertEquals(1L, chatMessage.id)
        assertEquals("TEST_GROUP", chatMessage.groupCode)
        assertEquals(123L, chatMessage.senderId)
        assertEquals("John Doe", chatMessage.senderName)
        assertEquals("Hello World", chatMessage.message)
        assertEquals(MessageType.TEXT, chatMessage.messageType)
        assertEquals("2025-01-01T10:00:00", chatMessage.timestamp)
        assertEquals(false, chatMessage.isEdited)
        assertEquals(null, chatMessage.replyToMessageId)
    }

    @Test
    fun `ChatMessage should support reply functionality`() {
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

        // Then
        assertEquals(1L, replyMessage.replyToMessageId)
    }

    @Test
    fun `ChatMessage should support edited messages`() {
        // Given
        val editedMessage = ChatMessage(
            id = 1L,
            groupCode = "TEST_GROUP",
            senderId = 123L,
            senderName = "John Doe",
            message = "Hello World (edited)",
            messageType = MessageType.TEXT,
            timestamp = "2025-01-01T10:00:00",
            isEdited = true,
            replyToMessageId = null
        )

        // Then
        assertTrue(editedMessage.isEdited)
    }

    @Test
    fun `ChatMessage data class equality should work correctly`() {
        // Given
        val message1 = ChatMessage(
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

        val message2 = ChatMessage(
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

        // Then
        assertEquals(message1, message2)
        assertEquals(message1.hashCode(), message2.hashCode())
    }
}

class MessageTypeTest {

    @Test
    fun `MessageType fromString should return correct enum for TEXT`() {
        // When
        val result = MessageType.fromString("TEXT")

        // Then
        assertEquals(MessageType.TEXT, result)
    }

    @Test
    fun `MessageType fromString should return correct enum for IMAGE`() {
        // When
        val result = MessageType.fromString("IMAGE")

        // Then
        assertEquals(MessageType.IMAGE, result)
    }

    @Test
    fun `MessageType fromString should return correct enum for FILE`() {
        // When
        val result = MessageType.fromString("FILE")

        // Then
        assertEquals(MessageType.FILE, result)
    }

    @Test
    fun `MessageType fromString should return TEXT for unknown value`() {
        // When
        val result = MessageType.fromString("UNKNOWN")

        // Then
        assertEquals(MessageType.TEXT, result)
    }

    @Test
    fun `MessageType fromString should return TEXT for empty string`() {
        // When
        val result = MessageType.fromString("")

        // Then
        assertEquals(MessageType.TEXT, result)
    }

    @Test
    fun `MessageType should have correct string values`() {
        // Then
        assertEquals("TEXT", MessageType.TEXT.value)
        assertEquals("IMAGE", MessageType.IMAGE.value)
        assertEquals("FILE", MessageType.FILE.value)
    }
}