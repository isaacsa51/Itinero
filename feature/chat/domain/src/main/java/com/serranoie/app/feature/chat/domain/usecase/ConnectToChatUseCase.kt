/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ConnectToChatUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for connecting to real-time chat via WebSocket
 */
class ConnectToChatUseCase(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case to connect to chat
     * @param groupCode The group code to connect to
     * @param authToken JWT authentication token
     * @return Flow of chat messages from WebSocket
     */
    suspend operator fun invoke(
        groupCode: String,
        authToken: String
    ): Flow<ChatMessage> {
        return chatRepository.connectToChat(groupCode, authToken)
    }

    /**
     * Disconnect from chat
     */
    fun disconnect() {
        chatRepository.disconnect()
    }
}