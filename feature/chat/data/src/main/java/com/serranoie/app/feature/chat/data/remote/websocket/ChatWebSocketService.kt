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

import android.util.Log
import com.serranoie.app.feature.chat.data.mappers.toDomain
import com.serranoie.app.feature.chat.data.mappers.toDto
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.itinero.core.domain.exception.ChatApiException
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class ChatWebSocketService(
    private val httpClient: HttpClient, private val baseUrl: String, private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) {

    companion object {
        private const val TAG = "ChatWebSocketService"
        private const val GROUP_CODE_PATTERN = "^[A-Z]{3}-\\d{5}$"
    }

    // Store active WebSocket sessions for reuse
    private val activeSessions = ConcurrentHashMap<String, DefaultClientWebSocketSession>()

    private fun validateGroupCode(groupCode: String) {
        if (!groupCode.matches(Regex(GROUP_CODE_PATTERN))) {
            Log.e(TAG, "Invalid group code format: $groupCode")
            throw ChatApiException("Invalid group code format")
        }
    }

    suspend fun connectToChat(groupCode: String, authToken: String): Flow<ChatMessage> = flow {
        validateGroupCode(groupCode)

        try {
            Log.d(TAG, "Attempting to connect to WebSocket for group: $groupCode")

            httpClient.webSocket(
                urlString = "$baseUrl/chat/groups/$groupCode", request = {
                    headers.append("Authorization", "Bearer $authToken")
                }) {
                Log.i(TAG, "WebSocket connection established for group: $groupCode")

                if (this is DefaultClientWebSocketSession) {
                    activeSessions[groupCode] = this
                }

                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                try {
                                    val messageText = frame.readText()
                                    Log.d(TAG, "Received WebSocket message for group $groupCode")

                                    val messageDto =
                                        json.decodeFromString<ChatMessageDto>(messageText)
                                    emit(messageDto.toDomain())
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Failed to parse WebSocket message for group $groupCode: ${e.message}",
                                        e
                                    )
                                }
                            }

                            is Frame.Binary -> {
                                Log.w(TAG, "Received unexpected binary frame for group $groupCode")
                            }

                            is Frame.Close -> {
                                Log.i(TAG, "WebSocket connection closed for group: $groupCode")
                                activeSessions.remove(groupCode)
                                break
                            }

                            else -> {
                                Log.d(
                                    TAG, "Received other frame type: ${frame.javaClass.simpleName}"
                                )
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    Log.w(
                        TAG, "WebSocket receive channel closed for group $groupCode: ${e.message}"
                    )
                    activeSessions.remove(groupCode)
                } catch (e: Exception) {
                    Log.e(TAG, "WebSocket processing error for group $groupCode: ${e.message}", e)
                    activeSessions.remove(groupCode)
                    throw ChatApiException("WebSocket communication failed for group $groupCode", e)
                }
            }
        } catch (e: ChatApiException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to establish WebSocket connection for group $groupCode: ${e.message}",
                e
            )
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException, is io.ktor.client.network.sockets.SocketTimeoutException, is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed while connecting to chat for group $groupCode", e
                    )
                }

                else -> {
                    // Check for authentication errors based on exception message or type
                    if (e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true) {
                        throw UnauthorizedException("Authentication failed while connecting to chat for group $groupCode")
                    }
                    throw ChatApiException("Unable to connect to chat for group $groupCode", e)
                }
            }
        }
    }

    suspend fun sendMessage(message: ChatMessage, authToken: String) {
        validateGroupCode(message.groupCode)

        val existingSession = activeSessions[message.groupCode]

        if (existingSession != null && !existingSession.outgoing.isClosedForSend) {
            // Use existing session
            try {
                Log.d(
                    TAG,
                    "Sending message through existing WebSocket session for group: ${message.groupCode}"
                )
                sendMessageThroughSession(existingSession, message)
            } catch (e: Exception) {
                Log.w(
                    TAG,
                    "Failed to send through existing session for group ${message.groupCode}, creating new connection: ${e.message}"
                )
                activeSessions.remove(message.groupCode)
                sendMessageWithNewConnection(message, authToken)
            }
        } else {
            Log.d(
                TAG,
                "Creating new WebSocket connection to send message for group: ${message.groupCode}"
            )
            sendMessageWithNewConnection(message, authToken)
        }
    }

    private suspend fun sendMessageThroughSession(session: WebSocketSession, message: ChatMessage) {
        try {
            val messageDto = message.toDto()
            val messageJson = json.encodeToString(messageDto)
            session.send(Frame.Text(messageJson))
            Log.i(
                TAG,
                "Message sent successfully through existing session for group: ${message.groupCode}"
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error sending message through session for group ${message.groupCode}: ${e.message}",
                e
            )
            throw ChatApiException(
                "Failed to send message through existing connection for group ${message.groupCode}",
                e
            )
        }
    }

    private suspend fun sendMessageWithNewConnection(message: ChatMessage, authToken: String) {
        try {
            httpClient.webSocket(
                urlString = "$baseUrl/chat/groups/${message.groupCode}", request = {
                    headers.append("Authorization", "Bearer $authToken")
                }) {
                Log.d(
                    TAG,
                    "New WebSocket connection established for sending message to group: ${message.groupCode}"
                )

                if (this is DefaultClientWebSocketSession) {
                    activeSessions[message.groupCode] = this
                }

                try {
                    val messageDto = message.toDto()
                    val messageJson = json.encodeToString(messageDto)
                    send(Frame.Text(messageJson))
                    Log.i(
                        TAG,
                        "Message sent successfully through new connection for group: ${message.groupCode}"
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error sending message through new connection for group ${message.groupCode}: ${e.message}",
                        e
                    )
                    throw ChatApiException(
                        "Failed to send message for group ${message.groupCode}", e
                    )
                }
            }
        } catch (e: ChatApiException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to establish WebSocket connection for sending message to group ${message.groupCode}: ${e.message}",
                e
            )
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException, is io.ktor.client.network.sockets.SocketTimeoutException, is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed while sending message to group ${message.groupCode}",
                        e
                    )
                }

                else -> {
                    if (e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true) {
                        throw UnauthorizedException("Authentication failed while sending message to group ${message.groupCode}")
                    }
                    throw ChatApiException(
                        "Unable to send message to group ${message.groupCode}", e
                    )
                }
            }
        }
    }

    suspend fun closeConnection(groupCode: String) {
        try {
            val session = activeSessions.remove(groupCode)
            if (session != null) {
                session.close()
                Log.i(TAG, "WebSocket connection closed for group: $groupCode")
            } else {
                Log.d(TAG, "No active WebSocket connection found for group: $groupCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebSocket connection for group $groupCode: ${e.message}", e)
        }
    }

    suspend fun closeAllConnections() {
        Log.i(TAG, "Closing all active WebSocket connections")
        val sessionsToClose = activeSessions.toMap()
        activeSessions.clear()

        for ((groupCode, session) in sessionsToClose) {
            try {
                session.close()
                Log.d(TAG, "Closed connection for group: $groupCode")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing connection for group $groupCode: ${e.message}", e)
            }
        }
    }

    fun hasActiveConnection(groupCode: String): Boolean {
        val session = activeSessions[groupCode]
        val isActive = session != null && !session.outgoing.isClosedForSend
        Log.d(TAG, "Active connection check for group $groupCode: $isActive")
        return isActive
    }
}