package com.serranoie.itinero.di

import com.serranoie.app.feature.itinerary.data.remote.ItineraryApi
import com.serranoie.app.feature.itinerary.data.remote.ItineraryApiImpl
import com.serranoie.app.feature.itinerary.data.remote.repository.ItineraryRepositoryImpl
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.app.feature.expenses.data.remote.ExpensesApi
import com.serranoie.app.feature.expenses.data.remote.ExpensesApiImpl
import com.serranoie.app.feature.expenses.data.remote.repository.ExpensesRepositoryImpl
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.data.remote.resources.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Remote APIs
    single<ItineroApi> { ItineroApiImpl(get()) }
    single<ItineraryApi> { ItineraryApiImpl(get()) }
    single<ExpensesApi> { ExpensesApiImpl(get()) }

    // Repository Implementations
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<TravelRepository> {
        TravelRepositoryImpl(
            get(), // ItineroApi
            get()  // LocalTravelRepository
        )
    }
    single<ExpensesRepository>{
        ExpensesRepositoryImpl(
            get(), // ExpensesApi
            get()  // LocalExpensesRepository
        )
    }

    single<ItineraryRepository> {
        ItineraryRepositoryImpl(
            get(), // ItineraryApi
            get()  // LocalItineraryRepository
        )
    }
}
