/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: WelcomeViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.home.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 June 2025
 */

package com.serranoie.app.feature.di

import com.serranoie.app.feature.SharedTravelViewModel
import com.serranoie.app.feature.TravelListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val welcomeViewModelModule = module {
    viewModel { SharedTravelViewModel(get()) }
    viewModel { TravelListViewModel(get()) }
}
