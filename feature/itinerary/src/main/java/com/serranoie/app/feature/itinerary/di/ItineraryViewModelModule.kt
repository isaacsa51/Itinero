/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 20 junio 2025
 */

package com.serranoie.app.feature.itinerary.di

import com.serranoie.app.feature.itinerary.ItineraryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val itineraryViewModelModule = module {
    viewModel { (groupCode: String) -> ItineraryViewModel(get(), groupCode) }
}