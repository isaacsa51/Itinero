/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: DeleteMessageUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 17 July 2025
 */

package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.repository.ChatRepository

class DeleteMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        messageId: Long,
        authToken: String
    ): Result<String> {
        return repository.deleteMessage(messageId, authToken)
    }
}