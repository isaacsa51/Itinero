package com.serranoie.app.itinero.feature.itinerary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.itinero.navigation.bottombar.BottomBarNav
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

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

    Scaffold(topBar = {
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
        }, scrollBehavior = scrollBehavior
        )
    }, bottomBar = { BottomBarNav(navController = navController) }, floatingActionButton = {
        FloatingActionButton(
            onClick = { /* Handle FAB click */ },
            content = { Icon(Icons.Rounded.Add, contentDescription = "Add") },
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            items(generateDateRange(startDate, endDate)) { date ->
                ItineraryDateSection(
                    date = date, activities = itinerary[date].orEmpty(), onSwiped = onSwiped
                )
            }
        }
    }
}

@Composable
fun ItineraryDateSection(date: LocalDate, activities: List<ItineraryItem>, onSwiped: () -> Unit) {
    Row() {
        Column(
            modifier = Modifier.weight(0.15f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("dd")),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM")).uppercase(),
                style = MaterialTheme.typography.labelSmall
            )
        }

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
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)
            } else {
                activities.forEach { activity ->
                    SwipeableCard(item = activity, onSwiped = onSwiped)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SwipeableCard(item: ItineraryItem, onSwiped: () -> Unit) {

    var isCompleted by remember { mutableStateOf(item.isCompleted) }

    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth()
            .pointerInput(Unit) {
                val velocityTracker = VelocityTracker()

                detectHorizontalDragGestures(
                    onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity()
                    if (velocity.x.absoluteValue > 1000f) {
                        isCompleted = true
                        onSwiped()
                    }
                },
                    onDragCancel = { velocityTracker.resetTracking() },
                    onHorizontalDrag = { change, dragAmount ->
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // You could also add some visual feedback here if needed
                    })
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.elevatedCardColors(
            if (isCompleted) MaterialTheme.colorScheme.tertiaryContainer.copy(
                alpha = 0.5f
            ) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "🕒 ${item.time}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "📍 ${item.location}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "❓ ${item.description}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

// Data model for itinerary item
data class ItineraryItem(
    val title: String,
    val time: String,
    val location: String,
    val description: String,
    var isCompleted: Boolean = false
)

fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
    return generateSequence(start) { it.plusDays(1) }.takeWhile { it <= end }.toList()
}

@ThemePreviews
@Composable
fun OnboardScreenPreview() {
    val startDate = LocalDate.now()
    val endDate = startDate.plusDays(3)
    val dateRange = generateDateRange(startDate, endDate)

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
fun SegmentPathDivider(
    modifier: Modifier = Modifier,
    width: Dp = 20.dp,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) = Canvas(
    modifier
        .fillMaxHeight()
        .width(width)
) {
    drawLine(
        color = color,
        strokeWidth = thickness.toPx(),
        start = Offset(thickness.toPx() / 2 + width.toPx() * 0.25f, size.height),
        end = Offset(thickness.toPx() / 2 + width.toPx() * 0.75f, 0f)
    )
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