/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatViewModel.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 07 August 2025
 */

package com.serranoie.app.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.designsystemlib.ui.theme.component.MessageData
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import com.serranoie.app.feature.chat.domain.usecase.ConnectToChatUseCase
import com.serranoie.app.feature.chat.domain.usecase.DeleteMessageUseCase
import com.serranoie.app.feature.chat.domain.usecase.EditMessageOverSocketUseCase
import com.serranoie.app.feature.chat.domain.usecase.EditMessageUseCase
import com.serranoie.app.feature.chat.domain.usecase.GetMessagesUseCase
import com.serranoie.app.feature.chat.domain.usecase.SendMessageUseCase

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage as UiChatMessage
import com.serranoie.app.feature.chat.domain.model.ChatMessage as DomainChatMessage

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val connectToChatUseCase: ConnectToChatUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val editMessageOverSocketUseCase: EditMessageOverSocketUseCase,
    private val getCurrentUserId: () -> String,
    private val getCurrentUserName: () -> String,
    private val getAuthToken: suspend () -> String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatScreenUiState(
            channelName = "", channelMembers = 0, initialMessages = emptyList()
        )
    )
    val uiState: StateFlow<ChatScreenUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _typingUsers = MutableStateFlow<Set<String>>(emptySet())
    val typingUsers: StateFlow<Set<String>> = _typingUsers.asStateFlow()

    private var webSocketJob: Job? = null
    private var currentGroupCode: String = ""

    private val _allMessages = MutableStateFlow<List<UiChatMessage>>(emptyList())

    fun initializeChat(groupCode: String, groupName: String, memberCount: Int) {
        currentGroupCode = groupCode

        _uiState.update { currentState ->
            currentState.copy(
                channelName = groupName, channelMembers = memberCount, initialMessages = emptyList()
            )
        }

        loadMessages(groupCode)
        connectToRealTimeChat(groupCode)
    }

    private fun loadMessages(groupCode: String, limit: Int = 50, offset: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            getMessagesUseCase(groupCode, getAuthToken(), limit, offset).onSuccess { messages ->
                val uiMessages = messages.map { domainMessage ->
                    val (replyAuthorName, replyMessage) = resolveReplyInfo(
                        domainMessage.replyToMessageId, messages
                    )
                    UiChatMessage(
                        id = domainMessage.id.toString(),
                        content = domainMessage.message,
                        authorId = domainMessage.senderId.toString(),
                        authorName = domainMessage.senderName,
                        timestamp = formatTimestamp(domainMessage.timestamp),
                        rawTimestamp = domainMessage.timestamp,
                        replyToMessageId = domainMessage.replyToMessageId?.toString(),
                        replyAuthorName = replyAuthorName,
                        replyMessage = replyMessage,
                        isEdited = domainMessage.isEdited
                    )
                }

                _uiState.update { currentState ->
                    currentState.copy(initialMessages = uiMessages)
                }
                _allMessages.update { currentState ->
                    (currentState + uiMessages).distinctBy { it.id }
                }
            }.onFailure { exception ->
                _error.value = "Failed to load messages: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    private fun connectToRealTimeChat(groupCode: String) {
        webSocketJob?.cancel()
        connectToChatUseCase.disconnect()

        webSocketJob = viewModelScope.launch {
            try {
                _isConnected.value = false

                connectToChatUseCase(groupCode, getAuthToken()).catch { exception ->
                    _isConnected.value = false
                    _error.value = "Connection failed: ${exception.message}"
                }.collect { event ->
                    when (event) {
                        is ChatEvent.MessageReceived -> {
                            if (!_isConnected.value) {
                                _isConnected.value = true
                                _error.value = null
                            }

                            val domainMessage = event.message
                            val allCachedMessages = _allMessages.value

                            val (replyAuthorName, replyMessage) = resolveReplyInfoFromUi(
                                domainMessage.replyToMessageId, allCachedMessages
                            )

                            val newUiMessage = UiChatMessage(
                                id = domainMessage.id.toString(),
                                content = domainMessage.message,
                                authorId = domainMessage.senderId.toString(),
                                authorName = domainMessage.senderName,
                                timestamp = formatTimestamp(domainMessage.timestamp),
                                rawTimestamp = domainMessage.timestamp,
                                replyToMessageId = domainMessage.replyToMessageId?.toString(),
                                replyAuthorName = replyAuthorName,
                                replyMessage = replyMessage,
                                isEdited = domainMessage.isEdited
                            )

                            _uiState.update { currentState ->
                                val existingMessageIds =
                                    currentState.initialMessages.map { it.id }.toSet()
                                if (newUiMessage.id !in existingMessageIds) {
                                    currentState.copy(
                                        initialMessages = currentState.initialMessages + newUiMessage
                                    )
                                } else {
                                    currentState
                                }
                            }
                            _allMessages.update { currentState ->
                                currentState + newUiMessage
                            }
                        }

                        is ChatEvent.TypingStarted -> {
                            val currentUserName = getCurrentUserName()
                            if (event.userName != currentUserName) {
                                _typingUsers.update { typingSet ->
                                    typingSet + event.userName
                                }
                            }
                        }

                        is ChatEvent.TypingStopped -> {
                            _typingUsers.update { typingSet ->
                                typingSet - event.userName
                            }
                        }

                        is ChatEvent.UserJoined -> {}

                        is ChatEvent.UserLeft -> {}

                        is ChatEvent.MessageDeleted -> {
                            val messageId = event.messageId.toString()
                            val deletedByName = event.userName ?: "Unknown"
                            updateMessageAsDeleted(messageId, deletedByName)
                        }

                        is ChatEvent.MessageEdited -> {
                            val messageId = event.messageId.toString()
                            val newMessage = event.newMessage
                            updateMessageAsEdited(messageId, newMessage, true)
                        }
                    }
                }
            } catch (e: Exception) {
                _isConnected.value = false
                _error.value = "WebSocket error: ${e.message}"
            }
        }
    }

    fun sendMessage(messageData: MessageData) {
        viewModelScope.launch {
            try {
                val currentUserId = getCurrentUserId()
                val message = DomainChatMessage(
                    id = 0L,
                    groupCode = currentGroupCode,
                    senderId = currentUserId.toLongOrNull() ?: 0L,
                    senderName = getCurrentUserName(),
                    message = messageData.message,
                    messageType = MessageType.TEXT,
                    timestamp = System.currentTimeMillis().toString(),
                    isEdited = false,
                    replyToMessageId = messageData.replyToMessageId?.toLongOrNull()
                )
                sendMessageUseCase(message, getAuthToken()).onFailure { exception ->
                    Log.e(TAG, "Failed to send message: ${exception.message}")
                    _error.value = "Failed to send message: ${exception.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while sending message: ${e.message}", e)
                _error.value = "Failed to send message: ${e.message}"
            }
        }
    }

    fun retryConnection() {
        if (currentGroupCode.isNotBlank()) {
            connectToRealTimeChat(currentGroupCode)
        }
    }

    fun sendTypingStopped() {
        if (currentGroupCode.isNotBlank()) {
            viewModelScope.launch {
                connectToChatUseCase.sendTypingEvent(
                    isTyping = false, groupCode = currentGroupCode, authToken = getAuthToken()
                ).onFailure { exception ->
                    Log.e(TAG, "Failed to send typing stopped: ${exception.message}")
                }
            }
        }
    }

    fun sendTypingStarted() {
        if (currentGroupCode.isNotBlank()) {
            viewModelScope.launch {
                connectToChatUseCase.sendTypingEvent(
                    isTyping = true, groupCode = currentGroupCode, authToken = getAuthToken()
                ).onFailure { exception ->
                    Log.e(TAG, "Failed to send typing started: ${exception.message}")
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun deleteMessages(messageIds: List<String>) {
        viewModelScope.launch {
            val currentUserId = getCurrentUserId()
            val currentUserName = getCurrentUserName()

            messageIds.forEach { messageId ->
                val messageIdLong = messageId.toLongOrNull()
                if (messageIdLong != null) {
                    try {
                        deleteMessageUseCase(messageIdLong, getAuthToken()).onSuccess {
                            updateMessageAsDeleted(messageId, currentUserName)
                        }.onFailure { exception ->
                            Log.e(TAG, "Failed to delete message: ${exception.message}")
                            _error.value = "Failed to delete message: ${exception.message}"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception while deleting message: ${e.message}", e)
                        _error.value = "Failed to delete message: ${e.message}"
                    }
                }
            }
        }
    }

    fun editMessage(messageId: String, newText: String) {
        viewModelScope.launch {
            val messageIdLong = messageId.toLongOrNull()
            if (messageIdLong != null) {
                try {
                    val socketResult = editMessageOverSocketUseCase(
                        groupCode = currentGroupCode,
                        messageId = messageIdLong,
                        newMessage = newText,
                        authToken = getAuthToken()
                    )
                    socketResult.onSuccess {
                        updateMessageAsEdited(messageId, newText, true)
                        return@launch
                    }.onFailure { ex ->
                        Log.w(TAG, "WebSocket edit failed, falling back to HTTP: ${ex.message}")
                    }

                    editMessageUseCase(messageIdLong, newText, getAuthToken()).onSuccess {
                        updateMessageAsEdited(messageId, newText, true)
                    }.onFailure { exception ->
                        Log.e(TAG, "❌ Failed to edit message: ${exception.message}")
                        _error.value = "Failed to edit message: ${exception.message}"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Exception while editing message: ${e.message}", e)
                    _error.value = "Failed to edit message: ${e.message}"
                }
            }
        }
    }

    private fun updateMessageAsDeleted(messageId: String, deletedByName: String) {
        _uiState.update { currentState ->
            val updatedMessages = currentState.initialMessages.map { message ->
                if (message.id == messageId) {
                    message.copy(
                        isDeleted = true, deletedByName = deletedByName
                    )
                } else {
                    message
                }
            }
            currentState.copy(initialMessages = updatedMessages)
        }

        _allMessages.update { currentMessages ->
            currentMessages.map { message ->
                if (message.id == messageId) {
                    message.copy(
                        isDeleted = true, deletedByName = deletedByName
                    )
                } else {
                    message
                }
            }
        }
    }

    private fun updateMessageAsEdited(
        messageId: String, newMessage: String, isEdited: Boolean = false
    ) {
        _uiState.update { currentState ->
            val updatedMessages = currentState.initialMessages.map { message ->
                if (message.id == messageId) {
                    message.copy(
                        content = newMessage, isEdited = isEdited
                    )
                } else {
                    message
                }
            }
            currentState.copy(initialMessages = updatedMessages)
        }

        _allMessages.update { currentMessages ->
            currentMessages.map { message ->
                if (message.id == messageId) {
                    message.copy(
                        content = newMessage, isEdited = isEdited
                    )
                } else {
                    message
                }
            }
        }
    }

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val time = timestamp.toLongOrNull()
            if (time != null) {
                val date = java.util.Date(time)
                java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(date)
            } else {
                try {
                    val isoFormatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    val localDateTime = java.time.LocalDateTime.parse(timestamp, isoFormatter)
                    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                    localDateTime.format(timeFormatter)
                } catch (e: Exception) {
                    timestamp
                }
            }
        } catch (e: Exception) {
            timestamp
        }
    }

    private fun resolveReplyInfo(
        replyToMessageId: Long?, allMessages: List<DomainChatMessage>
    ): Pair<String?, String?> {
        if (replyToMessageId == null) return null to null

        val replyMessage = allMessages.find { it.id == replyToMessageId }
        return if (replyMessage != null) {
            replyMessage.senderName to replyMessage.message
        } else {
            null to null
        }
    }

    private fun resolveReplyInfoFromUi(
        replyToMessageId: Long?, allUiMessages: List<UiChatMessage>
    ): Pair<String?, String?> {
        if (replyToMessageId == null) return null to null

        val replyMessage = allUiMessages.find { it.id.toLongOrNull() == replyToMessageId }
        return if (replyMessage != null) {
            replyMessage.authorName to replyMessage.content
        } else {
            Log.e(TAG, "Could not find reply message with ID: $replyToMessageId")
            null to null
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketJob?.cancel()
        connectToChatUseCase.disconnect()
    }
}

const val TAG = "ChatViewModel"