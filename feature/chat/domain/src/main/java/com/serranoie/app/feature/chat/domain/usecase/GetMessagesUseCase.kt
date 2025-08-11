/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: GetMessagesUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.model.ChatMessage
import com.serranoie.app.feature.chat.domain.repository.ChatRepository

class GetMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        groupCode: String,
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<ChatMessage>> {
        return chatRepository.getMessages(groupCode, authToken, limit, offset)
    }
}