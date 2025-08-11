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

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages WHERE groupCode = :groupCode ORDER BY timestamp DESC")
    fun getMessagesByGroup(groupCode: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE groupCode = :groupCode ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesByGroupPaged(
        groupCode: String,
        limit: Int,
        offset: Int
    ): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): ChatMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("DELETE FROM chat_messages WHERE groupCode = :groupCode")
    suspend fun deleteMessagesByGroup(groupCode: String)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE groupCode = :groupCode")
    suspend fun getMessageCountByGroup(groupCode: String): Int

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}