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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import com.serranoie.app.designsystemlib.ui.theme.component.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.theme.component.shimmerable
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
        is TravelUiState.Idle -> if (trips.isNotEmpty()) "success" else "loading"
    }

    LaunchedEffect(uiState) {
        if (uiState is TravelUiState.Error) {
            onShowSnackbar(uiState.message)
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
                TravelItem(
                    trip = Trip(
                        id = "loading",
                        groupCode = "loading",
                        groupName = "loading",
                        totalMembers = 0,
                        destination = "loading",
                        startDate = "2025-12-01",
                        endDate = "2025-12-31",
                        summary = "loading",
                        accommodation = Accommodation(
                            name = "loading",
                            phone = "loading",
                            checkIn = "loading",
                            checkOut = "loading",
                            location = "loading",
                            mapUri = "loading"
                        ),
                        reservationCode = "loading",
                        extraInfo = "loading",
                        additionalInfo = "loading",
                        ownerId = "loading"
                    ),
                    onClick = {}
                )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = trip.destination,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.shimmerable()
                    )
                }

                Text(
                    text = trip.groupName,
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    modifier = Modifier.shimmerable()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    Text(
                        text = tripStatus,
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.shimmerable()
                    )
                    Text(
                        text = "${trip.startDate} - ${trip.endDate}",
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        modifier = Modifier.shimmerable()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "Accommodation:",
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.shimmerable()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trip.accommodation.name,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        modifier = Modifier.shimmerable()
                    )
                }
            }
        }

        if (tripStatus == "Completed") {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleLargeEmphasized.copy(
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface
                    )
                )
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
