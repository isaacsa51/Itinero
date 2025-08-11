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

import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import kotlinx.coroutines.flow.Flow

class ConnectToChatUseCase(
    private val chatRepository: ChatRepository
) {

    suspend operator fun invoke(
        groupCode: String,
        authToken: String
    ): Flow<ChatEvent> {
        return chatRepository.connectToChat(groupCode, authToken)
    }

    suspend fun sendTypingEvent(
        isTyping: Boolean,
        groupCode: String,
        authToken: String
    ): Result<Unit> {
        return chatRepository.sendTypingEvent(isTyping, groupCode, authToken)
    }

    fun disconnect() {
        chatRepository.disconnect()
    }
}