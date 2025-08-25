package com.serranoie.app.feature.welcome

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import com.serranoie.app.feature.TravelUiState
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TravelListScreen(
    uiState: TravelUiState,
    trips: List<Trip>,
    onGetAllTravels: () -> Unit,
    onResetState: () -> Unit,
    onAddTravelClick: () -> Unit,
    onJoinTravelClick: () -> Unit,
    onTravelClick: (String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    val items = listOf(
        Icons.Rounded.People to "Join a trip",
        Icons.Rounded.GroupAdd to "Create trip"
    )

    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

    LaunchedEffect(key1 = true) {
        if (uiState is TravelUiState.Idle && trips.isEmpty()) {
            onGetAllTravels()
        }
    }

    val targetState = when (uiState) {
        is TravelUiState.Loading -> "loading"
        is TravelUiState.Success<*> -> if (trips.isNotEmpty()) "success" else "empty"
        is TravelUiState.Error -> "error"
        is TravelUiState.NetworkError -> "network_error"
        is TravelUiState.NoInternet -> "no_internet"
        is TravelUiState.Idle -> if (trips.isNotEmpty()) "success" else "loading"
    }

    LaunchedEffect(uiState) {
        if (uiState is TravelUiState.Error) {
            onShowSnackbar(uiState.message)
            onResetState()
        } else if (uiState is TravelUiState.NetworkError) {
            onShowSnackbar("Network connection error. Please check your internet connection.")
            onResetState()
        } else if (uiState is TravelUiState.NoInternet) {
            onShowSnackbar("No internet connection. Please check your network settings.")
            onResetState()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("My Trips") },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded,
                button = {
                    ToggleFloatingActionButton(
                        modifier =
                            Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription =
                                        if (fabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = fabVisible || fabMenuExpanded,
                                    alignment = Alignment.BottomEnd
                                ),
                        checked = fabMenuExpanded,
                        onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (fabMenuExpanded) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(
                            painter = rememberVectorPainter(imageVector),
                            contentDescription = null
                        )
                    }
                }
            ) {
                items.forEachIndexed { i, item ->
                    FloatingActionButtonMenuItem(
                        modifier =
                            Modifier.semantics {
                                isTraversalGroup = true
                                if (i == items.size - 1) {
                                    customActions =
                                        listOf(
                                            CustomAccessibilityAction(
                                                label = "Close menu",
                                                action = {
                                                    fabMenuExpanded = false
                                                    true
                                                }
                                            )
                                        )
                                }
                            },
                        onClick = {
                            fabMenuExpanded = false
                            when (i) {
                                0 -> onJoinTravelClick()
                                1 -> onAddTravelClick()
                            }
                        },
                        icon = { Icon(item.first, contentDescription = null) },
                        text = { Text(text = item.second) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                if (targetState == "success" && initialState == "loading" ||
                    targetState == "empty" && initialState == "loading") {
                    fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 300))
                } else {
                    fadeIn() togetherWith fadeOut()
                }
                    .using(
                        SizeTransform(clip = false)
                    )
            }
        ) { stateKey ->
            when (stateKey) {
                "loading" -> {
                    ShimmerLoadingTravelList(paddingValues = paddingValues)
                }
                "success" -> {
                    TripListInfo(
                        trips = trips,
                        paddingValues = paddingValues,
                        listState = listState,
                        onTravelClick = onTravelClick
                    )
                }
                "empty" -> {
                    EmptyTravelList(
                        paddingValues = paddingValues,
                    )
                }
                "error" -> {
                    ErrorTravelList(
                        paddingValues = paddingValues,
                        onRetry = onGetAllTravels
                    )
                }
                "network_error" -> {
                    ErrorTravelList(
                        paddingValues = paddingValues,
                        onRetry = onGetAllTravels
                    )
                }

                "no_internet" -> {
                    NoInternetTravelList(
                        paddingValues = paddingValues,
                        onRetry = onGetAllTravels
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerLoadingTravelList(
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues
    ) {
        items(4) {
            ShimmerProvider {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shimmer Airplane Icon
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .shimmerable()
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                // Destination shimmer
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(24.dp)
                                        .shimmerable()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Date and people shimmer
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(16.dp)
                                        .shimmerable()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Group name shimmer
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(16.dp)
                                        .shimmerable()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripListInfo(
    trips: List<Trip>,
    paddingValues: PaddingValues,
    listState: LazyListState,
    onTravelClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
        state = listState
    ) {
        items(trips) { trip ->
            TravelItem(
                trip = trip,
                onClick = { onTravelClick(trip.groupCode) }
            )
        }
    }
}

@Composable
fun EmptyTravelList(
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No trips found",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to create or join a trip",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ErrorTravelList(
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error loading trips",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please try again",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun NoInternetTravelList(
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No internet connection",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please check your connection and try again",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelItem(
    trip: Trip,
    onClick: () -> Unit
) {
    val tripStatus = determineTripStatus(trip.startDate, trip.endDate)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Airplane Icon
                Icon(
                    imageVector = Icons.Filled.FlightTakeoff,
                    contentDescription = "Trip",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.destination,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.shimmerable()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTripDates(trip.startDate, trip.endDate),
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.shimmerable()
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Rounded.People,
                            contentDescription = "Members",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trip.totalMembers} people",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.shimmerable()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trip.groupName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.shimmerable()
                    )
                }
                if (tripStatus == "Completed") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

fun determineTripStatus(startDate: String, endDate: String): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val start = LocalDate.parse(startDate, formatter)
    val end = LocalDate.parse(endDate, formatter)

    return when {
        today.isAfter(end) -> "Completed"
        today.isAfter(start) && today.isBefore(end) -> "In Progress"
        else -> "Pending"
    }
}

fun formatTripDates(startDate: String, endDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("MMM d")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)

        if (start.month == end.month && start.year == end.year) {
            // Same month: "Oct 12 - 15"
            "${start.format(outputFormatter)} - ${end.dayOfMonth}"
        } else {
            // Different months: "Oct 12 - Nov 15"
            "${start.format(outputFormatter)} - ${end.format(outputFormatter)}"
        }
    } catch (e: Exception) {
        "$startDate - $endDate"
    }
}

@DevicePreview
@Composable
private fun TravelListPreview() {
    PreviewWrapper {
        TravelListScreen(
            uiState = TravelUiState.Idle,
            trips = emptyList(),
            onGetAllTravels = {},
            onResetState = {},
            onAddTravelClick = {},
            onJoinTravelClick = {},
            onTravelClick = {},
            onShowSnackbar = {}
        )
    }
}

@Preview
@Composable
private fun ScreenWithItemsPreview() {
    val listDataMock = listOf(
        Trip(
            id = "1",
            groupCode = "PAR24",
            groupName = "My Group",
            totalMembers = 2,
            destination = "Paris, France",
            startDate = "2025-12-01",
            endDate = "2025-12-31",
            summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
            accommodation = Accommodation(
                name = "Le Meurice Hotel",
                phone = "+33 1 23 45 67 89",
                checkIn = "14:00",
                checkOut = "11:00",
                location = "Paris, France",
                mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
                latitude = 48.8566,
                longitude = 2.3522,
                reservationCode = "LMH-2024-098",
                extraInfo = "Winter season with holiday decorations",
            ),
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
            additionalInfo = "Museum passes and restaurant reservations confirmed",
            ownerId = 1.toString(),
        ), Trip(
            id = "1",
            groupCode = "PAR24",
            groupName = "My Group",
            destination = "Tokyo, Japan",
            startDate = "2025-12-01",
            endDate = "2025-12-31",
            summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
            accommodation = Accommodation(
                name = "Le Meurice Hotel",
                phone = "+33 1 23 45 67 89",
                checkIn = "14:00",
                checkOut = "11:00",
                location = "Paris, France",
                mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
                latitude = 35.6895,
                longitude = 139.7670,
                reservationCode = "LMH-2024-098",
                extraInfo = "Winter season with holiday decorations",
            ),
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
            additionalInfo = "Museum passes and restaurant reservations confirmed",
            ownerId = 1.toString(),
            totalMembers = 2,
        )
    )

    PreviewWrapper {
        TravelListScreen(
            uiState = TravelUiState.Success(listDataMock),
            trips = listDataMock,
            onGetAllTravels = {},
            onResetState = {},
            onAddTravelClick = {},
            onJoinTravelClick = {},
            onTravelClick = {},
            onShowSnackbar = {},
        )
    }
}

@ComponentPreview
@Composable
private fun TravelItemPreview() {
    val mockTrip = Trip(
        id = "1",
        groupName = "My Group",
        groupCode = "PAR24",
        totalMembers = 2,
        destination = "Paris, France",
        startDate = "2025-12-01",
        endDate = "2025-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = Accommodation(
            name = "Le Meurice Hotel",
            phone = "+33 1 23 45 67 89",
            checkIn = "14:00",
            checkOut = "11:00",
            location = "Paris, France",
            mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
            latitude = 48.8566,
            longitude = 2.3522,
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
        ),
        reservationCode = "LMH-2024-099",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        ownerId = 1.toString(),
    )

    val mockOwnerTripPending = Trip(
        id = "1",
        groupName = "My Group",
        groupCode = "PAR24",
        destination = "Tokyo, Japan",
        startDate = "2025-12-01",
        endDate = "2025-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = Accommodation(
            name = "Le Meurice Hotel",
            phone = "+33 1 23 45 67 89",
            checkIn = "14:00",
            checkOut = "11:00",
            location = "Paris, France",
            mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
            latitude = 35.6895,
            longitude = 139.7670,
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
        ),
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        ownerId = 1.toString(),
        totalMembers = 2,
    )

    val mockOwnerTripProgress = Trip(
        id = "1",
        groupName = "My Group",
        groupCode = "PAR24",
        destination = "Paris, France",
        startDate = "2025-05-25",
        endDate = "2025-05-30",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = Accommodation(
            name = "Le Meurice Hotel",
            phone = "+33 1 23 45 67 89",
            checkIn = "14:00",
            checkOut = "11:00",
            location = "Paris, France",
            mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
            latitude = 48.8566,
            longitude = 2.3522,
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
        ),
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        ownerId = 1.toString(),
        totalMembers = 2,
    )

    val mockTripCompleted = Trip(
        id = "1",
        groupName = "My Group",
        groupCode = "PAR24",
        destination = "Paris, France",
        startDate = "2024-12-01",
        endDate = "2024-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = Accommodation(
            name = "Le Meurice Hotel",
            phone = "+33 1 23 45 67 89",
            checkIn = "14:00",
            checkOut = "11:00",
            location = "Paris, France",
            mapUri = "https://www.google.com/maps/place/Paris,+France/@48.85661",
            latitude = 48.8566,
            longitude = 2.3522,
            reservationCode = "LMH-2024-098",
            extraInfo = "Winter season with holiday decorations",
        ),
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        ownerId = 1.toString(),
        totalMembers = 2,
    )

    PreviewWrapper {
        Column {
            TravelItem(
                trip = mockTrip,
                onClick = {}
            )

            TravelItem(
                trip = mockOwnerTripProgress,
                onClick = {}
            )

            TravelItem(
                trip = mockOwnerTripPending,
                onClick = {}
            )

            TravelItem(
                trip = mockTripCompleted,
                onClick = {}
            )
        }
    }
}
