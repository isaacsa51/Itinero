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
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
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
    ): Flow<ChatMessage> {
        return webSocketService.connectToChat(groupCode, authToken)
    }

    override suspend fun sendMessage(
        message: ChatMessage, authToken: String
    ): Result<Unit> {
        return try {
            webSocketService.sendMessage(message, authToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun disconnect() {
        // Note: We can't call suspend functions from non-suspend context
        // For now, we'll use a fire-and-forget approach
        kotlinx.coroutines.GlobalScope.launch {
            webSocketService.closeAllConnections()
        }
    }
}