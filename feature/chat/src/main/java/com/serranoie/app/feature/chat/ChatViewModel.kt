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
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage as UiChatMessage
import com.serranoie.app.feature.chat.domain.model.ChatMessage as DomainChatMessage
import com.serranoie.app.feature.chat.domain.model.MessageType
import com.serranoie.app.feature.chat.domain.usecase.ConnectToChatUseCase
import com.serranoie.app.feature.chat.domain.usecase.GetMessagesUseCase
import com.serranoie.app.feature.chat.domain.usecase.SendMessageUseCase
import com.serranoie.app.feature.chat.domain.repository.ChatEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val connectToChatUseCase: ConnectToChatUseCase,
    private val getCurrentUserId: () -> String,
    private val getCurrentUserName: () -> String,
    private val getAuthToken: () -> String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatScreenUiState(
            channelName = "",
            channelMembers = 0,
            initialMessages = emptyList()
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

    fun initializeChat(groupCode: String, groupName: String, memberCount: Int) {
        currentGroupCode = groupCode

        _uiState.update { currentState ->
            currentState.copy(
                channelName = groupName,
                channelMembers = memberCount,
                initialMessages = emptyList()
            )
        }

        loadMessages(groupCode)
        connectToRealTimeChat(groupCode)
    }

    private fun loadMessages(groupCode: String, limit: Int = 50, offset: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            getMessagesUseCase(groupCode, getAuthToken(), limit, offset)
                .onSuccess { messages ->
                    val uiMessages = messages.map { domainMessage ->
                        UiChatMessage(
                            id = domainMessage.id.toString(),
                            content = domainMessage.message,
                            authorId = domainMessage.senderId.toString(),
                            authorName = domainMessage.senderName,
                            timestamp = formatTimestamp(domainMessage.timestamp),
                            rawTimestamp = domainMessage.timestamp // Pass through original timestamp
                        )
                    }

                    _uiState.update { currentState ->
                        currentState.copy(initialMessages = uiMessages)
                    }
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = "Failed to load messages: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    private fun connectToRealTimeChat(groupCode: String) {
        webSocketJob?.cancel()
        connectToChatUseCase.disconnect()

        webSocketJob = viewModelScope.launch {
            try {
                _isConnected.value = false

                connectToChatUseCase(groupCode, getAuthToken())
                    .catch { exception ->
                        _isConnected.value = false
                        _error.value = "Connection failed: ${exception.message}"
                    }
                    .collect { event ->
                        when (event) {
                            is ChatEvent.MessageReceived -> {
                                if (!_isConnected.value) {
                                    _isConnected.value = true
                                    _error.value = null
                                }

                                val domainMessage = event.message
                                val newUiMessage = UiChatMessage(
                                    id = domainMessage.id.toString(),
                                    content = domainMessage.message,
                                    authorId = domainMessage.senderId.toString(),
                                    authorName = domainMessage.senderName,
                                    timestamp = formatTimestamp(domainMessage.timestamp),
                                    rawTimestamp = domainMessage.timestamp // Pass through original timestamp
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

                            is ChatEvent.UserJoined -> {
                                // Handle user joined - could show a system message or update member count
                            }

                            is ChatEvent.UserLeft -> {
                                // Handle user left - could show a system message or update member count
                            }
                        }
                    }
            } catch (e: Exception) {
                _isConnected.value = false
                _error.value = "WebSocket error: ${e.message}"
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || currentGroupCode.isBlank()) {
            return
        }

        val currentUserId = getCurrentUserId()
        val message = DomainChatMessage(
            id = 0L,
            groupCode = currentGroupCode,
            senderId = currentUserId.toLongOrNull() ?: 0L, 
            senderName = getCurrentUserName(),
            message = content.trim(),
            messageType = MessageType.TEXT,
            timestamp = System.currentTimeMillis().toString(),
            isEdited = false,
            replyToMessageId = null
        )

        viewModelScope.launch {
            sendMessageUseCase(message, getAuthToken())
                .onSuccess {
                }
                .onFailure { exception ->
                    _error.value = "Failed to send message: ${exception.message}"
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
                    isTyping = false,
                    groupCode = currentGroupCode,
                    authToken = getAuthToken()
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
                    isTyping = true,
                    groupCode = currentGroupCode,
                    authToken = getAuthToken()
                ).onFailure { exception ->
                    Log.e(TAG, "Failed to send typing started: ${exception.message}")
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Temporary test method - remove after server is fixed
    fun testTypingIndicator(userName: String, isTyping: Boolean) {
        if (isTyping) {
            _typingUsers.update { typingSet ->
                val newSet = typingSet + userName
                newSet
            }
        } else {
            _typingUsers.update { typingSet ->
                val newSet = typingSet - userName
                newSet
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

    override fun onCleared() {
        super.onCleared()
        webSocketJob?.cancel()
        connectToChatUseCase.disconnect()
    }
}

const val TAG = "ChatViewModel"