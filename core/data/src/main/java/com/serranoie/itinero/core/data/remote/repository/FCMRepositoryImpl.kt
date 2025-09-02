package com.serranoie.itinero.core.data.remote.repository

import android.util.Log
import com.serranoie.itinero.core.data.remote.dto.fcm.*
import com.serranoie.itinero.core.data.remote.resources.FCMApi
import com.serranoie.itinero.core.domain.model.DeviceType
import com.serranoie.itinero.core.domain.repository.FCMRepository
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FCMRepositoryImpl(
    private val fcmApi: FCMApi
) : FCMRepository {

    companion object {
        private const val TAG = "FCMRepositoryImpl"
    }

    override suspend fun saveFCMToken(
        fcmToken: String,
        deviceId: String,
        deviceType: DeviceType
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Saving FCM token for device: $deviceId, type: ${deviceType.value}")
            val request = SaveFCMTokenRequestDto(
                fcmToken = fcmToken,
                deviceId = deviceId,
                deviceType = deviceType.value
            )
            val response = fcmApi.saveFCMToken(request)
            Log.d(TAG, "FCM token save response: ${response.message}")

            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving FCM token", e)
            Result.Error(e)
        }
    }

    override suspend fun getFCMTokens(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Retrieving FCM tokens")
            val response = fcmApi.getFCMTokens()
            Log.d(TAG, "Retrieved ${response.tokens.size} FCM tokens")

            if (response.success) {
                Result.Success(response.tokens)
            } else {
                Result.Error(Exception("Failed to get FCM tokens"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting FCM tokens", e)
            Result.Error(e)
        }
    }

    override suspend fun deactivateFCMToken(token: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deactivating FCM token")
                val response = fcmApi.deactivateFCMToken(token)
                Log.d(TAG, "Token deactivation response: ${response.message}")

                if (response.success) {
                    Result.Success(true)
                } else {
                    Result.Error(Exception(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deactivating FCM token", e)
                Result.Error(e)
            }
        }

    override suspend fun subscribeToTopic(tokens: List<String>, topic: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Subscribing ${tokens.size} tokens to topic: $topic")
                val request = TopicSubscriptionRequestDto(tokens = tokens, topic = topic)
                val response = fcmApi.subscribeToTopic(request)

                Log.d(TAG, "Topic subscription response: ${response.message}")
                Log.d(
                    TAG,
                    "Success count: ${response.successCount}, Failure count: ${response.failureCount}"
                )

                if (response.success) {
                    Result.Success(true)
                } else {
                    Result.Error(Exception(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error subscribing to topic '$topic'", e)
                // Don't fail completely if topic subscription fails - it's not critical
                // Just log the error and return success to continue app functionality
                Log.w(
                    TAG,
                    "Topic subscription failed but continuing - notifications may not work for this topic"
                )
                Result.Success(false) // Return false success to indicate partial failure
            }
        }

    override suspend fun unsubscribeFromTopic(
        tokens: List<String>,
        topic: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Unsubscribing ${tokens.size} tokens from topic: $topic")
            val request = TopicSubscriptionRequestDto(tokens = tokens, topic = topic)
            val response = fcmApi.unsubscribeFromTopic(request)

            Log.d(TAG, "Topic unsubscription response: ${response.message}")
            Log.d(
                TAG,
                "Success count: ${response.successCount}, Failure count: ${response.failureCount}"
            )

            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from topic '$topic'", e)
            // Don't fail completely if topic unsubscription fails
            Log.w(TAG, "Topic unsubscription failed but continuing")
            Result.Success(false) // Return false success to indicate partial failure
        }
    }
}