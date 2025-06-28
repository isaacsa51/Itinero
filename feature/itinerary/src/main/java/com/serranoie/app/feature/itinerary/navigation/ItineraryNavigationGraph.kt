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
            },
            onActivityClick = { item: ScreenItineraryItem ->
                Log.d("ITINERO - ITNavGraph", "=== ITEM CLICKED ===")
                Log.d("ITINERO - ITNavGraph", "Item ID: ${item.id}")
                if (!item.id.isNullOrEmpty()) {
                    val editRoute = Route.EditItinerary.createRoute(item.id)
                    Log.d("ITINERO - ITNavGraph", "Navigating to: $editRoute")
                    navController.navigate(editRoute)
                } else {
                    Log.w(
                        "ITINERO - ITNavGraph",
                        "Item ID is null or empty, cannot navigate to edit"
                    )
                }
            }
        )
    }

    composable(Route.AddItinerary.route) {
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> { parametersOf(groupCode) }

        Log.d("ItineraryNavGraph", "=== ADD ITINERARY ROUTE ===")
        Log.d("ItineraryNavGraph", "Group Code: $groupCode")

        CreateEventScreen(
            navController = navController,
            existingItem = null,
            onCreateActivity = { name, dateTime, location, summary ->
                Log.d("ItineraryNavGraph", "=== CREATE ACTIVITY ===")
                Log.d(
                    "ItineraryNavGraph",
                    "Name: $name, DateTime: $dateTime, Location: $location"
                )

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                Log.d("ItineraryNavGraph", "Parsed - Date: $date, Time: $time")

                val createRequest = CreateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    summary = summary
                )

                viewModel.createActivity(groupCode, createRequest)
            },
            onUpdateActivity = { id, name, dateTime, location, summary ->
                Log.d("ItineraryNavGraph", "=== UPDATE ACTIVITY ===")
                Log.d("ItineraryNavGraph", "ID: $id, Name: $name")

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                Log.d("ItineraryNavGraph", "Parsed - Date: $date, Time: $time")

                val updateRequest = UpdateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    summary = summary
                )

                viewModel.updateActivity(id, updateRequest)
            },
            onSaveComplete = {
                Log.d("ItineraryNavGraph", "=== SAVE COMPLETE ===")
                navController.popBackStack()
            }
        )
    }

    composable(Route.EditItinerary.route) { backStackEntry ->
        val itemId = Route.EditItinerary.itemIdFromRoute(backStackEntry)
        val groupCode = tripData?.groupCode ?: tripId
        val viewModel = koinViewModel<ItineraryViewModel> { parametersOf(groupCode) }

        Log.d("ItineraryNavGraph", "=== EDIT ITINERARY ROUTE ===")
        Log.d("ItineraryNavGraph", "Item ID from route: $itemId")
        Log.d("ItineraryNavGraph", "Group Code: $groupCode")

        val uiState by viewModel.uiState.collectAsState()
        val itineraryData by viewModel.itineraryData.collectAsState()

        LaunchedEffect(groupCode) {
            Log.d("ItineraryNavGraph", "LaunchedEffect: Checking itinerary data...")
            Log.d("ItineraryNavGraph", "Current itinerary data size: ${itineraryData.size}")
            if (itineraryData.isEmpty()) {
                Log.d("ItineraryNavGraph", "Fetching itinerary data...")
                viewModel.fetchItinerary(groupCode)
            }
        }

        val getExistingItem = { id: String? ->
            if (!id.isNullOrEmpty()) {
                val foundItem = itineraryData.find { it.id.toString() == id }
                Log.d("ItineraryNavGraph", "Found item: ${foundItem?.name ?: "NOT FOUND"}")
                foundItem?.let { domainItem ->
                    ScreenItineraryItem(
                        id = domainItem.id.toString(),
                        title = domainItem.name,
                        date = domainItem.date,
                        time = "${domainItem.date} at ${domainItem.time}",
                        location = domainItem.location,
                        description = domainItem.summary,
                        isCompleted = domainItem.isCompleted
                    )
                }
            } else null
        }

        val existingItem = getExistingItem(itemId)

        LaunchedEffect(itemId, itineraryData) {
            if (!itemId.isNullOrEmpty() && itineraryData.isNotEmpty() && existingItem == null) {
                Log.w("ItineraryNavGraph", "Item with ID $itemId not found, navigating back")
                navController.popBackStack()
            }
        }

        CreateEventScreen(
            navController = navController,
            existingItem = existingItem,
            onCreateActivity = { name, dateTime, location, summary ->
                Log.d("ItineraryNavGraph", "=== CREATE ACTIVITY ===")
                Log.d(
                    "ItineraryNavGraph",
                    "Name: $name, DateTime: $dateTime, Location: $location"
                )

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                Log.d("ItineraryNavGraph", "Parsed - Date: $date, Time: $time")

                val createRequest = CreateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    summary = summary
                )

                viewModel.createActivity(groupCode, createRequest)
            },
            onUpdateActivity = { id, name, dateTime, location, summary ->
                Log.d("ItineraryNavGraph", "=== UPDATE ACTIVITY ===")
                Log.d("ItineraryNavGraph", "ID: $id, Name: $name")

                val (date, time) = parseDateTimeFromCreateEventScreen(dateTime)
                Log.d("ItineraryNavGraph", "Parsed - Date: $date, Time: $time")

                val updateRequest = UpdateItineraryItem(
                    name = name,
                    date = date,
                    time = time,
                    location = location,
                    summary = summary
                )

                viewModel.updateActivity(id, updateRequest)
            },
            onSaveComplete = {
                Log.d("ItineraryNavGraph", "=== SAVE COMPLETE ===")
                navController.popBackStack()
            }
        )
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
                title = domainItem.name,
                date = domainItem.date,
                time = domainItem.time,
                location = domainItem.location,
                description = domainItem.summary,
                isCompleted = domainItem.isCompleted
            )
        }
    }
    return result
}

private fun parseDateFromDateTime(datePart: String): LocalDate {
    Log.d("ITINERO - ITNavGraph", "Parsing date: '$datePart'")

    return try {
        when {
            datePart.split(" ").size >= 3 -> {
                LocalDate.parse(datePart, DateTimeFormatter.ofPattern("d MMMM yyyy"))
            }
            datePart.length == 10 && datePart.contains("-") -> {
                LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE)
            }
            else -> {
                Log.w("ITINERO - ITNavGraph", "Unknown date format: $datePart")
                LocalDate.now()
            }
        }
    } catch (e: Exception) {
        Log.w("ITINERO - ITNavGraph", "Failed to parse date: $datePart, using current date", e)
        LocalDate.now()
    }
}

private fun parseDateTimeFromCreateEventScreen(dateTime: String): Pair<String, String> {
    Log.d("ItineraryNavGraph", "Parsing dateTime from CreateEventScreen: $dateTime")

    return when {
        dateTime.contains(" at ") -> {
            val parts = dateTime.split(" at ")
            Pair(parts[0].trim(), parts[1].trim())
        }
        dateTime.contains(", ") -> {
            val parts = dateTime.split(", ")
            Pair(parts[0].trim(), parts[1].trim())
        }
        dateTime.contains("T") -> {
            val parts = dateTime.split("T")
            val date = parts[0].trim()
            val time = if (parts.size > 1) parts[1].trim() else "TBD"
            Pair(date, time)
        }
        else -> {
            Pair(dateTime.trim(), "TBD")
        }
    }
}
