package org.serranoie.core.itinero.navigation

sealed class Screen {
    data object Onboard: Screen()
    data object Auth: Screen()
    data object Home: Screen()

    // Add more screens as needed
}