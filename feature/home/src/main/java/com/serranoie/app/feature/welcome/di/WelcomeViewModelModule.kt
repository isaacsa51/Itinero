package com.serranoie.app.feature.welcome.di

import com.serranoie.app.feature.TravelViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val welcomeViewModelModule = module {
    viewModel { TravelViewModel(get()) }
}