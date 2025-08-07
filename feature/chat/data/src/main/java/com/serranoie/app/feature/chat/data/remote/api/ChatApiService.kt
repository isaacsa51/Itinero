/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatApiService.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.remote.api

import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.data.remote.dto.UpdateMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Service for handling chat HTTP API operations
 * Base URL is injected via DI from BuildConfig
 */
class ChatApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    /**
     * Get chat messages for a group
     * @param groupCode The group code to get messages for
     * @param authToken JWT token for authentication
     * @param limit Number of messages to retrieve (default: 20)
     * @param offset Offset for pagination (default: 0)
     * @return List of chat message DTOs
     */
    suspend fun getMessages(
        groupCode: String,
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<ChatMessageDto> {
        return httpClient.get("$baseUrl/chat/groups/$groupCode/messages") {
            header("Authorization", "Bearer $authToken")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    /**
     * Update a chat message
     * @param messageId The ID of the message to update
     * @param updateMessageDto The new message content
     * @param authToken JWT token for authentication
     * @return Success message
     */
    suspend fun updateMessage(
        messageId: Long,
        updateMessageDto: UpdateMessageDto,
        authToken: String
    ): String {
        return httpClient.put("$baseUrl/chat/messages/$messageId") {
            header("Authorization", "Bearer $authToken")
            contentType(ContentType.Application.Json)
            setBody(updateMessageDto)
        }.body()
    }

    /**
     * Delete a chat message
     * @param messageId The ID of the message to delete
     * @param authToken JWT token for authentication
     * @return Success message
     */
    suspend fun deleteMessage(
        messageId: Long,
        authToken: String
    ): String {
        return httpClient.delete("$baseUrl/chat/messages/$messageId") {
            header("Authorization", "Bearer $authToken")
        }.body()
    }
}