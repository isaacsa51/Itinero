package com.serranoie.app.feature.itinerary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.CreateEventScreen
import com.serranoie.app.feature.itinerary.ItineraryItem
import com.serranoie.app.feature.itinerary.ItineraryScreen
import com.serranoie.itinero.core.domain.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun NavGraphBuilder.itineraryGraph(
    navController: NavController,
    tripId: String,
    tripData: Trip? = null
) {
    composable(Route.Itinerary.route) {
        val itineraryData = tripData?.let { trip ->
            // Parse date strings to LocalDate
            val startDate = try {
                LocalDate.parse(trip.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                LocalDate.now()
            }

            val endDate = try {
                LocalDate.parse(trip.endDate, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                startDate.plusDays(7) // Default 7 days if parsing fails
            }

            // Generate itinerary items per day
            generateItineraryFromTrip(startDate, endDate, trip)
        } ?: generateMockItinerary() // Fallback to mock data

        ItineraryScreen(
            navController = navController,
            itinerary = itineraryData,
            onSwiped = {
                // Handle swipe actions - placeholder for now
            }
        )
    }

    composable(Route.AddItinerary.route) {
        CreateEventScreen(navController)
    }
}

// Helper function to generate itinerary from trip data
private fun generateItineraryFromTrip(
    startDate: LocalDate,
    endDate: LocalDate,
    trip: Trip
): Map<LocalDate, List<ItineraryItem>> {
    val itineraryMap = mutableMapOf<LocalDate, List<ItineraryItem>>()

    // Generate dates from start to end
    var currentDate = startDate
    while (!currentDate.isAfter(endDate)) {
        // Create activities based on day of week and trip data
        val activities = when (currentDate.dayOfWeek.value) {
            1 -> listOf( // Monday
                ItineraryItem(
                    title = "Arrival Day",
                    time = "10:00 AM",
                    location = trip.destination ?: "Destination",
                    description = "Check-in and explore the area"
                )
            )

            2 -> listOf( // Tuesday
                ItineraryItem(
                    title = "City Tour",
                    time = "9:00 AM",
                    location = "City Center",
                    description = "Guided tour of main attractions"
                ),
                ItineraryItem(
                    title = "Local Restaurant",
                    time = "1:00 PM",
                    location = "Downtown",
                    description = "Try local cuisine"
                )
            )

            3 -> listOf( // Wednesday
                ItineraryItem(
                    title = "Museum Visit",
                    time = "10:00 AM",
                    location = "Art Museum",
                    description = "Explore local art and culture"
                )
            )

            else -> emptyList()
        }

        itineraryMap[currentDate] = activities
        currentDate = currentDate.plusDays(1)
    }

    return itineraryMap
}

// Fallback mock data function
private fun generateMockItinerary(): Map<LocalDate, List<ItineraryItem>> {
    return mapOf(
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
        LocalDate.now().plusDays(5) to emptyList()
    )
}
