package com.serranoie.app.feature.home

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.MarqueeText
import com.serranoie.app.designsystemlib.ui.theme.component.SelectField
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpandableCard
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.AIShimmer
import com.serranoie.app.designsystemlib.ui.utils.Constants
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency
import com.serranoie.app.designsystemlib.ui.utils.clickableWithoutRipple
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Trip
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TripDateInfo(
    val status: TripStatus, val displayText: String, val subtitle: String
)

enum class TripStatus {
    PENDING, ACTIVE, COMPLETED
}

fun calculateTripDateInfo(startDate: String, endDate: String): TripDateInfo {
    return try {
        val formatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )

        var start: LocalDate? = null
        var end: LocalDate? = null

        for (formatter in formatters) {
            try {
                start = LocalDate.parse(startDate, formatter)
                break
            } catch (e: DateTimeException) {
                continue
            }
        }

        for (formatter in formatters) {
            try {
                end = LocalDate.parse(endDate, formatter)
                break
            } catch (e: DateTimeException) {
                continue
            }
        }

        if (start == null || end == null) {
            return TripDateInfo(
                status = TripStatus.PENDING,
                displayText = "Invalid dates",
                subtitle = "$startDate - $endDate"
            )
        }

        val today = LocalDate.now()
        val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1

        when {
            today.isBefore(start) -> {
                val daysUntilStart = ChronoUnit.DAYS.between(today, start).toInt()
                TripDateInfo(
                    status = TripStatus.PENDING,
                    displayText = "$daysUntilStart days until start",
                    subtitle = "$startDate - $endDate"
                )
            }

            today.isAfter(end) -> {
                TripDateInfo(
                    status = TripStatus.COMPLETED,
                    displayText = "Completed",
                    subtitle = "$totalDays days total"
                )
            }

            else -> {
                val daysRemaining = ChronoUnit.DAYS.between(today, end).toInt() + 1
                TripDateInfo(
                    status = TripStatus.ACTIVE,
                    displayText = "$daysRemaining days left",
                    subtitle = "$totalDays days total"
                )
            }
        }

    } catch (e: Exception) {
        Log.e("TripDateCalculation", "Error calculating trip dates: ${e.message}")
        TripDateInfo(
            status = TripStatus.PENDING,
            displayText = "Date error",
            subtitle = "$startDate - $endDate"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    tripId: String,
    uiState: HomeUiState,
    overviewUiState: OverviewUiState,
    onShowSnackbar: suspend (String) -> Unit,
    onGetTravel: () -> Unit,
    onGetOverview: () -> Unit,
    onRefresh: () -> Unit,
    tripInfo: Trip?
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(key1 = tripId, key2 = tripInfo, key3 = uiState) {
        if (tripInfo == null && uiState !is HomeUiState.Loading) {
            onGetTravel()
        }
        // Call getTripOverview when trip data is successfully loaded
        if (uiState is HomeUiState.Success && tripInfo != null) {
            onGetOverview()
        }
    }

    val targetState = remember(uiState, tripInfo) {
        when (uiState) {
            is HomeUiState.Loading -> "loading"
            is HomeUiState.Success -> if (tripInfo != null) "success" else "empty"
            is HomeUiState.Error -> "error"
            is HomeUiState.Idle -> if (tripInfo != null) "success" else "loading"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Itinero", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Route.Settings.route) }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }) { paddingValues ->
        AnimatedContent(
            targetState = targetState, transitionSpec = {
                if ((targetState == "success" || targetState == "empty") && initialState == "loading") {
                    fadeIn(animationSpec = tween(durationMillis = 350)) togetherWith fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                } else {
                    fadeIn() togetherWith fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }, label = "HomeScreenAnimation", modifier = Modifier.fillMaxSize()
        ) { stateKey ->
            when (stateKey) {
                "loading" -> {
                    HomeScreenShimmer(paddingValues = paddingValues)
                }

                "success" -> {
                    tripInfo?.let { trip ->
                        HomeScreenContent(
                            navController = navController,
                            tripId = tripId,
                            trip = trip,
                            tripOverview = overviewUiState,
                            paddingValues = paddingValues,
                            onRefresh = onRefresh,
                            onShowSnackbar = onShowSnackbar
                        )
                    } ?: HomeScreenEmptyOrError(
                        message = "Unexpected error: Trip data missing after success.",
                        paddingValues = paddingValues,
                        navController = navController
                    )
                }

                "empty" -> {
                    HomeScreenEmptyOrError(
                        message = "No trip information available.",
                        paddingValues = paddingValues,
                        navController = navController
                    )
                }

                "error" -> {
                    val errorMessage =
                        (uiState as? HomeUiState.Error)?.message ?: "An unknown error occurred."
                    HomeScreenEmptyOrError(
                        message = errorMessage,
                        paddingValues = paddingValues,
                        navController = navController
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenShimmer(
    paddingValues: PaddingValues
) {
    var shimmerExpanded by remember { mutableStateOf(false) }

    ShimmerProvider(isLoading = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .standardPadding()
        ) {
            DestinationCard(
                country = "Loading destination...", tripInfo = null
            )

            Spacer(modifier = Modifier.height(Constants.basePadding))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.mediumPadding)
            ) {
                InfoCard(
                    title = "Travel date",
                    value = "Loading...",
                    subtitle = "Loading...",
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                )

                InfoCard(
                    title = "People",
                    value = "Loading...",
                    subtitle = "Loading...",
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(Constants.basePadding))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Constants.basePadding)
            ) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                )
                Spacer(modifier = Modifier.height(smallPadding))
                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.shimmerable()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerable()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.shimmerable()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerable()
                )

                Spacer(modifier = Modifier.height(Constants.largePadding))

                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .AIShimmer()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Text(
                    text = "Loading trip summary information...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .AIShimmer()
                )
            }

            Spacer(modifier = Modifier.height(smallPadding))

            // Expense Summary Card
            ICard(
                modifier = Modifier
                    .fillMaxWidth()
                    .shimmerable(),
                shape = RoundedCornerShape(Constants.commonCornerRadius),
                content = {
                    Column(
                        modifier = Modifier.standardPadding()
                    ) {
                        Text(
                            text = "Expense Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(smallPadding))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Breakfast — John",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$25.00",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transportation — Sarah",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$15.50",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(smallPadding))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(smallPadding))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Today", style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$40.50",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.height(Constants.mediumPadding))

            // Today's Itinerary
            ICard(
                modifier = Modifier
                    .fillMaxWidth()
                    .shimmerable(),
                shape = RoundedCornerShape(Constants.commonCornerRadius),
                content = {
                    Column(
                        modifier = Modifier.standardPadding()
                    ) {
                        Text(
                            text = "Today's Itinerary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Constants.mediumPadding))

                        // Itinerary Item 1
                        Row(
                            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "10:00 AM",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(80.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Visit Local Market",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Explore traditional crafts and local foods",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Constants.mediumPadding))

                        // Itinerary Item 2
                        Row(
                            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "2:00 PM",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(80.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Museum Tour",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Guided tour of the city's history museum",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Constants.mediumPadding))

                        // Itinerary Item 3
                        Row(
                            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "7:00 PM",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(80.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Dinner Reservation",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Traditional cuisine at La Bella Vista",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                onClick = { })

            Spacer(modifier = Modifier.height(Constants.basePadding))

            Text(
                text = "Accommodation",
                style = MaterialTheme.typography.headlineSmallEmphasized,
            )

            Spacer(modifier = Modifier.height(smallPadding))

            ExpandableCard(
                title = "Details",
                isExpanded = shimmerExpanded,
                onExpandedChange = { shimmerExpanded = it },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                showDivider = true
            ) {
                Text(
                    text = "map sdk location holder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.shimmerable()
                )

                Spacer(modifier = Modifier.height(smallPadding))

                Text(
                    text = "ADDRESS",
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.shimmerable()
                )

                Text(
                    text = "Loading address...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.shimmerable()
                )
            }

            Spacer(modifier = Modifier.height(Constants.basePadding))

            // Travel Information section with shimmer
            TravelInfoCardShimmer()
        }
    }
}

@Composable
fun HomeScreenContent(
    navController: NavHostController,
    tripId: String,
    trip: Trip,
    tripOverview: OverviewUiState,
    paddingValues: PaddingValues,
    onRefresh: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = false, onRefresh = {
            onRefresh()
            Log.d("ITINERO", "Refreshing data...")
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .standardPadding()
        ) {
            TripDetailsContent(
                navController = navController, trip = trip, overviewUiState = tripOverview
            )
        }
    }
}

@Composable
fun HomeScreenEmptyOrError(
    message: String,
    paddingValues: PaddingValues,
    onRetry: (() -> Unit)? = null,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        onRetry?.let {
            Spacer(modifier = Modifier.height(Constants.basePadding))

            IButton(
                content = { Text("Go Back") },
                onClick = {
                    navController.navigate(Route.TravelList.route)
                },
                importance = ButtonImportance.Tertiary,
            )

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripDetailsContent(
    navController: NavController,
    trip: Trip,
    overviewUiState: OverviewUiState = OverviewUiState.Idle
) {
    var isExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DestinationCard(
            country = trip.destination, tripInfo = trip
        )

        Spacer(modifier = Modifier.height(Constants.basePadding))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(smallPadding)
        ) {
            val dateInfo = calculateTripDateInfo(trip.startDate, trip.endDate)
            InfoCard(
                title = "Travel date",
                value = dateInfo.displayText,
                subtitle = dateInfo.subtitle,
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
            )

            PeopleInfoCard(
                confirmedCount = trip.totalMembers,
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                tripInfo = trip,
            )
        }

        Spacer(modifier = Modifier.height(Constants.basePadding))

        SummarySection(
            onClick = { }, tripInfo = trip
        )

        Spacer(modifier = Modifier.height(Constants.basePadding))

        TodayTasksSection(overviewUiState = overviewUiState)

        Spacer(modifier = Modifier.height(Constants.basePadding))

        Text(
            text = "Accommodation", style = MaterialTheme.typography.headlineSmallEmphasized
        )

        Spacer(modifier = Modifier.height(smallPadding))

        ExpandableCard(
            title = "Details",
            isExpanded = isExpanded,
            onExpandedChange = { newValue ->
                isExpanded = newValue
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            showDivider = true
        ) {
            Text(
                text = "map sdk location holder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(smallPadding))

            Text(
                text = "Address".uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = trip.accommodation.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(Constants.basePadding))

        TravelInfoCard(navController, trip)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DestinationCard(
    country: String, tripInfo: Trip?
) {
    ICard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Constants.extraSmallPadding + smallPadding),
        color = MaterialTheme.colorScheme.primaryContainer,
        content = {
            Column(
                modifier = Modifier.padding(Constants.basePadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Destination".uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmallEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Text(
                    text = country,
                    style = MaterialTheme.typography.displayMediumEmphasized.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.shimmerable(
                        startColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                        endColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(Constants.extraSmallPadding))
                tripInfo?.groupName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLargeEmphasized,
                        modifier = Modifier.shimmerable()
                    )
                }
            }
        })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    ICard(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.standardPadding()
            ) {
                Text(
                    text = title.uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.shimmerable()
                )
                MarqueeText(
                    text = value,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    gradientEdgeColor = MaterialTheme.colorScheme.surfaceContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.shimmerable()
                )
            }
        },
    )
}

/* TODO: Change subtitle depending of the pending invitations like following :
    - Group ready
    - Waiting for more
    - All confirmed
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PeopleInfoCard(
    confirmedCount: Int,
    modifier: Modifier = Modifier,
    tripInfo: Trip,
) {
    ICard(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.standardPadding()
            ) {
                Text(
                    text = "People".uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.shimmerable()
                )
                Text(
                    text = "$confirmedCount total",
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier.shimmerable()
                )
                Row(
                    modifier = Modifier.padding(top = Constants.extraSmallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (confirmedCount <= 0) {
                        Text(
                            text = "Waiting people to join",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.shimmerable()
                        )
                    } else if (confirmedCount == tripInfo.totalMembers) {
                        Text(
                            text = "Group ready",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.shimmerable()
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelInfoCard(navController: NavController, tripInfo: Trip) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Travel Information",
                style = MaterialTheme.typography.headlineSmallEmphasized,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.shimmerable()
            )
            IconButton(onClick = {
                navController.navigate(
                    Route.TripSettings.createRoute(
                        tripId = tripInfo.groupCode, scrollTo = "tripInfo"
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "More travel information options"
                )
            }
        }

        SelectField(
            value = tripInfo.accommodation.name,
            onSelect = { },
            label = "Accommodation",
            leadingIcon = Icons.Rounded.SupervisedUserCircle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable()
        )

        SelectField(
            value = tripInfo.accommodation.phone,
            onSelect = { },
            label = "Phone Number",
            leadingIcon = Icons.Rounded.SupervisedUserCircle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable(),
            horizontalArrangement = Arrangement.spacedBy(Constants.mediumPadding)
        ) {
            SelectField(
                value = tripInfo.accommodation.checkIn,
                onSelect = { },
                label = "Check-In Date & Time",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier
                    .weight(1f)
                    .shimmerable()
            )
            SelectField(
                value = tripInfo.accommodation.checkOut,
                onSelect = { },
                label = "Check-Out Date & Time",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier
                    .weight(1f)
                    .shimmerable()
            )
        }

        SelectField(
            value = tripInfo.reservationCode,
            onSelect = { },
            label = "Reservation Code",
            leadingIcon = null,
            modifier = Modifier
                .fillMaxWidth()
                .shimmerable()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SummarySection(
    onClick: () -> Unit, tripInfo: Trip
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickableWithoutRipple { onClick() }
        .padding(horizontal = Constants.basePadding)) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineSmallEmphasized,
        )
        Spacer(modifier = Modifier.height(smallPadding))
        Text(
            text = tripInfo.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.shimmerable()
        )
        Spacer(modifier = Modifier.height(smallPadding))
        Text(
            text = "AI summary generated by Gemini would be here for the moment...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.shimmerable()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodayTasksSection(overviewUiState: OverviewUiState = OverviewUiState.Idle) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        when (overviewUiState) {
            is OverviewUiState.Success -> {
                val overview = overviewUiState.data

                // Yesterday Expenses Card
                if (overview.yesterdayExpenses.isNotEmpty()) {
                    ICard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Constants.commonCornerRadius),
                        content = {
                            Column(
                                modifier = Modifier.standardPadding(),
                                verticalArrangement = Arrangement.spacedBy(smallPadding)
                            ) {
                                Text(
                                    text = "Yesterday Expenses",
                                    style = MaterialTheme.typography.titleMediumEmphasized,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                overview.yesterdayExpenses.forEach { expense ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${expense.name} — ${expense.paidBy.name} ${expense.paidBy.surname}".trim(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = formatCurrency(expense.amount),
                                            style = MaterialTheme.typography.labelMediumEmphasized,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                HorizontalDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.titleSmallEmphasized
                                    )
                                    Text(
                                        text = "$${
                                            String.format(
                                                "%.2f",
                                                overview.yesterdayExpenses.sumOf { it.amount })
                                        }",
                                        style = MaterialTheme.typography.titleSmallEmphasized,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        onClick = { })
                }

                // Today's Itinerary Card
                if (overview.todayItinerary.isNotEmpty()) {
                    ICard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Constants.commonCornerRadius),
                        content = {
                            Column(
                                modifier = Modifier.standardPadding(),
                                verticalArrangement = Arrangement.spacedBy(Constants.mediumPadding)
                            ) {
                                Text(
                                    text = "Today's Itinerary",
                                    style = MaterialTheme.typography.titleMediumEmphasized,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                overview.todayItinerary.forEach { itinerary ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = itinerary.time,
                                            style = MaterialTheme.typography.labelMediumEmphasized,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.width(84.dp)
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = itinerary.name,
                                                style = MaterialTheme.typography.labelMediumEmphasized
                                            )
                                            Text(
                                                text = itinerary.location,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        onClick = { })
                }
            }

            is OverviewUiState.Loading -> {
                // Show shimmer loading state
                ICard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerable(),
                    shape = RoundedCornerShape(Constants.commonCornerRadius),
                    content = {
                        Column(
                            modifier = Modifier.standardPadding(),
                            verticalArrangement = Arrangement.spacedBy(smallPadding)
                        ) {
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.shimmerable()
                            )
                        }
                    },
                    onClick = { })
            }

            is OverviewUiState.Error -> {
                // Show error state with fallback to hardcoded data
                ICard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Constants.commonCornerRadius),
                    content = {
                        Column(
                            modifier = Modifier.standardPadding(),
                            verticalArrangement = Arrangement.spacedBy(smallPadding)
                        ) {
                            Text(
                                text = "Failed to load overview data",
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    onClick = { })
            }

            is OverviewUiState.Idle -> {
                // Show placeholder/default state
                ICard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Constants.commonCornerRadius),
                    content = {
                        Column(
                            modifier = Modifier.standardPadding(),
                            verticalArrangement = Arrangement.spacedBy(smallPadding)
                        ) {
                            Text(
                                text = "Yesterday Expenses",
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Breakfast — John",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$25.00",
                                    style = MaterialTheme.typography.labelMediumEmphasized,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Transportation — Sarah",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$15.50",
                                    style = MaterialTheme.typography.labelMediumEmphasized,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    style = MaterialTheme.typography.titleSmallEmphasized
                                )
                                Text(
                                    text = "$40.50",
                                    style = MaterialTheme.typography.titleSmallEmphasized,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    onClick = { })

                // Today's Itinerary
                ICard(modifier = Modifier.fillMaxWidth(), content = {
                    Column(
                        modifier = Modifier.standardPadding(),
                        verticalArrangement = Arrangement.spacedBy(smallPadding)
                    ) {
                        Text(
                            text = "Today's Itinerary",
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "10:00 AM",
                                style = MaterialTheme.typography.labelMediumEmphasized,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(84.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Visit Local Market",
                                    style = MaterialTheme.typography.labelMediumEmphasized
                                )
                                Text(
                                    text = "Location name",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }, onClick = { })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelInfoCardShimmer() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Travel Information",
                style = MaterialTheme.typography.headlineSmallEmphasized,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "More travel information options"
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable()
        ) {
            SelectField(
                value = "Loading...",
                onSelect = { },
                label = "Accommodation",
                leadingIcon = Icons.Rounded.SupervisedUserCircle,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable()
        ) {
            SelectField(
                value = "Loading...",
                onSelect = { },
                label = "Phone Number",
                leadingIcon = Icons.Rounded.SupervisedUserCircle,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.extraSmallPadding)
                .shimmerable(),
            horizontalArrangement = Arrangement.spacedBy(Constants.mediumPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shimmerable()
            ) {
                SelectField(
                    value = "Loading...",
                    onSelect = { },
                    label = "Check-In Date & Time",
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shimmerable()
            ) {
                SelectField(
                    value = "Loading...",
                    onSelect = { },
                    label = "Check-Out Date & Time",
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shimmerable()
        ) {
            SelectField(
                value = "Loading...",
                onSelect = { },
                label = "Reservation Code",
                leadingIcon = null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@DevicePreview
@Composable
fun HomeScreenPreview() {
    PreviewWrapper {
        val mockAccommodation = Accommodation(
            name = "Hotel",
            phone = "1231231",
            checkIn = "2025/10/06",
            checkOut = "2025/11/06",
            location = "Germany",
            mapUri = "test"
        )
        val tripInfo = Trip(
            id = "ITN-51712",
            groupName = "My itinero group name",
            destination = "Germany",
            startDate = "2024/05/05",
            endDate = "2025/04/05",
            summary = "Just a summary regarding this amazin trip to Japan...",
            totalMembers = 2,
            accommodation = mockAccommodation,
            reservationCode = "REV14123",
            extraInfo = "Not provided",
            additionalInfo = "Not provided",
            groupCode = "ITN-51712",
            ownerId = 1.toString(),
        )

        HomeScreen(
            tripId = "ITN-51712",
            uiState = HomeUiState.Idle,
            overviewUiState = OverviewUiState.Idle,
            onShowSnackbar = {},
            onGetTravel = { },
            onGetOverview = { },
            tripInfo = tripInfo,
            onRefresh = { },
        )
    }
}

@DevicePreview
@Composable
fun HomeScreenPreviewLoading() {
    PreviewWrapper {
        val mockAccommodation = Accommodation(
            name = "Hotel",
            phone = "1231231",
            checkIn = "2025/10/06",
            checkOut = "2025/11/06",
            location = "Germany",
            mapUri = "test"
        )
        Trip(
            id = "ITN-51712",
            groupName = "My itinero group name",
            destination = "Germany",
            startDate = "2024/05/05",
            endDate = "2025/04/05",
            summary = "Just a summary regarding this amazin trip to Japan...",
            totalMembers = 2,
            accommodation = mockAccommodation,
            reservationCode = "REV14123",
            extraInfo = "Not provided",
            additionalInfo = "Not provided",
            groupCode = "ITN-51712",
            ownerId = 1.toString(),
        )

        HomeScreenShimmer(
            paddingValues = PaddingValues()
        )
    }
}
