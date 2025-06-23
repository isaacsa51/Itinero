/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripSettingsViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.app.feature.settings.di

import com.serranoie.app.itinero.feature.settings.trip.TripSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tripSettingsViewModelModule = module {
    viewModel { TripSettingsViewModel(get()) }
}