/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: EditMessageUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 17 July 2025
 */

package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.repository.ChatRepository

class EditMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        messageId: Long,
        newMessage: String,
        authToken: String
    ): Result<String> {
        return repository.updateMessage(messageId, newMessage, authToken)
    }
}