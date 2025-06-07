package com.serranoie.app.itinero.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.chat.ChatScreen
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.navigation.bottombar.BottomBarNav
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph
import com.serranoie.app.feature.settings.trip.TripSettingsScreen

@Composable
fun HomeRootScreen(tripId: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Log.d("ISAAC", "Trip ID: $tripId")

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(
                    Route.Home.route,
                    Route.Itinerary.route,
                    Route.Expenses.route,
                )
            ) {
                BottomBarNav(navController, tripId)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Home.route) {
                HomeScreen(navController, tripId)
            }

            itineraryGraph(navController, tripId)

            expensesGraph(navController, tripId)

            composable(Route.Chat.route) {
                ChatScreen(navController, tripId)
            }

            composable(
                route = "trip_settings/{tripId}?scrollTo={scrollTo}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("scrollTo") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val routeTripId = backStackEntry.arguments?.getString("tripId") ?: ""
                val scrollTo = backStackEntry.arguments?.getString("scrollTo")
                TripSettingsScreen(
                    navController = navController,
                    tripId = routeTripId,
                    scrollTo = scrollTo
                )
            }
        }
    }
}
