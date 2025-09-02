/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: FCMRepository.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - Last edited: 27 agosto 2025
 */

package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.DeviceType
import com.serranoie.itinero.core.domain.result.Result

interface FCMRepository {
    suspend fun saveFCMToken(
        fcmToken: String, deviceId: String, deviceType: DeviceType
    ): Result<Boolean>

    suspend fun getFCMTokens(): Result<List<String>>

    suspend fun deactivateFCMToken(token: String): Result<Boolean>

    suspend fun subscribeToTopic(tokens: List<String>, topic: String): Result<Boolean>

    suspend fun unsubscribeFromTopic(tokens: List<String>, topic: String): Result<Boolean>
}