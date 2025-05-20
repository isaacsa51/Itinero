package com.serranoie.app.feature.itinerary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystem.ui.theme.component.OutlinedCard
import com.serranoie.app.feature.itinerary.util.generateDateRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    navController: NavController,
    itinerary: Map<LocalDate, List<ItineraryItem>>,
    onSwiped: () -> Unit
) {
    val startDate = itinerary.keys.minOrNull() ?: LocalDate.now()
    val endDate = itinerary.keys.maxOrNull() ?: startDate
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ), title = {
                    Text(
                        "Itinerary", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }, content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    })
                }, actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle FAB click */ },
                content = { Icon(Icons.Rounded.Add, contentDescription = "Add") },
            )
        }
    ) { paddingValues ->
        val itineraryState = remember { mutableStateOf(itinerary) }
        val currentItinerary by itineraryState

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            items(generateDateRange(startDate, endDate)) { date ->
                ItineraryDateSection(
                    date = date, activities = currentItinerary[date].orEmpty(), onSwiped = {
                        // Mark as complete logic
                        val newItinerary = currentItinerary.toMutableMap()
                        newItinerary[date] = newItinerary[date]?.map {
                            if (it.isCompleted) it else it.copy(isCompleted = true)
                        } ?: emptyList()
                        itineraryState.value = newItinerary
                    }
                )
            }
        }
    }
}

@Composable
fun ItineraryDateSection(date: LocalDate, activities: List<ItineraryItem>, onSwiped: () -> Unit) {
    Row {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            if (activities.isEmpty()) {
                Text(
                    text = "NO ITEMS ADDED AT THIS DATE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp
                )
            } else {
                activities.forEach { activity ->
                    OutlinedCard(
                        swipeable = true,
                        isCompleted = activity.isCompleted,
                        onSwipe = onSwiped,
                        headerTitle = activity.title,
                        headerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                            Text(
                                text = "🕒 ${activity.time}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "📍 ${activity.location}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "❓ ${activity.description}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Make the divider match the card width instead of full width
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

// Data model for itinerary item
data class ItineraryItem(
    val title: String,
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
                title = "Visit Eiffel Tower",
                time = "10:00 AM",
                location = "Champ de Mars, Paris",
                description = "Enjoy the view from the top",
                isCompleted = false
            ), ItineraryItem(
                title = "Lunch at Le Jules Verne",
                time = "1:00 PM",
                location = "Eiffel Tower, 2nd floor",
                description = "Reservation under Smith",
                isCompleted = true
            )
        ), startDate.plusDays(1) to listOf(
            ItineraryItem(
                title = "Louvre Museum",
                time = "9:30 AM",
                location = "Rue de Rivoli, Paris",
                description = "Don't miss the Mona Lisa",
                isCompleted = false
            )
        ), startDate.plusDays(2) to emptyList()
    )

    PreviewWrapper {
        ItineraryScreen(
            navController = rememberNavController(), itinerary = mockItineraryData, onSwiped = {})
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) = Canvas(
    modifier
        .fillMaxHeight()
        .width(thickness)
) {
    drawLine(
        color = color,
        strokeWidth = thickness.toPx(),
        start = Offset(thickness.toPx() / 2, 0f),
        end = Offset(thickness.toPx() / 2, size.height),
    )
}