package com.serranoie.itinero.di

import androidx.room.Room
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepository
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepositoryImpl
import com.serranoie.itinero.core.data.local.database.AppDatabase
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistenceModule = module {
    // Single Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "itinero_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<AppDatabase>().tripDao() }
    single { get<AppDatabase>().itineraryDao() }

    // Preferences
    single { AuthPreferences(androidContext()) }

    // Local Repositories
    single<LocalTravelRepository> { LocalTravelRepositoryImpl(get()) }
    single<LocalItineraryRepository> { LocalItineraryRepositoryImpl(get()) }
}
