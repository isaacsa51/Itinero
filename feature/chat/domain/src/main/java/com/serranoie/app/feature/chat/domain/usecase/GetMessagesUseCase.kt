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

/**
 * Use case for retrieving chat messages
 */
class GetMessagesUseCase(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case to get messages
     * @param groupCode The group code to get messages for
     * @param authToken JWT authentication token
     * @param limit Number of messages to retrieve
     * @param offset Offset for pagination
     * @return Result containing list of chat messages or error
     */
    suspend operator fun invoke(
        groupCode: String,
        authToken: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<ChatMessage>> {
        return chatRepository.getMessages(groupCode, authToken, limit, offset)
    }
}