/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatDataModule.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 06 August 2025
 */

package com.serranoie.app.feature.chat.data.di

import com.serranoie.app.feature.chat.data.BuildConfig
import com.serranoie.app.feature.chat.data.remote.api.ChatApiService
import com.serranoie.app.feature.chat.data.remote.websocket.ChatWebSocketService
import com.serranoie.app.feature.chat.data.repository.ChatRepositoryImpl
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import org.koin.dsl.module

val chatDataModule = module {

    // API Service - reads endpoint from local.properties via BuildConfig
    single {
        ChatApiService(
            httpClient = get(), // HttpClient from NetworkModule
            baseUrl = BuildConfig.API_BASE_URL
        )
    }

    // WebSocket Service - reads endpoint from local.properties via BuildConfig
    single {
        ChatWebSocketService(
            httpClient = get(), // HttpClient from NetworkModule with WebSocket support
            baseUrl = BuildConfig.WEBSOCKET_BASE_URL
        )
    }

    // Repository Implementation
    single<ChatRepository> {
        ChatRepositoryImpl(
            apiService = get(),
            webSocketService = get()
        )
    }

    // Note: ChatMessageDao should be added to your main app database configuration
    // The DAO will be available through your existing Room database instance
}