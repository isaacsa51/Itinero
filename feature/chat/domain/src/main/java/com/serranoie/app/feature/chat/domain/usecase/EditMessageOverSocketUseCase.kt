package com.serranoie.app.feature.chat.domain.usecase

import com.serranoie.app.feature.chat.domain.repository.ChatRepository

class EditMessageOverSocketUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        groupCode: String,
        messageId: Long,
        newMessage: String,
        authToken: String
    ): Result<Unit> {
        return repository.editMessageOverSocket(groupCode, messageId, newMessage, authToken)
    }
}