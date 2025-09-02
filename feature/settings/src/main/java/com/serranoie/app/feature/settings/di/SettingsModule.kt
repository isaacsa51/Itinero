/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SettingsModule.kt
 - Project: Itinero
 - Module: Itinero.feature.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.app.feature.settings.di

import com.serranoie.app.feature.settings.language.LanguageSettingsViewModel
import com.serranoie.app.feature.settings.trip.TripSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { TripSettingsViewModel(get()) }
    viewModel { LanguageSettingsViewModel(get(), get()) }
}