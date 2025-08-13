/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessage.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.domain.model

data class ChatMessage(
    val id: Long,
    val groupCode: String,
    val senderId: Long,
    val senderName: String,
    val message: String,
    val messageType: MessageType,
    val timestamp: String,
    val isEdited: Boolean,
    val replyToMessageId: Long?
)

enum class MessageType(val value: String) {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    FILE("FILE");

    companion object {
        fun fromString(value: String): MessageType {
            return entries.find { it.value == value } ?: TEXT
        }
    }
}