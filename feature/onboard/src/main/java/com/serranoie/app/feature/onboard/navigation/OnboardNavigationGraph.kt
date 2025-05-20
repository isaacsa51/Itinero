package com.serranoie.app.feature.onboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.onboard.OnboardScreen

class OnboardNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.Onboarding.route
        ) {
            composable(route = Route.Onboarding.route) {
                OnboardScreen(
                    onFinished = {
                        navController.navigate(Route.AuthNavigation.route) {
                            popUpTo(Route.AppStartNavigation.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}