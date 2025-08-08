/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SendMessageUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.repository.ChatRepository

/**
 * Use case for sending chat messages via WebSocket
 */
class SendMessageUseCase(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case to send a message
     * @param message The chat message to send
     * @param authToken JWT authentication token
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        message: ChatMessage,
        authToken: String
    ): Result<Unit> {
        return chatRepository.sendMessage(message, authToken)
    }
}