package com.serranoie.itinero.di

import com.serranoie.app.designsystemlib.ui.network.NetworkObserver
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.itinero.core.data.notification.NotificationRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.FCMRepositoryImpl
import com.serranoie.itinero.core.data.remote.resources.FCMApi
import com.serranoie.itinero.core.data.remote.resources.FCMApiImpl
import com.serranoie.itinero.core.domain.repository.FCMRepository
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val utilitiesModule = module {
    single { NetworkObserver(androidContext()) }

    // FCM API
    single<FCMApi> {
        FCMApiImpl(httpClient = get())
    }

    // FCM Repository
    single<FCMRepository> {
        FCMRepositoryImpl(fcmApi = get())
    }

    // Notification Repository
    single<NotificationRepository> {
    NotificationRepositoryImpl(context = androidContext())
    }

    viewModel { SettingsViewModel(androidContext(), get(), get()) }
}
