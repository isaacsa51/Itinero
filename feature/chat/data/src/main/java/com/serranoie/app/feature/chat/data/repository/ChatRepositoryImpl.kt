/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatRepositoryImpl.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.repository

import android.util.Log
import com.serranoie.app.feature.chat.data.mappers.toDomain
import com.serranoie.app.feature.chat.data.remote.api.ChatApiService
import com.serranoie.app.feature.chat.data.remote.dto.UpdateMessageDto
import com.serranoie.app.feature.chat.data.remote.websocket.ChatWebSocketService
import com.serranoie.app.feature.chat.data.remote.websocket.WebSocketEvent
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class ChatRepositoryImpl(
    private val apiService: ChatApiService, private val webSocketService: ChatWebSocketService
) : ChatRepository {

    companion object {
        private const val TAG = "ChatRepositoryImpl"
    }

    override suspend fun getMessages(
        groupCode: String, authToken: String, limit: Int, offset: Int
    ): Result<List<ChatMessage>> {
        return try {
            val messageDtos = apiService.getMessages(groupCode, authToken, limit, offset)
            val domainMessages = messageDtos.toDomain()
            Result.success(domainMessages)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting messages: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateMessage(
        messageId: Long, newMessage: String, authToken: String
    ): Result<String> {
        return try {
            val updateDto = UpdateMessageDto(newMessage = newMessage)
            val response = apiService.updateMessage(messageId, updateDto, authToken)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating message: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(
        messageId: Long, authToken: String
    ): Result<String> {
        return try {
            val response = apiService.deleteMessage(messageId, authToken)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting message: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun connectToChat(
        groupCode: String, authToken: String
    ): Flow<ChatEvent> {
        return webSocketService.connectToChat(groupCode, authToken)
            .mapNotNull { webSocketEvent ->
                when (webSocketEvent) {
                    is WebSocketEvent.MessageReceived -> {
                        ChatEvent.MessageReceived(webSocketEvent.message)
                    }

                    is WebSocketEvent.TypingStart -> {
                        ChatEvent.TypingStarted(
                            webSocketEvent.typingIndicator.userId,
                            webSocketEvent.typingIndicator.userName
                        )
                    }

                    is WebSocketEvent.TypingStop -> {
                        ChatEvent.TypingStopped(
                            webSocketEvent.typingIndicator.userId,
                            webSocketEvent.typingIndicator.userName
                        )
                    }

                    is WebSocketEvent.UserJoined -> {
                        ChatEvent.UserJoined(
                            webSocketEvent.userId.toLong(),
                            webSocketEvent.userName
                        )
                    }

                    is WebSocketEvent.UserLeft -> {
                        ChatEvent.UserLeft(
                            webSocketEvent.userId.toLong(),
                            webSocketEvent.userName
                        )
                    }

                    is WebSocketEvent.MessageEdited -> {
                        ChatEvent.MessageEdited(
                            webSocketEvent.editedMessageId,
                            webSocketEvent.editedMessage,
                            webSocketEvent.userId?.toLong(),
                            webSocketEvent.userName
                        )
                    }

                    is WebSocketEvent.MessageDeleted -> {
                        ChatEvent.MessageDeleted(
                            webSocketEvent.deletedMessageId,
                            webSocketEvent.userId?.toLong(),
                            webSocketEvent.userName
                        )
                    }
                }
            }
    }

    override suspend fun sendMessage(
        message: ChatMessage, authToken: String
    ): Result<Unit> {
        return try {
            webSocketService.sendMessage(message, authToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ChatRepositoryImpl", " Failed to send message: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun sendTypingEvent(
        isTyping: Boolean,
        groupCode: String,
        authToken: String
    ): Result<Unit> {
        return try {
            if (isTyping) {
                webSocketService.sendTypingStart(groupCode, authToken)
            } else {
                webSocketService.sendTypingStop(groupCode, authToken)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending typing event: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun editMessageOverSocket(
        groupCode: String,
        messageId: Long,
        newMessage: String,
        authToken: String
    ): Result<Unit> {
        return try {
            webSocketService.sendEditMessage(groupCode, messageId, newMessage, authToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending edit over socket: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteMessageOverSocket(
        groupCode: String,
        messageId: Long,
        authToken: String
    ): Result<Unit> {
        return try {
            webSocketService.sendDeleteMessage(groupCode, messageId, authToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending delete over socket: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun disconnect() {
        // TODO: Is this correct?
        // Note: We can't call suspend functions from non-suspend context
        // For now, we'll use a fire-and-forget approach
        kotlinx.coroutines.GlobalScope.launch {
            webSocketService.closeAllConnections()
        }
    }
}