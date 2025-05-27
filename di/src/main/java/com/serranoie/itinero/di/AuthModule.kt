package com.serranoie.itinero.di

import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import com.serranoie.itinero.core.domain.usecase.GetAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.LoginUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutUseCase
import com.serranoie.itinero.core.domain.usecase.RegisterUseCase
import com.serranoie.itinero.core.domain.usecase.SaveAuthTokenUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    // API
    single<ItineroApi> { ItineroApiImpl(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Preferences
    single { AuthPreferences(androidContext()) }

    // Use Cases
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetAuthTokenUseCase(get()) }
    factory { SaveAuthTokenUseCase(get()) }
    factory { LogoutUseCase(get()) }

    // Combined Use Case
    factory {
        AuthUseCase(
            login = get(),
            register = get(),
            getAuthToken = get(),
            saveAuthToken = get(),
            logout = get()
        )
    }
}
