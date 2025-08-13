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
import com.serranoie.app.feature.chat.data.remote.dto.ServerWebSocketMessage
import com.serranoie.app.feature.chat.data.remote.dto.WebSocketMessage
import com.serranoie.app.feature.chat.data.remote.dto.MessageData
import com.serranoie.app.feature.chat.data.remote.dto.TypingIndicator
import com.serranoie.app.feature.chat.data.remote.dto.EditMessageData
import com.serranoie.app.feature.chat.data.remote.dto.DeleteMessageData
import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.itinero.core.domain.exception.ChatApiException
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

sealed class WebSocketEvent {
    data class MessageReceived(val message: ChatMessage) : WebSocketEvent()
    data class TypingStart(val typingIndicator: TypingIndicator) : WebSocketEvent()
    data class TypingStop(val typingIndicator: TypingIndicator) : WebSocketEvent()
    data class UserJoined(val userId: Int, val userName: String) : WebSocketEvent()
    data class UserLeft(val userId: Int, val userName: String) : WebSocketEvent()
    data class MessageEdited(
        val editedMessageId: Long,
        val editedMessage: String,
        val userId: Int?,
        val userName: String?
    ) : WebSocketEvent()

    data class MessageDeleted(val deletedMessageId: Long, val userId: Int?, val userName: String?) :
        WebSocketEvent()
}

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

    private val activeSessions = ConcurrentHashMap<String, DefaultClientWebSocketSession>()

    private fun validateGroupCode(groupCode: String) {
        if (!groupCode.matches(Regex(GROUP_CODE_PATTERN))) {
            Log.e(TAG, "Invalid group code format: $groupCode")
            throw ChatApiException("Invalid group code format")
        }
    }

    fun connectToChat(groupCode: String, authToken: String): Flow<WebSocketEvent> = flow {
        validateGroupCode(groupCode)

        try {
            httpClient.webSocket(
                urlString = "$baseUrl/chat/$groupCode", request = {
                    headers.append("Authorization", "Bearer $authToken")
                }) {

                if (this is DefaultClientWebSocketSession) {
                    activeSessions[groupCode] = this
                }

                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                try {
                                    val messageText = frame.readText()
                                    val serverMessage =
                                        json.decodeFromString<ServerWebSocketMessage>(messageText)

                                    when (serverMessage.type) {
                                        "MESSAGE_RECEIVED" -> {
                                            serverMessage.message?.let { receivedMessage ->
                                                val messageDto: ChatMessageDto? =
                                                    serverMessage.message ?: serverMessage.data
                                                if (messageDto != null) {
                                                    emit(WebSocketEvent.MessageReceived(messageDto.toDomain()))
                                                } else {
                                                    Log.e(
                                                        TAG,
                                                        "MESSAGE_RECEIVED event missing message data for group $groupCode"
                                                    )
                                                }
                                            }
                                        }

                                        "USER_JOINED" -> {
                                            serverMessage.userId?.let { userId ->
                                                serverMessage.userName?.let { userName ->
                                                    emit(
                                                        WebSocketEvent.UserJoined(
                                                            userId,
                                                            userName
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        "USER_LEFT" -> {
                                            serverMessage.userId?.let { userId ->
                                                serverMessage.userName?.let { userName ->
                                                    emit(
                                                        WebSocketEvent.UserLeft(
                                                            userId,
                                                            userName
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        "TYPING_START" -> {
                                            serverMessage.typingIndicator?.let { indicator ->
                                                emit(WebSocketEvent.TypingStart(indicator))
                                            } ?: Log.e(
                                                TAG,
                                                "TYPING_START event missing typingIndicator data"
                                            )
                                        }

                                        "TYPING_STOP" -> {
                                            serverMessage.typingIndicator?.let { indicator ->
                                                emit(WebSocketEvent.TypingStop(indicator))
                                            } ?: Log.e(
                                                TAG,
                                                "TYPING_STOP event missing typingIndicator data"
                                            )
                                        }

                                        null -> {
                                            try {
                                                val directMessage =
                                                    json.decodeFromString<ChatMessageDto>(
                                                        messageText
                                                    )
                                                emit(WebSocketEvent.MessageReceived(directMessage.toDomain()))
                                            } catch (fallbackException: Exception) {
                                                Log.e(
                                                    TAG,
                                                    "Failed to parse message in both formats for group $groupCode",
                                                    fallbackException
                                                )
                                            }
                                        }

                                        else -> {
                                            if (serverMessage.type?.contains(
                                                    "TYPING",
                                                    ignoreCase = true
                                                ) == true
                                            ) {
                                                serverMessage.typingIndicator?.let { indicator ->
                                                    if (serverMessage.type?.contains(
                                                            "START",
                                                            ignoreCase = true
                                                        ) == true
                                                    ) {
                                                        emit(WebSocketEvent.TypingStart(indicator))
                                                    } else if (serverMessage.type?.contains(
                                                            "STOP",
                                                            ignoreCase = true
                                                        ) == true
                                                    ) {
                                                        emit(WebSocketEvent.TypingStop(indicator))
                                                    }
                                                }
                                            } else if (serverMessage.type?.equals(
                                                    "EDIT_MESSAGE",
                                                    ignoreCase = true
                                                ) == true
                                            ) {
                                                val raw = messageText
                                                try {
                                                    val editedId =
                                                        Regex("\"editedMessageId\":(\\d+)")
                                                            .find(raw)?.groupValues?.getOrNull(1)
                                                            ?.toLongOrNull()
                                                    val editedMsg =
                                                        Regex("\"editedMessage\":\"(.*?)\"")
                                                            .find(raw)?.groupValues?.getOrNull(1)
                                                    val uid = serverMessage.userId
                                                    val uname = serverMessage.userName
                                                    if (editedId != null && editedMsg != null) {
                                                        emit(
                                                            WebSocketEvent.MessageEdited(
                                                                editedId,
                                                                editedMsg,
                                                                uid,
                                                                uname
                                                            )
                                                        )
                                                    }
                                                } catch (_: Exception) {
                                                }
                                            } else if (serverMessage.type?.equals(
                                                    "DELETE_MESSAGE",
                                                    ignoreCase = true
                                                ) == true
                                            ) {
                                                val raw = messageText
                                                Log.d(TAG, "DELETE_MESSAGE event: $raw")
                                                try {
                                                    val deletedId =
                                                        Regex("\"deletedMessageId\":(\\d+)")
                                                            .find(raw)?.groupValues?.getOrNull(1)
                                                            ?.toLongOrNull()
                                                    val uid = serverMessage.userId
                                                    val uname = serverMessage.userName
                                                    if (deletedId != null) {
                                                        emit(
                                                            WebSocketEvent.MessageDeleted(
                                                                deletedId,
                                                                uid,
                                                                uname
                                                            )
                                                        )
                                                    }
                                                } catch (_: Exception) {
                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Failed to parse WebSocket message for group $groupCode: ${e.message}",
                                        e
                                    )
                                }
                            }

                            is Frame.Close -> {
                                activeSessions.remove(groupCode)
                                break
                            }

                            else -> { }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    Log.e(
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
        } catch (e: ClientRequestException) {
            Log.e(
                TAG,
                "HTTP error during WebSocket connect to group $groupCode: ${e.message}",
                e
            )
            when (e.response.status) {
                HttpStatusCode.NotFound -> throw ChatApiException("Chat group not found or WebSocket endpoint unavailable for group $groupCode")
                HttpStatusCode.Unauthorized -> throw UnauthorizedException("Authentication failed while connecting to chat for group $groupCode")
                HttpStatusCode.Forbidden -> throw ChatApiException("Access denied to chat group $groupCode")
                else -> throw ChatApiException("Unable to connect to chat for group $groupCode", e)
            }
        } catch (e: ServerResponseException) {
            Log.e(TAG, "Server error during WebSocket connect to group $groupCode: ${e.message}", e)
            throw ChatApiException("Server error while connecting to chat for group $groupCode", e)
        } catch (e: RedirectResponseException) {
            Log.e(
                TAG,
                "Unexpected redirect during WebSocket connect to group $groupCode: ${e.message}",
                e
            )
            throw ChatApiException(
                "Unexpected redirect while connecting to chat for group $groupCode",
                e
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to establish WebSocket connection for group $groupCode: ${e.message}",
                e
            )
            throw ChatApiException("Unable to connect to chat for group $groupCode", e)
        }
    }

    suspend fun sendMessage(message: ChatMessage, authToken: String) {
        validateGroupCode(message.groupCode)

        val existingSession = activeSessions[message.groupCode]

        if (existingSession != null && !existingSession.outgoing.isClosedForSend) {
            try {
                sendMessageThroughSession(existingSession, message)
            } catch (e: Exception) {
                activeSessions.remove(message.groupCode)
                sendMessageWithNewConnection(message, authToken)
            }
        } else {
            sendMessageWithNewConnection(message, authToken)
        }
    }

    private suspend fun sendMessageThroughSession(session: WebSocketSession, message: ChatMessage) {
        try {
            val messageData = MessageData(
                message = message.message,
                messageType = message.messageType.value,
                replyToMessageId = message.replyToMessageId
            )
            val messageJson = json.encodeToString(messageData)
            val webSocketMessage = WebSocketMessage(
                type = "SEND_MESSAGE",
                groupCode = message.groupCode,
                data = messageJson
            )
            val fullPayloadJson = json.encodeToString(webSocketMessage)
            session.send(Frame.Text(fullPayloadJson))
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
                urlString = "$baseUrl/chat/${message.groupCode}", request = {
                    headers.append("Authorization", "Bearer $authToken")
                }) {

                if (this is DefaultClientWebSocketSession) {
                    activeSessions[message.groupCode] = this
                }

                try {
                    val messageData = MessageData(
                        message = message.message,
                        messageType = message.messageType.value,
                        replyToMessageId = message.replyToMessageId
                    )
                    val messageJson = json.encodeToString(messageData)
                    val webSocketMessage = WebSocketMessage(
                        type = "SEND_MESSAGE",
                        groupCode = message.groupCode,
                        data = messageJson
                    )
                    val fullPayloadJson = json.encodeToString(webSocketMessage)
                    send(Frame.Text(fullPayloadJson))
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            Log.e(
                TAG,
                "HTTP error while sending message to group ${message.groupCode}: ${e.message}",
                e
            )
            when (e.response.status) {
                HttpStatusCode.NotFound -> throw ChatApiException("Chat group not found or WebSocket endpoint unavailable for group ${message.groupCode}")
                HttpStatusCode.Unauthorized -> throw UnauthorizedException("Authentication failed while sending message to group ${message.groupCode}")
                HttpStatusCode.Forbidden -> throw ChatApiException("Access denied to chat group ${message.groupCode}")
                else -> throw ChatApiException(
                    "Unable to send message to group ${message.groupCode}",
                    e
                )
            }
        } catch (e: ServerResponseException) {
            Log.e(
                TAG,
                "Server error while sending message to group ${message.groupCode}: ${e.message}",
                e
            )
            throw ChatApiException(
                "Server error while sending message to group ${message.groupCode}",
                e
            )
        } catch (e: RedirectResponseException) {
            Log.e(
                TAG,
                "Unexpected redirect while sending message to group ${message.groupCode}: ${e.message}",
                e
            )
            throw ChatApiException(
                "Unexpected redirect while sending message to group ${message.groupCode}",
                e
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to establish WebSocket connection for sending message to group ${message.groupCode}: ${e.message}",
                e
            )
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException,
                is io.ktor.client.network.sockets.SocketTimeoutException,
                is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed while sending message to group ${message.groupCode}",
                        e
                    )
                }
                else -> {
                    val errorMessage = e.message ?: ""
                    when {
                        errorMessage.contains("404 Not Found") -> {
                            throw ChatApiException("Chat group not found or WebSocket endpoint unavailable for group ${message.groupCode}")
                        }
                        errorMessage.contains("401") || errorMessage.contains("Unauthorized") -> {
                            throw UnauthorizedException("Authentication failed while sending message to group ${message.groupCode}")
                        }

                        errorMessage.contains("403") || errorMessage.contains("Forbidden") -> {
                            throw ChatApiException("Access denied to chat group ${message.groupCode}")
                        }
                        else -> {
                            throw ChatApiException(
                                "Unable to send message to group ${message.groupCode}",
                                e
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun closeConnection(groupCode: String) {
        try {
            val session = activeSessions.remove(groupCode)
            session?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebSocket connection for group $groupCode: ${e.message}", e)
        }
    }

    suspend fun closeAllConnections() {
        val sessionsToClose = activeSessions.toMap()
        activeSessions.clear()

        for ((groupCode, session) in sessionsToClose) {
            try {
                session.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing connection for group $groupCode: ${e.message}", e)
            }
        }
    }

    fun hasActiveConnection(groupCode: String): Boolean {
        val session = activeSessions[groupCode]
        return session != null && !session.outgoing.isClosedForSend
    }

    suspend fun sendTypingStart(groupCode: String, authToken: String) {
        validateGroupCode(groupCode)

        val existingSession = activeSessions[groupCode]
        if (existingSession != null && !existingSession.outgoing.isClosedForSend) {
            try {
                val webSocketMessage = WebSocketMessage(
                    type = "TYPING_START",
                    groupCode = groupCode,
                    data = "{}"
                )
                val messageJson = json.encodeToString(webSocketMessage)

                existingSession.send(Frame.Text(messageJson))
            } catch (e: Exception) {
                Log.e(TAG, "Error sending typing start for group $groupCode: ${e.message}", e)
            }
        }
    }

    suspend fun sendTypingStop(groupCode: String, authToken: String) {
        validateGroupCode(groupCode)

        val existingSession = activeSessions[groupCode]
        if (existingSession != null && !existingSession.outgoing.isClosedForSend) {
            try {
                val webSocketMessage = WebSocketMessage(
                    type = "TYPING_STOP",
                    groupCode = groupCode,
                    data = "{}"
                )
                val messageJson = json.encodeToString(webSocketMessage)

                existingSession.send(Frame.Text(messageJson))
            } catch (e: Exception) {
                Log.e(TAG, "Error sending typing stop for group $groupCode: ${e.message}", e)
            }
        }
    }

    suspend fun sendEditMessage(
        groupCode: String,
        messageId: Long,
        newMessage: String,
        authToken: String
    ) {
        validateGroupCode(groupCode)
        val session = activeSessions[groupCode]
        val data = EditMessageData(messageId, newMessage)
        val dataJson = json.encodeToString(data)
        val wsPayload =
            WebSocketMessage(type = "EDIT_MESSAGE", groupCode = groupCode, data = dataJson)
        val payloadJson = json.encodeToString(wsPayload)
        if (session != null && !session.outgoing.isClosedForSend) {
            session.send(Frame.Text(payloadJson))
        } else {
            httpClient.webSocket(urlString = "$baseUrl/chat/$groupCode", request = {
                headers.append("Authorization", "Bearer $authToken")
            }) {
                send(Frame.Text(payloadJson))
            }
        }
    }

    suspend fun sendDeleteMessage(groupCode: String, messageId: Long, authToken: String) {
        validateGroupCode(groupCode)
        val session = activeSessions[groupCode]
        val data = DeleteMessageData(messageId)
        val dataJson = json.encodeToString(data)
        val wsPayload =
            WebSocketMessage(type = "DELETE_MESSAGE", groupCode = groupCode, data = dataJson)
        val payloadJson = json.encodeToString(wsPayload)
        if (session != null && !session.outgoing.isClosedForSend) {
            session.send(Frame.Text(payloadJson))
        } else {
            httpClient.webSocket(urlString = "$baseUrl/chat/$groupCode", request = {
                headers.append("Authorization", "Bearer $authToken")
            }) {
                send(Frame.Text(payloadJson))
            }
        }
    }
}