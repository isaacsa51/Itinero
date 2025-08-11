/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatRepository.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.domain.repository

import com.serranoie.app.feature.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

sealed class ChatEvent {
    data class MessageReceived(val message: ChatMessage) : ChatEvent()
    data class TypingStarted(val userId: Long, val userName: String) : ChatEvent()
    data class TypingStopped(val userId: Long, val userName: String) : ChatEvent()
    data class UserJoined(val userId: Int, val userName: String) : ChatEvent()
    data class UserLeft(val userId: Int, val userName: String) : ChatEvent()
}

interface ChatRepository {
    suspend fun getMessages(
        groupCode: String,
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<ChatMessage>>

    suspend fun updateMessage(
        messageId: Long,
        newMessage: String,
        authToken: String
    ): Result<String>

    suspend fun deleteMessage(
        messageId: Long,
        authToken: String
    ): Result<String>

    suspend fun connectToChat(
        groupCode: String,
        authToken: String
    ): Flow<ChatEvent>

    suspend fun sendMessage(
        message: ChatMessage,
        authToken: String
    ): Result<Unit>

    suspend fun sendTypingEvent(
        isTyping: Boolean,
        groupCode: String,
        authToken: String
    ): Result<Unit>

    fun disconnect()
}