/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessageEntity.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a chat message entity
 */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: Long,
    val groupCode: String,
    val senderId: Long,
    val senderName: String,
    val message: String,
    val messageType: String,
    val timestamp: String,
    val isEdited: Boolean,
    val replyToMessageId: Long?
)