package com.serranoie.app.feature.auth.di

import com.serranoie.app.feature.auth.ui.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authViewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
}
