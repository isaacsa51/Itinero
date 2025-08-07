/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatMessageDao.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.serranoie.app.feature.chat.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ChatMessageEntity
 */
@Dao
interface ChatMessageDao {

    /**
     * Get all messages for a specific group, ordered by timestamp descending
     */
    @Query("SELECT * FROM chat_messages WHERE groupCode = :groupCode ORDER BY timestamp DESC")
    fun getMessagesByGroup(groupCode: String): Flow<List<ChatMessageEntity>>

    /**
     * Get messages for a specific group with limit and offset for pagination
     */
    @Query("SELECT * FROM chat_messages WHERE groupCode = :groupCode ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesByGroupPaged(
        groupCode: String,
        limit: Int,
        offset: Int
    ): List<ChatMessageEntity>

    /**
     * Get a specific message by ID
     */
    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): ChatMessageEntity?

    /**
     * Insert a new message
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    /**
     * Insert multiple messages
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    /**
     * Update an existing message
     */
    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    /**
     * Delete a specific message
     */
    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    /**
     * Delete a message by ID
     */
    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    /**
     * Delete all messages for a specific group
     */
    @Query("DELETE FROM chat_messages WHERE groupCode = :groupCode")
    suspend fun deleteMessagesByGroup(groupCode: String)

    /**
     * Get the count of messages for a specific group
     */
    @Query("SELECT COUNT(*) FROM chat_messages WHERE groupCode = :groupCode")
    suspend fun getMessageCountByGroup(groupCode: String): Int

    /**
     * Clear all messages from the database
     */
    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}