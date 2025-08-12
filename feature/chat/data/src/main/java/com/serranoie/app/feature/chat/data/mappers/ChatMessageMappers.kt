/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessageMappers.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.mappers

import com.serranoie.app.feature.chat.data.local.entity.ChatMessageEntity
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType

private fun ChatMessageDto.resolveSenderId(): Long {
    val authorIdValue = this.authorId.takeIf { it.isNotEmpty() }
    if (authorIdValue != null) {
        val parsed = authorIdValue.toLongOrNull() ?: 0L
        return parsed
    }

    val senderIdValue = this.senderId?.takeIf { it.isNotEmpty() }
    if (senderIdValue != null) {
        val parsed = senderIdValue.toLongOrNull() ?: 0L

        return parsed
    }

    return 0L
}

private fun ChatMessageDto.resolveSenderName(): String {
    val authorNameValue = this.authorName.takeIf { it.isNotEmpty() }
    if (authorNameValue != null) return authorNameValue

    val senderNameValue = this.senderName?.takeIf { it.isNotEmpty() }
    if (senderNameValue != null) return senderNameValue

    return "Unknown"
}

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = this.id,
        groupCode = this.groupCode,
        senderId = this.resolveSenderId(),
        senderName = this.resolveSenderName(),
        message = this.message,
        messageType = MessageType.fromString(this.messageType),
        timestamp = this.timestamp,
        isEdited = this.isEdited,
        replyToMessageId = this.replyToId
    )
}

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = this.id,
        groupCode = this.groupCode,
        senderId = this.senderId,
        senderName = this.senderName,
        message = this.message,
        messageType = MessageType.fromString(this.messageType),
        timestamp = this.timestamp,
        isEdited = this.isEdited,
        replyToMessageId = this.replyToMessageId
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = this.id,
        groupCode = this.groupCode,
        senderId = this.senderId,
        senderName = this.senderName,
        message = this.message,
        messageType = this.messageType.value,
        timestamp = this.timestamp,
        isEdited = this.isEdited,
        replyToMessageId = this.replyToMessageId
    )
}

fun ChatMessage.toDto(): ChatMessageDto {
    return ChatMessageDto(
        id = this.id,
        groupCode = this.groupCode,
        authorId = this.senderId.toString(),
        authorName = this.senderName,
        message = this.message,
        messageType = this.messageType.value,
        timestamp = this.timestamp,
        isEdited = this.isEdited,
        replyToId = this.replyToMessageId
    )
}

fun List<ChatMessageDto>.toDomain(): List<ChatMessage> {
    return this.map { it.toDomain() }
}

fun List<ChatMessageEntity>.toDomainFromEntity(): List<ChatMessage> {
    return this.map { it.toDomain() }
}

fun List<ChatMessage>.toEntity(): List<ChatMessageEntity> {
    return this.map { it.toEntity() }
}

fun List<ChatMessage>.toDto(): List<ChatMessageDto> {
    return this.map { it.toDto() }
}