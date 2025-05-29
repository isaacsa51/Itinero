package com.serranoie.app.core.navigation

sealed class Route(val route: String) {

    data object AppStartNavigation : Route("appStartNavigation")

    data object HomeNavigation : Route("homeNavigation")
    data object Welcome : Route(Screen.WELCOME.name)
    data object Home : Route(Screen.HOME.name)
    data object Itinerary : Route(Screen.ITINERARY.name)
    data object Expenses : Route(Screen.EXPENSES.name)
    data object AddExpense : Route(Screen.ADD_EXPENSE.name)
    data object ExpenseDetails : Route(Screen.EXPENSE_DETAILS.name)
    data object Chat : Route(Screen.CHAT.name)
    data object TripSettings : Route(Screen.TRIP_SETTINGS.name)
    data object TripInfo : Route(Screen.TRIP_INFO.name)

    data object WelcomeNavigation : Route("welcomeNavigation")
    data object TravelList : Route(Screen.TRAVEL_LIST.name)
    data object TravelNavigation : Route(Screen.TRAVEL_NAVIGATION.name)
    data object CreateTravel : Route(Screen.CREATE_TRAVEL.name)
    data object JoinTrip : Route(Screen.JOIN_TRIP.name)
    data object CameraScanner : Route(Screen.CAMERA_SCANNER.name)

    data object SettingsNavigation : Route("settingsNavigation")

    data object AuthNavigation : Route("authNavigation")
    data object Authentication : Route(Screen.AUTH.name)
    data object Register: Route(Screen.REGISTER.name)
    data object ForgotPassword: Route(Screen.FORGOT_PASSWORD.name)

    data object Onboarding : Route(Screen.ONBOARDING.name)

//    data object Settings : Route(Screen.SETTINGS.name)
//    data object Profile : Route(Screen.PROFILE.name)
//    data object Notifications : Route(Screen.NOTIFICATIONS.name)
//    data object Help : Route(Screen.HELP.name)
//    data object PrivacyPolicy : Route(Screen.PRIVACY_POLICY.name)

//    data class Edit(val itemId: Int) : Route("edit/$itemId") {
//        companion object {
//            fun editItemRoute(itemId: Int) = "edit/$itemId"
//        }
//    }
}
