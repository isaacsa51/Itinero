package com.serranoie.app.feature.itinerary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.itinerary.ItineraryItem
import com.serranoie.app.feature.itinerary.ItineraryScreen
import java.time.LocalDate

fun NavGraphBuilder.itineraryGraph(navController: NavController) {
    composable(Route.Itinerary.route) {
        val mockItinerary = mapOf(
            LocalDate.now() to listOf(
                ItineraryItem(
                    title = "Visit Museum",
                    time = "12:00 PM",
                    location = "Art Museum",
                    description = "Explore the modern art section"
                ),
                ItineraryItem(
                    title = "Lunch",
                    time = "2:00 PM",
                    location = "City Café",
                    description = "Try the local special"
                )
            ),
            LocalDate.now().plusDays(1) to listOf(
                ItineraryItem(
                    title = "Beach Day",
                    time = "10:00 AM",
                    location = "Palm Beach",
                    description = "Sunbathing and volleyball"
                )
            ),
            LocalDate.now().plusDays(2) to emptyList()
        )

        ItineraryScreen(
            navController = navController,
            itinerary = mockItinerary,
            onSwiped = { /* Implementation */ }
        )
    }
}