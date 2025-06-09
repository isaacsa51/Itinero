/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: HomeViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.home.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 June 2025
 */

package com.serranoie.app.feature.di

import com.serranoie.app.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeViewModelModule = module {
    viewModel { (groupCode: String) -> HomeViewModel(get(), groupCode) }
}
