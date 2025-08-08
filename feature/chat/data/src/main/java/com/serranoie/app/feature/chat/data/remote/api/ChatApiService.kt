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

import android.util.Log
import com.serranoie.app.feature.chat.data.remote.dto.ChatMessageDto
import com.serranoie.app.feature.chat.data.remote.dto.UpdateMessageDto
import com.serranoie.itinero.core.domain.exception.ChatApiException
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ChatApiService(
    private val httpClient: HttpClient, private val baseUrl: String
) {

    companion object {
        private const val TAG = "ChatApiService"
        private const val GROUP_CODE_PATTERN = "^[A-Z]{3}-\\d{5}$"
    }

    private fun validateGroupCode(groupCode: String) {
        if (groupCode.isBlank()) {
            Log.e(TAG, "Group code cannot be empty")
            throw ChatApiException("Group code is required")
        }

        if (!groupCode.matches(Regex(GROUP_CODE_PATTERN))) {
            Log.e(TAG, "Invalid group code format provided")
            throw ChatApiException("Invalid group code format")
        }
    }

    private fun buildSafeUrl(basePath: String, vararg pathSegments: String): String {
        val urlBuilder = URLBuilder(baseUrl)
        val pathParts = mutableListOf<String>()

        // Add base path parts
        pathParts.addAll(basePath.split("/").filter { it.isNotEmpty() })

        // Add and encode path segments
        pathSegments.forEach { segment ->
            pathParts.add(URLEncoder.encode(segment, StandardCharsets.UTF_8.toString()))
        }

        urlBuilder.pathSegments = pathParts
        return urlBuilder.buildString()
    }

    suspend fun getMessages(
        groupCode: String, authToken: String, limit: Int = 20, offset: Int = 0
    ): List<ChatMessageDto> {
        validateGroupCode(groupCode)

        // Validate input parameters
        if (limit <= 0 || limit > 100) {
            throw ChatApiException("Limit must be between 1 and 100")
        }
        if (offset < 0) {
            throw ChatApiException("Offset must be non-negative")
        }

        return try {
            Log.d(TAG, "Fetching messages for group: $groupCode")

            val safeUrl = buildSafeUrl("chat/groups", groupCode, "messages")
            val response: HttpResponse = httpClient.get {
                expectSuccess = false
                url(safeUrl)
                header("Authorization", "Bearer $authToken")
                parameter("limit", limit)
                parameter("offset", offset)
            }

            when {
                response.status == HttpStatusCode.Unauthorized -> {
                    Log.w(TAG, "Authentication failed while fetching messages")
                    throw UnauthorizedException("Authentication failed while fetching messages")
                }

                response.status == HttpStatusCode.Forbidden -> {
                    Log.w(TAG, "Access forbidden for group")
                    throw ChatApiException("You don't have permission to access this chat group.")
                }

                response.status == HttpStatusCode.NotFound -> {
                    Log.w(TAG, "Chat group not found")
                    throw ChatApiException("Chat group not found.")
                }

                response.status.isSuccess() -> {
                    Log.i(TAG, "Successfully fetched messages for group: $groupCode")
                    response.body<List<ChatMessageDto>>()
                }

                else -> {
                    Log.e(TAG, "API error while fetching messages: ${response.status}")
                    throw ChatApiException("Failed to fetch messages. Please try again.")
                }
            }
        } catch (e: UnauthorizedException) {
            throw e
        } catch (e: ChatApiException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching messages: ${e.message}", e)
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException, is io.ktor.client.network.sockets.SocketTimeoutException, is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed. Please check your internet connection.", e
                    )
                }

                else -> {
                    throw ChatApiException("Unable to load messages. Please try again later.", e)
                }
            }
        }
    }

    suspend fun updateMessage(
        messageId: Long, updateMessageDto: UpdateMessageDto, authToken: String
    ): String {
        // Validate input parameters
        if (messageId <= 0) {
            throw ChatApiException("Invalid message ID")
        }
        if (updateMessageDto.newMessage.isBlank()) {
            throw ChatApiException("Message content cannot be empty")
        }

        return try {
            Log.d(TAG, "Updating message with ID: $messageId")

            val safeUrl = buildSafeUrl("chat/messages", messageId.toString())
            val response: HttpResponse = httpClient.put {
                url(safeUrl)
                header("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(updateMessageDto)
            }

            when {
                response.status == HttpStatusCode.Unauthorized -> {
                    Log.w(TAG, "Authentication failed while updating message")
                    throw UnauthorizedException("Authentication failed while updating message")
                }

                response.status == HttpStatusCode.Forbidden -> {
                    Log.w(TAG, "Permission denied for message update")
                    throw ChatApiException("You don't have permission to edit this message.")
                }

                response.status == HttpStatusCode.NotFound -> {
                    Log.w(TAG, "Message not found for update")
                    throw ChatApiException("Message not found or has been deleted.")
                }

                response.status.isSuccess() -> {
                    Log.i(TAG, "Successfully updated message with ID: $messageId")
                    response.body<String>()
                }

                else -> {
                    Log.e(TAG, "API error while updating message: ${response.status}")
                    throw ChatApiException("Failed to update message. Please try again.")
                }
            }
        } catch (e: UnauthorizedException) {
            throw e
        } catch (e: ChatApiException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error updating message: ${e.message}", e)
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException, is io.ktor.client.network.sockets.SocketTimeoutException, is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed. Please check your internet connection.", e
                    )
                }

                else -> {
                    throw ChatApiException("Unable to update message. Please try again later.", e)
                }
            }
        }
    }

    suspend fun deleteMessage(
        messageId: Long, authToken: String
    ): String {
        // Validate input parameters
        if (messageId <= 0) {
            throw ChatApiException("Invalid message ID")
        }

        return try {
            Log.d(TAG, "Deleting message with ID: $messageId")

            val safeUrl = buildSafeUrl("chat/messages", messageId.toString())
            val response: HttpResponse = httpClient.delete {
                url(safeUrl)
                header("Authorization", "Bearer $authToken")
            }

            when {
                response.status == HttpStatusCode.Unauthorized -> {
                    Log.w(TAG, "Authentication failed while deleting message")
                    throw UnauthorizedException("Authentication failed while deleting message")
                }

                response.status == HttpStatusCode.Forbidden -> {
                    Log.w(TAG, "Permission denied for message deletion")
                    throw ChatApiException("You don't have permission to delete this message.")
                }

                response.status == HttpStatusCode.NotFound -> {
                    Log.w(TAG, "Message not found for deletion")
                    throw ChatApiException("Message not found or has already been deleted.")
                }

                response.status.isSuccess() -> {
                    Log.i(TAG, "Successfully deleted message with ID: $messageId")
                    response.body<String>()
                }

                else -> {
                    Log.e(TAG, "API error while deleting message: ${response.status}")
                    throw ChatApiException("Failed to delete message. Please try again.")
                }
            }
        } catch (e: UnauthorizedException) {
            throw e
        } catch (e: ChatApiException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting message: ${e.message}", e)
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException, is io.ktor.client.network.sockets.SocketTimeoutException, is java.net.UnknownHostException -> {
                    throw NetworkException(
                        "Network connection failed. Please check your internet connection.", e
                    )
                }

                else -> {
                    throw ChatApiException("Unable to delete message. Please try again later.", e)
                }
            }
        }
    }
}