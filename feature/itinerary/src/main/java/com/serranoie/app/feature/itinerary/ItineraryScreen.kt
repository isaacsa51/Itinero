package com.serranoie.app.feature.itinerary

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.card.SwipeCardAction
import com.serranoie.app.designsystemlib.ui.theme.component.card.SwipeableCard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.largePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.app.feature.itinerary.util.generateDateRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }, content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    })
                }, scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        val hasAnyItems = itinerary.values.any { it.isNotEmpty() }

        PullToRefreshBox(
            isRefreshing = uiState is ItineraryUiState.Loading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedContent(targetState = uiState, transitionSpec = {
                if (targetState is ItineraryUiState.Loading) {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                } else {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            }) { targetState ->
                if (targetState is ItineraryUiState.Loading && itinerary.isEmpty()) {
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
                                    isLastSection = index == 2,
                                    onActivitySwiped = { _, _ -> })
                            }
                        }
                    }
                } else {
                    ItineraryContent(
                        hasAnyItems = hasAnyItems,
                        itinerary = itinerary,
                        startDate = startDate,
                        endDate = endDate,
                        onToggleCompletion = onToggleCompletion,
                        onSwiped = onSwiped,
                        onActivityClick = onActivityClick,
                        scrollBehavior = scrollBehavior,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItineraryContent(
    hasAnyItems: Boolean,
    itinerary: Map<LocalDate, List<ItineraryItem>>,
    startDate: LocalDate,
    endDate: LocalDate,
    onToggleCompletion: (String) -> Unit,
    onSwiped: () -> Unit,
    onActivityClick: (ItineraryItem) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
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
                        .padding(largePadding - extraSmallPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.35F),
                        painter = painterResource(id = R.drawable.itinerary_image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "No itinerary items found",
                        style = MaterialTheme.typography.headlineMediumEmphasized,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(smallPadding))
                    Text(
                        text = "Add some activities to get started with your trip planning",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
                }
            }
        } else {
            val dateRange = generateDateRange(startDate, endDate)
            itemsIndexed(dateRange) { index, date ->
                ItineraryDateSection(
                    date = date,
                    activities = itinerary[date].orEmpty(),
                    isLastSection = index == dateRange.size - 1,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItineraryDateSection(
    date: LocalDate,
    activities: List<ItineraryItem>,
    isLastSection: Boolean,
    onActivitySwiped: (ItineraryItem, Boolean) -> Unit,
    onActivityClick: (ItineraryItem) -> Unit = {}
) {
    Row {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = basePadding)
        ) {
            if (activities.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NO ITEMS ADDED AT THIS DATE",
                        style = MaterialTheme.typography.labelSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.standardPadding(
                            horizontal = 0.dp, vertical = basePadding
                        ),
                    )
                }
                if (!isLastSection) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = smallPadding),
                        thickness = borderStrokeWidth
                    )
                }
            } else {
                activities.forEachIndexed { index, activity ->
                    SwipeableCard(
                        modifier = Modifier
                            .padding(bottom = smallPadding)
                            .shimmerable(),
                        content = {
                            Column {
                                Text(
                                    text = "${activity.location} — ${activity.time}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = activity.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        isCompleted = activity.isCompleted,
                        headerTitle = activity.name,
                        headerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        headerIcon = if (activity.isCompleted) Icons.Default.CheckCircle else null,
                        rightSwipeAction = if (!activity.isCompleted) {
                            SwipeCardAction(
                                icon = Icons.Default.Check,
                                background = MaterialTheme.colorScheme.primaryContainer,
                                onAction = {
                                    Log.d(
                                        "ITINERO - ItineraryScreen",
                                        "Marking activity as completed: ${activity.name} (ID: ${activity.id})"
                                    )
                                    onActivitySwiped(activity, true)
                                },
                                toastMessage = "Activity marked as done"
                            )
                        } else {
                            SwipeCardAction(
                                icon = Icons.Default.Close,
                                background = MaterialTheme.colorScheme.errorContainer,
                                onAction = {
                                    onActivitySwiped(activity, false)
                                },
                                toastMessage = "Activity marked as completed"
                            )
                        },
                        onClick = { onActivityClick(activity) })
                }

                if (!isLastSection) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(), thickness = borderStrokeWidth
                        )
                    }

                    Spacer(modifier = Modifier.height(smallPadding))
                }
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

@DevicePreview
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
        ), startDate.plusDays(2) to emptyList(), startDate.plusDays(3) to listOf(
            ItineraryItem(
                id = "4",
                name = "Visit Taj Mahal",
                date = "2023-10-04",
                time = "4:20 PM",
                location = "Taj Mahal, Agra",
                description = "Experience the ancient Taj Mahal",
                isCompleted = false
            )
        )
    )

    PreviewWrapper {
        ItineraryScreen(
            navController = rememberNavController(),
            itinerary = mockItineraryData,
            uiState = ItineraryUiState.Idle,
            onRefresh = {},
            onToggleCompletion = {},
            onSwiped = {},
            onActivityClick = {})
    }
}

@DevicePreview
@Composable
private fun LoadingItineraryPreview() {
    PreviewWrapper {
        ItineraryScreen(
            navController = rememberNavController(),
            itinerary = emptyMap(),
            uiState = ItineraryUiState.Loading,
            onRefresh = {},
            onToggleCompletion = {},
            onSwiped = {})
    }
}