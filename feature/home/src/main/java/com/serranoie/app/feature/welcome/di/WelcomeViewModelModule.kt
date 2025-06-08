package com.serranoie.app.feature.welcome.di

import com.serranoie.app.feature.SharedTravelViewModel
import com.serranoie.app.feature.TravelListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val welcomeViewModelModule = module {
    viewModel { SharedTravelViewModel(get()) }
    viewModel { TravelListViewModel(get()) }
}
