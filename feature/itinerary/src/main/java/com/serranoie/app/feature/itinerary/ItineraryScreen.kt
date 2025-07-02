package com.serranoie.app.feature.itinerary

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.theme.component.card.SwipeActionsConfig
import com.serranoie.app.designsystemlib.ui.theme.component.shimmerable
import com.serranoie.app.feature.itinerary.util.generateDateRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    navController: NavController,
    itinerary: Map<LocalDate, List<ItineraryItem>>,
    uiState: ItineraryUiState,
    onRefresh: () -> Unit,
    onToggleCompletion: (String) -> Unit,
    onSwiped: () -> Unit,
    onActivityClick: (ItineraryItem) -> Unit = {}
) {
    val startDate = itinerary.keys.minOrNull() ?: LocalDate.now()
    val endDate = itinerary.keys.maxOrNull() ?: startDate
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Itinerary", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }, content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    })
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        val hasAnyItems = itinerary.values.any { it.isNotEmpty() }

        PullToRefreshBox(
            isRefreshing = uiState is ItineraryUiState.Loading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState is ItineraryUiState.Loading && itinerary.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(paddingValues)
                ) {
                    items(3) { index ->
                        ShimmerProvider {
                            ItineraryDateSection(
                                date = LocalDate.now().plusDays(index.toLong()),
                                activities = listOf(
                                    ItineraryItem(
                                        id = "loading_$index",
                                        name = "Loading activity",
                                        date = "2023-10-01",
                                        time = "Loading time",
                                        location = "Loading location",
                                        description = "Loading description"
                                    )
                                ),
                                onActivitySwiped = { _, _ -> }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(paddingValues)
                ) {
                    if (!hasAnyItems) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No itinerary items found",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add some activities to get started with your trip planning",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        items(generateDateRange(startDate, endDate)) { date ->
                            ItineraryDateSection(
                                date = date,
                                activities = itinerary[date].orEmpty(),
                                onActivitySwiped = { swipedActivity, isCompleting ->
                                    Log.d(
                                        "ITINERO - ItineraryScreen",
                                        "Swiped activity: ${swipedActivity.name}, isCompleting: $isCompleting"
                                    )

                                    if (!swipedActivity.id.isNullOrEmpty()) {
                                        onToggleCompletion(swipedActivity.id)
                                    } else {
                                        Log.w(
                                            "ITINERO - ItineraryScreen",
                                            "Activity has no ID, cannot toggle completion"
                                        )
                                        onSwiped()
                                    }
                                },
                                onActivityClick = onActivityClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItineraryDateSection(
    date: LocalDate,
    activities: List<ItineraryItem>,
    onActivitySwiped: (ItineraryItem, Boolean) -> Unit,
    onActivityClick: (ItineraryItem) -> Unit = {}
) {
    Row {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            if (activities.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NO ITEMS ADDED AT THIS DATE",
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp
                )
            } else {
                activities.forEach { activity ->
                    ICard(
                        swipeable = true,
                        isCompleted = activity.isCompleted,
                        headerTitle = activity.name,
                        headerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        swipeActionsConfig = if (!activity.isCompleted) {
                            SwipeActionsConfig(
                                threshold = 0.3f,
                                icon = Icons.Default.Check,
                                iconTint = MaterialTheme.colorScheme.onPrimary,
                                background = MaterialTheme.colorScheme.primary,
                                stayDismissed = false,
                                onDismiss = { onActivitySwiped(activity, true) })
                        } else {
                            SwipeActionsConfig(
                                threshold = 0.3f,
                                icon = Icons.Default.Close,
                                iconTint = MaterialTheme.colorScheme.onError,
                                background = MaterialTheme.colorScheme.error,
                                stayDismissed = false,
                                onDismiss = { onActivitySwiped(activity, false) })
                        },
                        onSwipe = {
                            onActivitySwiped(activity, !activity.isCompleted)
                        },
                        content = {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Text(
                                    text = "🕒 ${activity.time} ~ ${activity.date}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.shimmerable()
                                )
                                Text(
                                    text = "📍 ${activity.location}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.shimmerable()
                                )
                                Text(
                                    text = "❓ ${activity.description}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.shimmerable()
                                )
                            }
                        },
                        onClick = { onActivityClick(activity) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class ItineraryItem(
    val id: String? = null,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    var isCompleted: Boolean = false
)

@ThemePreviews
@Composable
private fun ItineraryScreenPreview() {
    val startDate = LocalDate.now()

    val mockItineraryData = mapOf(
        startDate to listOf(
            ItineraryItem(
                id = "1", // Add ID for preview
                name = "Visit Eiffel Tower",
                date = "2023-10-01",
                time = "10:00 AM",
                location = "Champ de Mars, Paris",
                description = "Enjoy the view from the top",
                isCompleted = false
            ), ItineraryItem(
                id = "2", // Add ID for preview
                name = "Lunch at Le Jules Verne",
                date = "2023-10-01",
                time = "1:00 PM",
                location = "Eiffel Tower, 2nd floor",
                description = "Reservation under Smith",
                isCompleted = true
            )
        ), startDate.plusDays(1) to listOf(
            ItineraryItem(
                id = "3", // Add ID for preview
                name = "Louvre Museum",
                date = "2023-10-02",
                time = "9:30 AM",
                location = "Rue de Rivoli, Paris",
                description = "Don't miss the Mona Lisa",
                isCompleted = false
            )
        ), startDate.plusDays(2) to emptyList()
    )

    PreviewWrapper {
        ItineraryScreen(
            navController = rememberNavController(),
            itinerary = mockItineraryData,
            uiState = ItineraryUiState.Idle,
            onRefresh = {},
            onToggleCompletion = {},
            onSwiped = {},
            onActivityClick = { item: ItineraryItem ->
                Log.d("ITINERO - ITNavGraph", "=== ITEM CLICKED ===")
                Log.d("ITINERO - ITNavGraph", "Item ID: ${item.id}")
            }
        )
    }
}

@ThemePreviews
@Composable
private fun LoadingItineraryPreview() {
    PreviewWrapper {
        ItineraryScreen(
            navController = rememberNavController(),
            itinerary = emptyMap(),
            uiState = ItineraryUiState.Loading,
            onRefresh = {},
            onToggleCompletion = {},
            onSwiped = {}
        )
    }
}