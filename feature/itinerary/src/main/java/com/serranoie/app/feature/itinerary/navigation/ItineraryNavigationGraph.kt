package com.serranoie.app.feature.itinerary.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.itinerary.CreateEventScreen
import com.serranoie.app.feature.itinerary.ItineraryScreen
import com.serranoie.app.feature.itinerary.ItineraryViewModel
import com.serranoie.itinero.core.domain.model.Trip
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.serranoie.app.feature.itinerary.ItineraryItem as ScreenItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem as DomainItineraryItem

fun NavGraphBuilder.itineraryGraph(
    navController: NavController, tripId: String, tripData: Trip? = null
) {
    composable(Route.Itinerary.route) {
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> { parametersOf(groupCode) }

        val uiState by viewModel.uiState.collectAsState()
        val itineraryData by viewModel.itineraryData.collectAsState()

        LaunchedEffect(groupCode) {
            viewModel.fetchItinerary(groupCode)
        }

        val screenItineraryData = convertDomainToScreenModel(itineraryData)

        ItineraryScreen(
            navController = navController,
            itinerary = screenItineraryData,
            uiState = uiState,
            onRefresh = {
                viewModel.fetchItinerary(groupCode, forceRefresh = true)
            },
            onToggleCompletion = { itemId ->
                viewModel.toggleActivityCompletion(itemId)
            },
            onSwiped = {
                Log.d("ITINERO - ITNavGraph", "=== SWIPE ACTION ===")
                viewModel.refreshData()
            })
    }

    composable(Route.AddItinerary.route) {
        CreateEventScreen(navController)
    }
}

// Convert domain model to screen model
private fun convertDomainToScreenModel(
    domainItems: List<DomainItineraryItem>
): Map<LocalDate, List<ScreenItineraryItem>> {
    if (domainItems.isEmpty()) {
        return emptyMap()
    }

    val result = domainItems.groupBy { item ->
        try {
            LocalDate.parse(item.dateTime.split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }.mapValues { (date, items) ->
        items.map { domainItem ->
            ScreenItineraryItem(
                id = domainItem.id.toString(),
                title = domainItem.name,
                time = extractTime(domainItem.dateTime),
                location = domainItem.location,
                description = domainItem.summary,
                isCompleted = domainItem.isCompleted
            )
        }
    }
    return result
}

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
        Log.w("ITINERO - ITNavGraph", "Time extraction failed for $dateTime")
        "TBD"
    }
}
