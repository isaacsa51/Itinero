package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.serranoie.app.feature.auth.navigation.AuthNavigationGraph
import com.serranoie.app.feature.onboard.navigation.OnboardNavigationGraph
import com.serranoie.app.feature.welcome.navigation.WelcomeNavigationGraph

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        with(OnboardNavigationGraph()) {
            build(navController)
        }
        with(HomeNavigationGraph()) {
            build(navController)
        }
        with(AuthNavigationGraph()) {
            build(navController)
        }
        with(WelcomeNavigationGraph()) {
            build(navController)
        }
    }
}
