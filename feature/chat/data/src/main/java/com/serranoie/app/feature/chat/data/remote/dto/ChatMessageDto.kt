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

    @SerialName("senderId")
    val senderId: String? = null,

    @SerialName("senderName")
    val senderName: String? = null
)

@Serializable
data class ServerWebSocketMessage(
    @SerialName("type")
    val type: String? = null,

    @SerialName("groupCode")
    val groupCode: String? = null,

    @SerialName("userId")
    val userId: Int? = null,

    @SerialName("userName")
    val userName: String? = null,

    @SerialName("message")
    val message: ChatMessageDto? = null,

    @SerialName("data")
    val data: ChatMessageDto? = null,

    @SerialName("typingIndicator")
    val typingIndicator: TypingIndicator? = null
)

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

@Serializable
data class WebSocketMessage(
    @SerialName("type")
    val type: String,

    @SerialName("groupCode")
    val groupCode: String,

    @SerialName("data")
    val data: String
)

@Serializable
data class MessageData(
    @SerialName("message")
    val message: String,

    @SerialName("messageType")
    val messageType: String = "TEXT",

    @SerialName("replyToMessageId")
    val replyToMessageId: Long? = null
)

@Serializable
data class TypingIndicator(
    @SerialName("userId")
    val userId: Long,

    @SerialName("userName")
    val userName: String,

    @SerialName("isTyping")
    val isTyping: Boolean
)

@Serializable
data class ChatNotification(
    @SerialName("type")
    val type: String,

    @SerialName("data")
    val data: ChatMessageDto
)

@Serializable
data class EditMessageData(
    @SerialName("messageId")
    val messageId: Long,

    @SerialName("newMessage")
    val newMessage: String
)

@Serializable
data class DeleteMessageData(
    @SerialName("messageId")
    val messageId: Long
)