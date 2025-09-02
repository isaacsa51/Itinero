package com.serranoie.itinero.core.data.remote.resources

import com.serranoie.itinero.core.data.BuildConfig
import com.serranoie.itinero.core.data.remote.dto.fcm.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class FCMApiImpl(
    private val httpClient: HttpClient
) : FCMApi {

    // Using a placeholder base URL. In a real app, this would come from build configs.
    private val baseUrl = BuildConfig.API_BASE_URL

    override suspend fun saveFCMToken(request: SaveFCMTokenRequestDto): FCMTokenResponseDto {
        return httpClient.post("$baseUrl/fcm-tokens") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getFCMTokens(): GetFCMTokensResponseDto {
        return httpClient.get("$baseUrl/fcm-tokens").body()
    }

    override suspend fun deactivateFCMToken(token: String): FCMTokenResponseDto {
        return httpClient.delete("$baseUrl/fcm-tokens/$token").body()
    }

    override suspend fun sendNotification(request: SendNotificationRequestDto): NotificationResponseDto {
        return httpClient.post("$baseUrl/notifications/send") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun subscribeToTopic(request: TopicSubscriptionRequestDto): TopicSubscriptionResponseDto {
        return httpClient.post("$baseUrl/notifications/subscribe-topic") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun unsubscribeFromTopic(request: TopicSubscriptionRequestDto): TopicSubscriptionResponseDto {
        return httpClient.post("$baseUrl/notifications/unsubscribe-topic") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
