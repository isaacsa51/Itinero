/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessageDto.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    @SerialName("id")
    val id: Long = 0,

    @SerialName("groupCode")
    val groupCode: String = "",

    @SerialName("authorId")
    val authorId: String = "",

    @SerialName("authorName")
    val authorName: String = "",

    @SerialName("message")
    val message: String = "",

    @SerialName("messageType")
    val messageType: String = "TEXT",

    @SerialName("timestamp")
    val timestamp: String = "",

    @SerialName("isEdited")
    val isEdited: Boolean = false,

    @SerialName("replyToId")
    val replyToId: Long? = null,

    // Alternative field names the server might use
    @SerialName("senderId")
    val senderId: String? = null,

    @SerialName("senderName")
    val senderName: String? = null,

    @SerialName("content")
    val content: String? = null
)

// Base WebSocket message from server
@Serializable
data class ServerWebSocketMessage(
    @SerialName("type")
    val type: String? = null,

    @SerialName("groupCode")
    val groupCode: String? = null,

    // For USER_JOINED/USER_LEFT messages
    @SerialName("userId")
    val userId: Int? = null,

    @SerialName("userName")
    val userName: String? = null,

    // For MESSAGE_RECEIVED notifications - the actual message data
    @SerialName("message")
    val message: ChatMessageDto? = null,

    // Alternative field name in case server uses 'data'
    @SerialName("data")
    val data: ChatMessageDto? = null,

    // For typing indicators
    @SerialName("typingIndicator")
    val typingIndicator: TypingIndicator? = null
)

// User activity messages
@Serializable
data class UserActivityMessage(
    @SerialName("type")
    val type: String, // USER_JOINED, USER_LEFT, etc.

    @SerialName("groupCode")
    val groupCode: String,

    @SerialName("userId")
    val userId: Int,

    @SerialName("userName")
    val userName: String
)

// WebSocket message format for sending to backend
@Serializable
data class WebSocketMessage(
    @SerialName("type")
    val type: String,

    @SerialName("groupCode")
    val groupCode: String,

    @SerialName("data")
    val data: String // JSON string of the actual message data
)

// Message data format (what goes inside the "data" field when sending)
@Serializable
data class MessageData(
    @SerialName("message")
    val message: String,

    @SerialName("messageType")
    val messageType: String = "TEXT"
)

// Typing indicator data from server
@Serializable
data class TypingIndicator(
    @SerialName("userId")
    val userId: Long,

    @SerialName("userName")
    val userName: String,

    @SerialName("isTyping")
    val isTyping: Boolean
)

// Chat notification from server (for MESSAGE_RECEIVED)
@Serializable
data class ChatNotification(
    @SerialName("type")
    val type: String,

    @SerialName("data")
    val data: ChatMessageDto
)