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

/**
 * Repository interface for chat operations
 */
interface ChatRepository {

    /**
     * Get chat messages for a group
     * @param groupCode The group code to get messages for
     * @param authToken JWT token for authentication
     * @param limit Number of messages to retrieve
     * @param offset Offset for pagination
     * @return Result containing list of chat messages or error
     */
    suspend fun getMessages(
        groupCode: String,
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<ChatMessage>>

    /**
     * Update a chat message
     * @param messageId The ID of the message to update
     * @param newMessage The new message content
     * @param authToken JWT token for authentication
     * @return Result containing success message or error
     */
    suspend fun updateMessage(
        messageId: Long,
        newMessage: String,
        authToken: String
    ): Result<String>

    /**
     * Delete a chat message
     * @param messageId The ID of the message to delete
     * @param authToken JWT token for authentication
     * @return Result containing success message or error
     */
    suspend fun deleteMessage(
        messageId: Long,
        authToken: String
    ): Result<String>

    /**
     * Connect to real-time chat for a group
     * @param groupCode The group code to connect to
     * @param authToken JWT token for authentication
     * @return Flow of chat messages from WebSocket
     */
    suspend fun connectToChat(
        groupCode: String,
        authToken: String
    ): Flow<ChatMessage>

    /**
     * Send a message through real-time connection
     * @param message The message to send
     * @param authToken JWT token for authentication
     * @return Result indicating success or failure
     */
    suspend fun sendMessage(
        message: ChatMessage,
        authToken: String
    ): Result<Unit>

    /**
     * Disconnect from real-time chat
     */
    fun disconnect()
}