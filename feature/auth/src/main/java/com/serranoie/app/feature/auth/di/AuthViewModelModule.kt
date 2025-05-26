package com.serranoie.app.feature.auth.di

import com.serranoie.app.feature.auth.ui.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authViewModelModule = module {
    viewModel { AuthViewModel(get()) }
}
