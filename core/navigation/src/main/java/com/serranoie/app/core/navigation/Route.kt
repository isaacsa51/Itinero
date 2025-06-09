package com.serranoie.app.core.navigation

sealed class Route(val route: String) {

    data object AppStartNavigation : Route("appStartNavigation")

    data object HomeNavigation : Route("homeNavigation")
    data object Home : Route("HOME/{tripId}") {
        fun createRoute(tripId: String) = "HOME/$tripId"
    }

    data object Welcome : Route(Screen.WELCOME.name)
    data object Itinerary : Route(Screen.ITINERARY.name)
    data object Expenses : Route(Screen.EXPENSES.name)
    data object AddExpense : Route(Screen.ADD_EXPENSE.name)
    data object ExpenseDetails : Route(Screen.EXPENSE_DETAILS.name)
    data object Chat : Route(Screen.CHAT.name)

    data object TripSettings : Route("TRIP_SETTINGS/{tripId}") {
        fun createRoute(tripId: String, scrollTo: String? = null): String {
            return "trip_settings/$tripId?scrollTo=${scrollTo ?: ""}"
        }
    }
    data object TripInfo : Route("TRIP_INFO/{tripId}") {
        fun createRoute(tripId: String): String {
            return "trip_info/$tripId"
        }
    }

    data object WelcomeNavigation : Route("welcomeNavigation")
    data object TravelList : Route(Screen.TRAVEL_LIST.name)
    data object CreateTravel : Route(Screen.CREATE_TRAVEL.name)
    data object JoinTrip : Route(Screen.JOIN_TRIP.name)
    data object CameraScanner : Route(Screen.CAMERA_SCANNER.name)

    data object SettingsNavigation : Route("settingsNavigation")

    data object AuthNavigation : Route("authNavigation")
    data object Authentication : Route(Screen.AUTH.name)
    data object Register: Route(Screen.REGISTER.name)
    data object ForgotPassword: Route(Screen.FORGOT_PASSWORD.name)

    data object Onboarding : Route(Screen.ONBOARDING.name)

}
