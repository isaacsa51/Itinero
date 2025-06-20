package com.serranoie.itinero.di

import com.serranoie.app.feature.itinerary.domain.usecase.CreateActivityUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.DeleteActivityByIdUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.GetActivityByIdUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.GetAllActivitiesUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.ItineraryUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.ToggleActivityCompletionUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.UpdateActivityInfoUseCase
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
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTravelsUseCase(get()) }
    factory { GetTravelByIdUseCase(get()) }
    factory { JoinTravelUseCase(get()) }
    factory { LeaveTravelUseCase(get()) }
    factory { CreateTravelUseCase(get()) }
    factory { UpdateTripInfoUseCase(get()) }

    factory { GetAllActivitiesUseCase(get()) }
    factory { GetActivityByIdUseCase(get()) }
    factory { CreateActivityUseCase(get()) }
    factory { DeleteActivityByIdUseCase(get()) }
    factory { UpdateActivityInfoUseCase(get()) }
    factory { ToggleActivityCompletionUseCase(get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetAuthTokenUseCase(get()) }
    factory { SaveAuthTokenUseCase(get()) }
    factory { LogoutUseCase(get()) }

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

    factory {
        ItineraryUseCase(
            getAllActivitiesUseCase = get(),
            getActivityByIdUseCase = get(),
            createActivityUseCase = get(),
            deleteActivityByIdUseCase = get(),
            updateActivityInfoUseCase = get(),
            toggleActivityCompletionUseCase = get()
        )
    }

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