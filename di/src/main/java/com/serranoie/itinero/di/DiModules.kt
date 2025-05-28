package com.serranoie.itinero.di

import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val diModules = module {
    // API
    single<ItineroApi> { ItineroApiImpl(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<TravelRepository> { TravelRepositoryImpl(get(), get()) }

    // Preferences
    single { AuthPreferences(androidContext()) }

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

    factory {
        TravelUseCase(
            getAllTravels = get(),
            getTravelById = get(),
            joinTravel = get(),
            leaveTravel = get(),
            createTravel = get()
        )
    }
}
