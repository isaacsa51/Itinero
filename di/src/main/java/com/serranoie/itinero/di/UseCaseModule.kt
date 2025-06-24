package com.serranoie.itinero.di

import com.serranoie.app.feature.itinerary.domain.usecase.CreateActivityUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.DeleteActivityByIdUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.GetActivityByIdUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.GetAllActivitiesUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.ItineraryUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.ToggleActivityCompletionUseCase
import com.serranoie.app.feature.itinerary.domain.usecase.UpdateActivityInfoUseCase
import com.serranoie.itinero.core.domain.usecase.AcceptMemberToTripUseCase
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import com.serranoie.itinero.core.domain.usecase.CreateTravelUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllMembersUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllTravelsUseCase
import com.serranoie.itinero.core.domain.usecase.GetAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.GetTravelByIdUseCase
import com.serranoie.itinero.core.domain.usecase.JoinTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTripUseCase
import com.serranoie.itinero.core.domain.usecase.LoginUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutUseCase
import com.serranoie.itinero.core.domain.usecase.MakeOwnerUseCase
import com.serranoie.itinero.core.domain.usecase.RegisterUseCase
import com.serranoie.itinero.core.domain.usecase.RejectMemberUseCase
import com.serranoie.itinero.core.domain.usecase.RemoveMemberUseCase
import com.serranoie.itinero.core.domain.usecase.SaveAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import com.serranoie.itinero.core.domain.usecase.UpdateTripInfoUseCase
import com.serranoie.itinero.core.domain.usecase.GetCurrentUserMembershipStatusUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTravelsUseCase(get()) }
    factory { GetTravelByIdUseCase(get()) }
    factory { JoinTravelUseCase(get()) }
    factory { LeaveTravelUseCase(get()) }
    factory { CreateTravelUseCase(get()) }
    factory { UpdateTripInfoUseCase(get()) }
    factory { AcceptMemberToTripUseCase(get()) }

    // Additional member management use cases
    factory { GetAllMembersUseCase(get()) }
    factory { RejectMemberUseCase(get()) }
    factory { RemoveMemberUseCase(get()) }
    factory { MakeOwnerUseCase(get()) }
    factory { GetCurrentUserMembershipStatusUseCase(get()) }
    factory { LeaveTripUseCase(get()) }

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
            acceptMemberToTrip = get(),
            // Additional member management use cases
            getAllMembers = get(),
            rejectMember = get(),
            removeMember = get(),
            makeOwner = get(),
            getCurrentUserMembershipStatus = get(),
            leaveTrip = get(),
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
