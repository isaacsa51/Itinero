/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: FirebaseCMApi.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - Last edited: 27 agosto 2025
 */

package com.serranoie.itinero.core.data.remote.resources

import com.serranoie.itinero.core.data.remote.dto.fcm.FCMTokenResponseDto
import com.serranoie.itinero.core.data.remote.dto.fcm.GetFCMTokensResponseDto
import com.serranoie.itinero.core.data.remote.dto.fcm.NotificationResponseDto
import com.serranoie.itinero.core.data.remote.dto.fcm.SaveFCMTokenRequestDto
import com.serranoie.itinero.core.data.remote.dto.fcm.SendNotificationRequestDto
import com.serranoie.itinero.core.data.remote.dto.fcm.TopicSubscriptionRequestDto
import com.serranoie.itinero.core.data.remote.dto.fcm.TopicSubscriptionResponseDto

interface FirebaseCMApi {
    suspend fun saveFCMToken(request: SaveFCMTokenRequestDto): FCMTokenResponseDto
    suspend fun getFCMTokens(): GetFCMTokensResponseDto
    suspend fun deactivateFCMToken(token: String): FCMTokenResponseDto

    suspend fun sendNotification(request: SendNotificationRequestDto): NotificationResponseDto

    suspend fun subscribeToTopic(request: TopicSubscriptionRequestDto): TopicSubscriptionResponseDto
    suspend fun unsubscribeFromTopic(request: TopicSubscriptionRequestDto): TopicSubscriptionResponseDto
}