package com.serranoie.app.feature.itinerary.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.itinerary.CreateEventScreen
import com.serranoie.app.feature.itinerary.ItineraryItem as ScreenItineraryItem
import com.serranoie.app.feature.itinerary.ItineraryScreen
import com.serranoie.app.feature.itinerary.ItineraryUiState
import com.serranoie.app.feature.itinerary.ItineraryViewModel
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem as DomainItineraryItem
import com.serranoie.itinero.core.domain.model.Trip
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun NavGraphBuilder.itineraryGraph(
    navController: NavController,
    tripId: String,
    tripData: Trip? = null
) {
    composable(Route.Itinerary.route) {
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> { parametersOf(groupCode) }
        val uiState by viewModel.uiState.collectAsState()
        val itineraryData by viewModel.itineraryData.collectAsState()

        // Fetch data when the screen is first loaded
        LaunchedEffect(groupCode) {
            viewModel.fetchItinerary(groupCode)
        }

        // Convert domain model to screen model - only use real data from ViewModel
        val screenItineraryData = convertDomainToScreenModel(itineraryData)

        ItineraryScreen(
            navController = navController,
            itinerary = screenItineraryData,
            uiState = uiState,
            onRefresh = {
                viewModel.fetchItinerary(groupCode, forceRefresh = true)
            },
            onToggleCompletion = { itemId -> viewModel.toggleActivityCompletion(itemId) },
            onSwiped = { viewModel.refreshData() }
        )
    }

    composable(Route.AddItinerary.route) {
        CreateEventScreen(navController)
    }
}

// Convert domain model to screen model
private fun convertDomainToScreenModel(
    domainItems: List<DomainItineraryItem>
): Map<LocalDate, List<ScreenItineraryItem>> {
    return domainItems.groupBy { item ->
        try {
            // Parse the dateTime string to extract date
            LocalDate.parse(item.dateTime.split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }.mapValues { (_, items) ->
        items.map { domainItem ->
            ScreenItineraryItem(
                title = domainItem.name,
                time = extractTime(domainItem.dateTime),
                location = domainItem.location,
                description = domainItem.summary,
                isCompleted = domainItem.isCompleted
            )
        }
    }
}

// Extract time from datetime string
private fun extractTime(dateTime: String): String {
    return try {
        val timePart = dateTime.split("T").getOrNull(1)?.split(":")
        if (timePart != null && timePart.size >= 2) {
            val hour = timePart[0].toInt()
            val minute = timePart[1]
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            "$displayHour:$minute $amPm"
        } else {
            "TBD"
        }
    } catch (e: Exception) {
        "TBD"
    }
}
