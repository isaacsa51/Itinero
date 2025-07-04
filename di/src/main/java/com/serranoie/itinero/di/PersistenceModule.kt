package com.serranoie.itinero.di

import androidx.room.Room
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepository
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepositoryImpl
import com.serranoie.itinero.core.data.local.database.AppDatabase
import com.serranoie.itinero.core.data.local.persistence.AuthPreferencesRepositoryImpl
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistenceModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "itinero_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().tripDao() }
    single { get<AppDatabase>().itineraryDao() }
    single { get<AppDatabase>().expenseDao()}

    single<AuthPreferencesRepository> { AuthPreferencesRepositoryImpl(androidContext()) }

    single<LocalTravelRepository> { LocalTravelRepositoryImpl(get()) }
    single<LocalItineraryRepository> { LocalItineraryRepositoryImpl(get()) }
}
