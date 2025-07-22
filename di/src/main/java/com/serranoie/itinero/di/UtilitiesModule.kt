package com.serranoie.itinero.di

import com.serranoie.app.designsystemlib.ui.network.NetworkObserver
import com.serranoie.core.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilitiesModule = module {
    single { NetworkObserver(androidContext()) }
    factory { SettingsViewModel(androidContext()) }
}