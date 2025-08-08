/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessageDto.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: Long,
    val groupCode: String,
    val senderId: Long,
    val senderName: String,
    val message: String,
    val messageType: String,
    val timestamp: String,
    val isEdited: Boolean,
    val replyToMessageId: Long?
)