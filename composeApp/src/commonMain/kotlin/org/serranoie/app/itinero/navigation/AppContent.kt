package org.serranoie.app.itinero.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.serranoie.core.itinero.navigation.Navigator
import org.serranoie.core.itinero.navigation.Screen
import org.serranoie.core.itinero.navigation.auth.AuthFlowScreen
import org.serranoie.feature.itinero.auth.AuthScreen
import org.serranoie.feature.itinero.auth.navigation.AuthFeatureContent
import org.serranoie.feature.itinero.onboard.OnboardScreen

@Composable
fun AppContent() {
    val navigator = remember { Navigator() }

    when (navigator.currentScreen) {
        is Screen.Onboard -> {
            OnboardScreen(
                onNavigateAuth = { navigator.navigateTo(Screen.Auth) }
            )
        }
        is Screen.Auth -> {
            AuthFeatureContent(
                onFinish = { navigator.navigateTo(Screen.Home) }
            )

        }

        Screen.Home -> TODO()
    }
}