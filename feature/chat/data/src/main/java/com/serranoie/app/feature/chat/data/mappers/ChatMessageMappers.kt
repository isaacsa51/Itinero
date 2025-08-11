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
import kotlin.collections.map

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = this.id,
        groupCode = this.groupCode,
        senderId = (this.authorId.takeIf { it.isNotEmpty() } ?: this.senderId ?: "0").toLongOrNull()
            ?: 0L,
        senderName = this.authorName.takeIf { it.isNotEmpty() } ?: this.senderName ?: "Unknown",
        message = this.message.takeIf { it.isNotEmpty() } ?: this.content ?: "",
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
        authorId = "",
        authorName = "",
        senderId = this.senderId.toString(),
        senderName = this.senderName,
        message = "",
        messageType = this.messageType.value,
        timestamp = this.timestamp,
        isEdited = this.isEdited,
        replyToId = this.replyToMessageId,
        content = this.message
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