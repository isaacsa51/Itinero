/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatWebSocketService.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.remote.websocket

import com.serranoie.app.feature.chat.data.mappers.toDomain
import com.serranoie.app.feature.chat.data.mappers.toDto
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Service for handling WebSocket connections for real-time chat
 * Base URL is injected via DI from BuildConfig
 */
class ChatWebSocketService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) {

    /**
     * Connect to chat WebSocket and receive messages
     * @param groupCode The group code to join the chat room
     * @param authToken JWT token for authentication
     * @return Flow of chat messages
     */
    suspend fun connectToChat(groupCode: String, authToken: String): Flow<ChatMessage> = flow {
        try {
            httpClient.webSocket(
                urlString = "$baseUrl/chat/groups/$groupCode",
                request = {
                    headers.append("Authorization", "Bearer $authToken")
                }
            ) {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                try {
                                    val messageText = frame.readText()
                                    val messageDto =
                                        json.decodeFromString<ChatMessageDto>(messageText)
                                    emit(messageDto.toDomain())
                                } catch (e: Exception) {
                                    println("Error parsing WebSocket message: ${e.message}")
                                }
                            }

                            is Frame.Binary -> {
                                // Handle binary frames if needed
                                println("Received binary frame")
                            }

                            is Frame.Close -> {
                                println("WebSocket connection closed")
                                break
                            }

                            else -> {
                                // Handle other frame types
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("WebSocket receive channel closed: ${e.message}")
                } catch (e: Exception) {
                    println("WebSocket error: ${e.message}")
                    throw e
                }
            }
        } catch (e: Exception) {
            println("Failed to connect to WebSocket: ${e.message}")
            throw e
        }
    }

    /**
     * Send a message through WebSocket
     * @param message The message to send
     * @param authToken JWT token for authentication
     */
    suspend fun sendMessage(message: ChatMessage, authToken: String) {
        try {
            httpClient.webSocket(
                urlString = "$baseUrl/chat/groups/${message.groupCode}",
                request = {
                    headers.append("Authorization", "Bearer $authToken")
                }
            ) {
                try {
                    val messageDto = message.toDto()
                    val messageJson = json.encodeToString(messageDto)
                    send(Frame.Text(messageJson))
                    println("Message sent successfully: ${message.message}")
                } catch (e: Exception) {
                    println("Error sending WebSocket message: ${e.message}")
                    throw e
                }
            }
        } catch (e: Exception) {
            println("Failed to send message via WebSocket: ${e.message}")
            throw e
        }
    }

    /**
     * Send a message to a specific WebSocket session (for long-lived connections)
     * This method assumes you maintain a persistent connection
     */
    suspend fun sendMessageToSession(
        session: Any, // You can define a proper session type based on your needs
        message: ChatMessage
    ) {
        try {
            val messageDto = message.toDto()
            val messageJson = json.encodeToString(messageDto)
            // Implementation depends on how you manage sessions
            println("Would send to session: $messageJson")
        } catch (e: Exception) {
            println("Error sending message to session: ${e.message}")
        }
    }

    /**
     * Close WebSocket connection
     * Note: The HttpClient manages connections automatically with the DI setup
     */
    fun close() {
        // The HttpClient is managed by DI, so we don't close it here
        println("WebSocket service cleanup requested")
    }
}