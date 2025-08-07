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

/**
 * Extension function to convert ChatMessageDto to ChatMessage domain model
 */
fun ChatMessageDto.toDomain(): ChatMessage {
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

/**
 * Extension function to convert ChatMessageEntity to ChatMessage domain model
 */
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

/**
 * Extension function to convert ChatMessage domain model to ChatMessageEntity
 */
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

/**
 * Extension function to convert ChatMessage domain model to ChatMessageDto
 */
fun ChatMessage.toDto(): ChatMessageDto {
    return ChatMessageDto(
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

/**
 * Extension function to convert list of ChatMessageDto to list of ChatMessage
 */
fun List<ChatMessageDto>.toDomain(): List<ChatMessage> {
    return this.map { it.toDomain() }
}

/**
 * Extension function to convert list of ChatMessageEntity to list of ChatMessage
 */
fun List<ChatMessageEntity>.toDomainFromEntity(): List<ChatMessage> {
    return this.map { it.toDomain() }
}

/**
 * Extension function to convert list of ChatMessage to list of ChatMessageEntity
 */
fun List<ChatMessage>.toEntity(): List<ChatMessageEntity> {
    return this.map { it.toEntity() }
}

/**
 * Extension function to convert list of ChatMessage to list of ChatMessageDto
 */
fun List<ChatMessage>.toDto(): List<ChatMessageDto> {
    return this.map { it.toDto() }
}