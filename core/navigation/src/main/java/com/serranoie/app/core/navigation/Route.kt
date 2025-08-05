package com.serranoie.app.core.navigation

import androidx.navigation.NavBackStackEntry

sealed class Route(val route: String) {

    data object AppStartNavigation : Route("appStartNavigation")

    data object HomeNavigation : Route("homeNavigation")
    data object Home : Route("HOME/{tripId}") {
        fun createRoute(tripId: String, userName: String = "", userStatus: String = ""): String {
            return "HOME/$tripId?userName=$userName&userStatus=$userStatus"
        }
    }

    data object Welcome : Route(Screen.WELCOME.name)
    data object Itinerary : Route(Screen.ITINERARY.name)
    data object AddItinerary : Route(Screen.ADD_ITINERARY.name)
    data object EditItinerary : Route("EDIT_ITINERARY/{itemId}") {
        fun createRoute(itemId: String) = "EDIT_ITINERARY/$itemId"
        fun itemIdFromRoute(backStackEntry: NavBackStackEntry): String? {
            return backStackEntry.arguments?.getString("itemId")
        }
    }
    data object Expenses : Route(Screen.EXPENSES.name)
    data object AddExpense : Route(Screen.ADD_EXPENSE.name)
    data object ExpenseDetails : Route("EXPENSE_DETAILS/{expenseId}") {
        fun createRoute(expenseId: String) = "EXPENSE_DETAILS/$expenseId"
        fun expenseIdFromRoute(backStackEntry: NavBackStackEntry): String? {
            return backStackEntry.arguments?.getString("expenseId")
        }
    }
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

    //data object SettingsNavigation : Route("settingsNavigation")
    data object Settings : Route(Screen.SETTINGS.name) {
        fun createRoute(userName: String = "", userStatus: String = ""): String {
            return "${Screen.SETTINGS.name}?userName=$userName&userStatus=$userStatus"
        }
    }
    data object Profile : Route(Screen.PROFILE.name)

    data object AuthNavigation : Route("authNavigation")
    data object Authentication : Route(Screen.AUTH.name)
    data object Register: Route(Screen.REGISTER.name)
    data object ForgotPassword: Route(Screen.FORGOT_PASSWORD.name)

    data object Onboarding : Route(Screen.ONBOARDING.name)

}
