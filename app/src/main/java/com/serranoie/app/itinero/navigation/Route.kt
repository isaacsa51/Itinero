package com.serranoie.app.itinero.navigation

sealed class Route(val route: String) {

    data object AppStartNavigation : Route("appStartNavigation")

    data object HomeNavigation : Route("homeNavigation")

    data object SettingsNavigation : Route("settingsNavigation")

    data object Onboarding : Route(Screen.ONBOARDING.name)
    data object Home : Route(Screen.HOME.name)

//    data class Edit(val itemId: Int) : Route("edit/$itemId") {
//        companion object {
//            fun editItemRoute(itemId: Int) = "edit/$itemId"
//        }
//    }
}