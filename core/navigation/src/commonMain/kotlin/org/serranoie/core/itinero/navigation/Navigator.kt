package org.serranoie.core.itinero.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Navigator(
    initialScreen: Screen = Screen.Onboard
) {
    var currentScreen by mutableStateOf(initialScreen)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}