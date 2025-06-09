package com.serranoie.app.feature.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.SelectField
import com.serranoie.app.designsystem.ui.theme.component.card.ExpandableCard
import com.serranoie.itinero.core.domain.model.Trip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    tripId: String,
    uiState: HomeUiState,
    onShowSnackbar: suspend (String) -> Unit,
    onGetTravel: () -> Unit,
    tripInfo: Trip?
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val isRefreshing = uiState is HomeUiState.Loading
    val onRefresh: () -> Unit = { onGetTravel() }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Error) {
            onShowSnackbar(uiState.message)
        }
        onGetTravel()
    }

    Log.d("ISAAC", "$uiState")

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(
            title = {
                Text(
                    "Itinero", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            },
            scrollBehavior = scrollBehavior,
        )
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = {
            navController.navigate(
                Route.TripSettings.createRoute(
                    tripId = tripId
                )
            )
        },
            icon = { Icon(Icons.Rounded.Edit, contentDescription = "Edit") },
            text = { Text("Edit") },
            expanded = true
        )
    }) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = isRefreshing, onRefresh = onRefresh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(), true)
                    .padding(paddingValues)
            ) {
                TripDetailsScreen(navController, tripId, tripInfo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripDetailsScreen(
    navController: NavHostController,
    tripId: String,
    tripInfo: Trip?
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DestinationCard(
            country = tripInfo?.destination.toString(),
            navController = navController,
            tripId = tripId,
            tripInfo = tripInfo
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateInfoCard(
                title = "Travel date",
                value = "5 days", // TODO: Calculate days from the trip values
                subtitle = tripInfo?.startDate + " - " + tripInfo?.endDate,
                modifier = Modifier.weight(1f)
            )
            tripInfo?.let {
                PeopleInfoCard(
                    confirmedCount = tripInfo.totalMembers ?: 0, modifier = Modifier.weight(1f), tripInfo = it,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tripInfo != null) {
            SummarySection(
                onClick = { }, tripInfo = tripInfo)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Accommodation", style = MaterialTheme.typography.headlineSmallEmphasized
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExpandableCard(
            title = "Details",
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            showDivider = true
        ) {
            Text(
                text = "map sdk location holder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ADDRESS",
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = tripInfo?.accommodation?.location.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tripInfo != null) {
            TravelInfoCard(navController, tripInfo)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DestinationCard(
    country: String,
    navController: NavHostController,
    tripId: String,
    tripInfo: Trip?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destination",
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                BadgedBox(
                    badge = {
                        Badge()
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.clickable {
                            navController.navigate(
                                Route.TripSettings.createRoute(
                                    tripId = tripId
                                )
                            )
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = country, style = MaterialTheme.typography.displayMediumEmphasized
            )
            Spacer(modifier = Modifier.height(4.dp))
            // TODO: Create and get the group name, if user didn't set any name, display the group code
            if (tripInfo?.groupCode != null) {
                Text(
                    text = "Group Name", style = MaterialTheme.typography.labelLargeEmphasized
                )
            } else {
                Text(
                    text = "Group Name", style = MaterialTheme.typography.labelLargeEmphasized
                )
            }
        }
    }
}

/* TODO: Change 5 days title depending of the context of the user date to show different thing
    - To begin
    - N days (when date is in the data)
    - Completed
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateInfoCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.elevatedCardColors(),
) {
    OutlinedCard(
        modifier = modifier, shape = RoundedCornerShape(16.dp), colors = cardColors
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value, style = MaterialTheme.typography.headlineSmallEmphasized
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
    cardColors: CardColors = CardDefaults.elevatedCardColors(),
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = cardColors,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "People",
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "$confirmedCount total",
                style = MaterialTheme.typography.headlineSmallEmphasized
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (confirmedCount <= 0) {
                    Text(text = "Waiting people to join", style = MaterialTheme.typography.labelSmall)
                } else if(confirmedCount == tripInfo.totalMembers) {
                    Text(text = "Group ready", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
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
                overflow = TextOverflow.Ellipsis
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
                .padding(vertical = 4.dp)
        )

        SelectField(
            value = tripInfo.accommodation.phone,
            onSelect = { },
            label = "Phone Number",
            leadingIcon = Icons.Rounded.SupervisedUserCircle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectField(
                value = tripInfo.accommodation.checkIn,
                onSelect = { },
                label = "Check-In Date & Time",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
            SelectField(
                value = tripInfo.accommodation.checkOut,
                onSelect = { },
                label = "Check-Out Date & Time",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
        }

        SelectField(
            value = tripInfo.reservationCode,
            onSelect = { },
            label = "Reservation Code",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SummarySection(onClick: () -> Unit, tripInfo: Trip) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(horizontal = 16.dp)) {
        Text(
            text = "Summary", style = MaterialTheme.typography.headlineSmallEmphasized
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tripInfo.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AI summary generated by Gemini would be here for the moment...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@ThemePreviews
@Composable
fun HomeScreenPreview() {
    PreviewWrapper {
        val tripId = "123"
        val tripInfo = Trip(
            id = tripId,
            destination = "Germany",
            startDate = "2",
            endDate = TODO(),
            summary = TODO(),
            totalMembers = TODO(),
            accommodation = TODO(),
            reservationCode = TODO(),
            extraInfo = TODO(),
            additionalInfo = TODO(),
            groupCode = TODO(),
            ownerId = TODO(),
        )

        HomeScreen(
            tripId = tripId,
            uiState = HomeUiState.Idle,
            onShowSnackbar = {},
            onGetTravel = { },
            tripInfo = tripInfo
        )
    }
}
