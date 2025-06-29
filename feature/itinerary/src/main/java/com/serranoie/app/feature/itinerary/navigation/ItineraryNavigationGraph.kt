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
import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.itinero.core.domain.model.Trip
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.serranoie.app.feature.itinerary.ItineraryItem as ScreenItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem as DomainItineraryItem

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
                viewModel.toggleActivityCompletion(groupCode, itemId)
            },
            onSwiped = {
                Log.d("ITINERO - ITNavGraph", "=== SWIPE ACTION ===")
                viewModel.refreshData()
            },
            onActivityClick = { item: ScreenItineraryItem ->
                if (!item.id.isNullOrEmpty()) {
                    val editRoute = Route.EditItinerary.createRoute(item.id)
                    navController.navigate(editRoute)
                }
            })
    }

    composable(Route.AddItinerary.route) {
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> { parametersOf(groupCode) }
        val itineraryData by viewModel.itineraryData.collectAsState()

        CreateEventScreen(
            navController = navController,
            existingItem = null,
            onCreateActivity = { name, dateTime, location, description ->
                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                val createRequest = CreateItineraryItem(
                    name = name,
                    description = description,
                    date = date,
                    time = time,
                    location = location
                )
                viewModel.createActivity(groupCode, createRequest)
            },
            onUpdateActivity = { id, name, dateTime, location, description ->
                Log.d("ItineraryNavGraph", "=== UPDATE ACTIVITY ===")
                Log.d("ItineraryNavGraph", "ID: $id, Name: $name")
                Log.d("ItineraryNavGraph", "Raw dateTime from CreateEventScreen: '$dateTime'")

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)

                val updateRequest = UpdateItineraryItem(
                    name = name,
                    description = description,
                    date = date,
                    time = time,
                    location = location
                )
                Log.d("ItineraryNavGraph", "Update request: $updateRequest")

                viewModel.updateActivity(groupCode, id, updateRequest)
            },
            onSaveComplete = {
                navController.popBackStack()
            })
    }

    composable(Route.EditItinerary.route) { backStackEntry ->
        val itemId = Route.EditItinerary.itemIdFromRoute(backStackEntry)
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> {
            parametersOf(groupCode)
        }
        val itineraryData by viewModel.itineraryData.collectAsState()

        LaunchedEffect(groupCode) {
            if (itineraryData.isEmpty()) {
                Log.d("ItineraryNavGraph", "Fetching itinerary data...")
                viewModel.fetchItinerary(groupCode)
            }
        }

        val getExistingItem = { id: String? ->
            if (!id.isNullOrEmpty()) {
                Log.d("ItineraryNavGraph", "Looking for item with ID: $id")
                val foundItem = itineraryData.find { it.id.toString() == id }
                Log.d("ItineraryNavGraph", "Found item: ${foundItem?.name}")
                foundItem?.let { domainItem ->
                    Log.d(
                        "ItineraryNavGraph",
                        "Domain item - Name: ${domainItem.name}, Date: ${domainItem.date}, Time: ${domainItem.time}"
                    )
                    ScreenItineraryItem(
                        id = domainItem.id.toString(),
                        name = domainItem.name,
                        date = domainItem.date,
                        time = domainItem.time,
                        location = domainItem.location,
                        description = domainItem.description,
                        isCompleted = domainItem.isCompleted
                    ).also { screenItem ->
                        Log.d(
                            "ItineraryNavGraph",
                            "Screen item - Title: ${screenItem.name}, Date: ${screenItem.date}, Time: ${screenItem.time}"
                        )
                    }
                }
            } else {
                Log.w("ItineraryNavGraph", "Item ID is null or empty")
                null
            }
        }

        val existingItem = getExistingItem(itemId)

        LaunchedEffect(itemId, itineraryData) {
            if (!itemId.isNullOrEmpty() && itineraryData.isNotEmpty() && existingItem == null) {
                navController.popBackStack()
            }
        }

        CreateEventScreen(
            navController = navController,
            existingItem = existingItem,
            onCreateActivity = { name, dateTime, location, description ->
                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                val createRequest = CreateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    description = description
                )

                viewModel.createActivity(groupCode, createRequest)
            },
            onUpdateActivity = { id, name, dateTime, location, description ->
                Log.d("ItineraryNavGraph", "=== UPDATE ACTIVITY ===")
                Log.d("ItineraryNavGraph", "ID: $id, Name: $name")
                Log.d("ItineraryNavGraph", "Raw dateTime from CreateEventScreen: '$dateTime'")

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)

                val updateRequest = UpdateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    description = description
                )
                Log.d("ItineraryNavGraph", "Update request: $updateRequest")

                viewModel.updateActivity(groupCode, id, updateRequest)
            },
            onSaveComplete = {
                navController.popBackStack()
            })
    }
}

private fun convertDomainToScreenModel(
    domainItems: List<DomainItineraryItem>
): Map<LocalDate, List<ScreenItineraryItem>> {
    if (domainItems.isEmpty()) {
        return emptyMap()
    }

    val result = domainItems.groupBy { item ->
        parseDateFromDateTime(item.date)
    }.mapValues { (_, items) ->
        items.map { domainItem ->
            ScreenItineraryItem(
                id = domainItem.id.toString(),
                name = domainItem.name,
                date = domainItem.date,
                time = domainItem.time,
                location = domainItem.location,
                description = domainItem.description,
                isCompleted = domainItem.isCompleted
            )
        }
    }
    return result
}

private fun parseDateFromDateTime(datePart: String): LocalDate {
    return try {
        // Handle format "28 June 2025" from the date field
        LocalDate.parse(
            datePart,
            DateTimeFormatter.ofPattern("dd MMMM yyyy")
        )
    } catch (e: Exception) {
        Log.e("ITINERO - ITNavGraph", "Failed to parse date: $datePart, using current date", e)
        LocalDate.now()
    }
}

private fun parseDateTimeFromCreateEventScreen(dateTime: String): Pair<String, String> {
    Log.d("ItineraryNavGraph", "Parsing dateTime: '$dateTime'")

    return when {
        // Handle format: "28 June 2025, 12:00 PM"
        dateTime.contains(", ") && (dateTime.contains(" AM") || dateTime.contains(" PM")) -> {
            val parts = dateTime.split(", ")
            val date = parts[0].trim()
            val time = parts[1].trim()
            Log.d("ItineraryNavGraph", "Parsed comma format - Date: '$date', Time: '$time'")
            Pair(date, time)
        }

        // Handle format: "28 June 2025 at 12:00 PM"
        dateTime.contains(" at ") -> {
            val parts = dateTime.split(" at ")
            val date = parts[0].trim()
            val time = parts[1].trim()
            Log.d("ItineraryNavGraph", "Parsed 'at' format - Date: '$date', Time: '$time'")
            Pair(date, time)
        }

        // Handle ISO format: "2025-06-28T12:00:00"
        dateTime.contains("T") -> {
            val parts = dateTime.split("T")
            val date = parts[0].trim()
            val time = if (parts.size > 1) parts[1].trim() else "TBD"
            Log.d("ItineraryNavGraph", "Parsed ISO format - Date: '$date', Time: '$time'")
            Pair(date, time)
        }

        // Handle date only format
        else -> {
            Log.d("ItineraryNavGraph", "Using as date only - Date: '$dateTime', Time: 'TBD'")
            Pair(dateTime.trim(), "TBD")
        }
    }
}
