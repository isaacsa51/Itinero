// Copyright (c) 2025 Isaac Serrano.
//
// File: FirebaseCMTokenDto.kt
// Project: Itinero
// Module: Itinero.core.data.main
//
// Last edited: 27 agosto 2025

package com.serranoie.itinero.core.data.remote.dto.fcm

import kotlinx.serialization.Serializable

@Serializable
data class SaveFCMTokenRequestDto(
    val fcmToken: String,
    val deviceId: String,
    val deviceType: String // "android", "ios", "web"
)

@Serializable
data class FCMTokenResponseDto(
    val success: Boolean,
    val message: String,
    val tokenId: Int? = null
)

@Serializable
data class GetFCMTokensResponseDto(
    val success: Boolean,
    val tokens: List<String>,
    val count: Int
)

@Serializable
data class SendNotificationRequestDto(
    val token: String? = null,
    val tokens: List<String>? = null,
    val topic: String? = null,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)

@Serializable
data class NotificationResponseDto(
    val success: Boolean,
    val message: String,
    val messageIds: List<String> = emptyList(),
    val failedTokens: List<String> = emptyList()
)

@Serializable
data class TopicSubscriptionRequestDto(
    val tokens: List<String>,
    val topic: String
)

@Serializable
data class TopicSubscriptionResponseDto(
    val success: Boolean,
    val message: String,
    val successCount: Int = 0, // Optional field with default value
    val failureCount: Int = 0  // Optional field with default value
)