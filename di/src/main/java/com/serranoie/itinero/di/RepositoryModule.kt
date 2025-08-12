package com.serranoie.itinero.di

import com.serranoie.app.feature.itinerary.data.remote.ItineraryApi
import com.serranoie.app.feature.itinerary.data.remote.ItineraryApiImpl
import com.serranoie.app.feature.itinerary.data.remote.repository.ItineraryRepositoryImpl
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.app.feature.expenses.data.remote.ExpensesApi
import com.serranoie.app.feature.expenses.data.remote.ExpensesApiImpl
import com.serranoie.app.feature.expenses.data.remote.repository.ExpensesRepositoryImpl
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.data.remote.resources.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.resources.UnauthorizedHandler
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.usecase.LogoutObserverUseCase
import com.serranoie.app.feature.chat.data.remote.api.ChatApiService
import com.serranoie.app.feature.chat.data.remote.websocket.ChatWebSocketService
import com.serranoie.app.feature.chat.data.repository.ChatRepositoryImpl
import com.serranoie.app.feature.chat.domain.repository.ChatRepository
import com.serranoie.app.feature.chat.data.BuildConfig as ChatBuildConfig
import org.koin.dsl.module

val repositoryModule = module {
    single<ItineroApi> { ItineroApiImpl(get()) }
    single<ItineraryApi> { ItineraryApiImpl(get()) }
    single<ExpensesApi> { ExpensesApiImpl(get()) }

    single {
        ChatApiService(
            httpClient = get(),
            baseUrl = ChatBuildConfig.API_BASE_URL
        )
    }
    single {
        ChatWebSocketService(
            httpClient = get(),
            baseUrl = ChatBuildConfig.WEBSOCKET_BASE_URL
        )
    }

    single<AuthRepository> {
        val authPrefs = get<AuthPreferencesRepository>()
        val logoutObserver = get<LogoutObserverUseCase>()

        UnauthorizedHandler.setAuthTokenClearer {
            authPrefs.clearToken()
            authPrefs.clearLoginStatus()
            authPrefs.saveLoginStatus(false)
        }
        UnauthorizedHandler.setLogoutObserver(logoutObserver)

        AuthRepositoryImpl(get(), authPrefs)
    }
    single<TravelRepository> {
        TravelRepositoryImpl(
            get(),
            get()
        )
    }
    single<ExpensesRepository>{
        ExpensesRepositoryImpl(
            get(),
            get()
        )
    }

    single<ItineraryRepository> {
        ItineraryRepositoryImpl(
            get(),
            get()
        )
    }

    single<ChatRepository> {
        ChatRepositoryImpl(
            apiService = get(),
            webSocketService = get()
        )
    }
}
