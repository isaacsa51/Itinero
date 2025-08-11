/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: chatViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.chat.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 agosto 2025
 */

package com.serranoie.app.feature.chat.di

import com.serranoie.app.feature.chat.ChatViewModel
import com.serranoie.itinero.core.domain.usecase.GetAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.GetCurrentUserIdUseCase
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatViewModelModule = module {
    viewModel {
        ChatViewModel(
            getMessagesUseCase = get(),
            sendMessageUseCase = get(),
            connectToChatUseCase = get(),
            getCurrentUserId = {
                val getCurrentUserIdUseCase = get<GetCurrentUserIdUseCase>()
                getCurrentUserIdUseCase() ?: "unknown_user"
            },
            getCurrentUserName = {
                val authPreferencesRepository =
                    get<com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository>()
                authPreferencesRepository.getUserName() ?: "You"
            },
            getAuthToken = {
                val authUseCase: GetAuthTokenUseCase = get()
                runCatching {
                    runBlocking {
                        authUseCase.invoke() ?: ""
                    }
                }.getOrElse { "" }
            }
        )
    }
}
