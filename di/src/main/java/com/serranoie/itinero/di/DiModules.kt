package com.serranoie.itinero.di

import androidx.room.Room
import com.serranoie.itinero.core.data.local.database.AppDatabase
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepositoryImpl
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import com.serranoie.itinero.core.domain.usecase.CreateTravelUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllTravelsUseCase
import com.serranoie.itinero.core.domain.usecase.GetAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.GetTravelByIdUseCase
import com.serranoie.itinero.core.domain.usecase.JoinTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LoginUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutUseCase
import com.serranoie.itinero.core.domain.usecase.RegisterUseCase
import com.serranoie.itinero.core.domain.usecase.SaveAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import com.serranoie.itinero.core.domain.usecase.UpdateTripInfoUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val diModules = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "itinero_database"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().tripDao() }

    // API
    single<ItineroApi> { ItineroApiImpl(get()) }

    // Local Repository (Room database)
    single<LocalTravelRepository> { LocalTravelRepositoryImpl(get()) }

    // Preferences
    single { AuthPreferences(androidContext()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<TravelRepository> {
        TravelRepositoryImpl(
            get(), // ItineroApi
            get()  // LocalTravelRepository
        )
    }

    // Individual Use Cases
    factory { GetAllTravelsUseCase(get()) }
    factory { GetTravelByIdUseCase(get()) }
    factory { JoinTravelUseCase(get()) }
    factory { LeaveTravelUseCase(get()) }
    factory { CreateTravelUseCase(get()) }
    factory { UpdateTripInfoUseCase(get()) }

    // Combined Use Case
    factory {
        TravelUseCase(
            getAllTravels = get(),
            getTravelById = get(),
            joinTravel = get(),
            leaveTravel = get(),
            createTravel = get(),
            updateTripInfo = get(),
        )
    }

    // Auth use cases
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetAuthTokenUseCase(get()) }
    factory { SaveAuthTokenUseCase(get()) }
    factory { LogoutUseCase(get()) }

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
