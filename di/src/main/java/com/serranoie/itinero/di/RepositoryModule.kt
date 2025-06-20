package com.serranoie.itinero.di

import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepository
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepositoryImpl
import com.serranoie.app.feature.itinerary.data.remote.ItineraryApi
import com.serranoie.app.feature.itinerary.data.remote.ItineraryApiImpl
import com.serranoie.app.feature.itinerary.data.remote.repository.ItineraryRepositoryImpl
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepositoryImpl
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ItineroApi> { ItineroApiImpl(get()) }
    single<ItineraryApi> { ItineraryApiImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<TravelRepository> {
        TravelRepositoryImpl(
            get(), // ItineroApi
            get()  // LocalTravelRepository
        )
    }
    single<ItineraryRepository> {
        ItineraryRepositoryImpl(
            get(), // ItineraryApi
            get()  // LocalItineraryRepository
        )
    }

    // Local Repositories
    single<LocalTravelRepository> { LocalTravelRepositoryImpl(get()) }
    single<LocalItineraryRepository> { LocalItineraryRepositoryImpl(get()) }
}