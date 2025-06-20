package com.serranoie.itinero.di

import androidx.room.Room
import com.serranoie.app.feature.itinerary.data.local.database.ItineraryDatabase
import com.serranoie.itinero.core.data.local.database.AppDatabase
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistenceModule = module {
    // Databases
    single {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "itinero_database"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        Room.databaseBuilder(
            androidContext(), ItineraryDatabase::class.java, "itinerary_database"
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<AppDatabase>().tripDao() }
    single { get<ItineraryDatabase>().itineraryDao() }

    // Preferences
    single { AuthPreferences(androidContext()) }
}