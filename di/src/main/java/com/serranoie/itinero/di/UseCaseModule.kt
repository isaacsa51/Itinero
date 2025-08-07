package com.serranoie.itinero.di

import com.serranoie.app.feature.expenses.domain.usecase.AddExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.DeleteExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.ExpensesUseCases
import com.serranoie.app.feature.expenses.domain.usecase.GetExpenseByIdUseCase
import com.serranoie.app.feature.expenses.domain.usecase.GetUserExpensesUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsPaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsUnpaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.UpdateExpenseUseCase
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
import com.serranoie.itinero.core.domain.usecase.DeleteAccountUseCase
import com.serranoie.itinero.core.domain.usecase.DeleteTripUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllMembersUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllTravelsUseCase
import com.serranoie.itinero.core.domain.usecase.GetAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.GetCurrentUserMembershipStatusUseCase
import com.serranoie.itinero.core.domain.usecase.GetTravelByIdUseCase
import com.serranoie.itinero.core.domain.usecase.GetTripOverviewUseCase
import com.serranoie.itinero.core.domain.usecase.JoinTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTripUseCase
import com.serranoie.itinero.core.domain.usecase.LoginUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutObserverUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutUseCase
import com.serranoie.itinero.core.domain.usecase.MakeOwnerUseCase
import com.serranoie.itinero.core.domain.usecase.RegisterUseCase
import com.serranoie.itinero.core.domain.usecase.RejectMemberUseCase
import com.serranoie.itinero.core.domain.usecase.RemoveMemberUseCase
import com.serranoie.itinero.core.domain.usecase.SaveAuthTokenUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import com.serranoie.itinero.core.domain.usecase.UpdateTripInfoUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { LogoutObserverUseCase() }

    factory { GetAllTravelsUseCase(get()) }
    factory { GetTravelByIdUseCase(get()) }
    factory { JoinTravelUseCase(get()) }
    factory { LeaveTravelUseCase(get()) }
    factory { CreateTravelUseCase(get()) }
    factory { UpdateTripInfoUseCase(get()) }
    factory { AcceptMemberToTripUseCase(get()) }
    factory { GetAllMembersUseCase(get()) }
    factory { RejectMemberUseCase(get()) }
    factory { RemoveMemberUseCase(get()) }
    factory { MakeOwnerUseCase(get()) }
    factory { GetCurrentUserMembershipStatusUseCase(get()) }
    factory { LeaveTripUseCase(get()) }
    factory { GetTripOverviewUseCase(get()) }
    factory { DeleteTripUseCase(get()) }

    factory { GetAllActivitiesUseCase(get()) }
    factory { GetActivityByIdUseCase(get()) }
    factory { CreateActivityUseCase(get()) }
    factory { DeleteActivityByIdUseCase(get()) }
    factory { UpdateActivityInfoUseCase(get()) }
    factory { ToggleActivityCompletionUseCase(get()) }

    factory { GetUserExpensesUseCase(get()) }
    factory { GetExpenseByIdUseCase(get()) }
    factory { AddExpenseUseCase(get()) }
    factory { UpdateExpenseUseCase(get()) }
    factory { DeleteExpenseUseCase(get()) }
    factory { MarkDebtorAsPaidUseCase(get()) }
    factory { MarkDebtorAsUnpaidUseCase(get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetAuthTokenUseCase(get()) }
    factory { SaveAuthTokenUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { DeleteAccountUseCase(get()) }

    factory {
        TravelUseCase(
            getAllTravels = get(),
            getTravelById = get(),
            joinTravel = get(),
            leaveTravel = get(),
            createTravel = get(),
            updateTripInfo = get(),
            acceptMemberToTrip = get(),
            getAllMembers = get(),
            rejectMember = get(),
            removeMember = get(),
            makeOwner = get(),
            getCurrentUserMembershipStatus = get(),
            leaveTrip = get(),
            getTripOverview = get(),
            deleteTrip = get()
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
            logout = get(),
            deleteAccountUseCase = get()
        )
    }

    factory {
        ExpensesUseCases(
            getUserExpensesUseCase = get(),
            getExpenseByIdUseCase = get(),
            addExpenseUseCase = get(),
            updateExpenseUseCase = get(),
            deleteExpenseUseCase = get(),
            markDebtorAsPaidUseCase = get(),
            markDebtorAsUnpaidUseCase = get(),
        )
    }
}
