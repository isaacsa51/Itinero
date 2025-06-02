package com.serranoie.app.feature.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.ComponentPreview
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.feature.TravelUiState
import com.serranoie.itinero.core.domain.model.Travel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelListScreen(
    uiState: TravelUiState,
    travels: List<Travel>,
    onGetAllTravels: () -> Unit,
    onResetState: () -> Unit,
    onCreateTravelClick: () -> Unit,
    onTravelClick: (String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        onGetAllTravels()
    }

    LaunchedEffect(uiState) {
        if (uiState is TravelUiState.Error) {
            onShowSnackbar(uiState.message)
            onResetState()
        }
    }

    Scaffold(topBar = {
        LargeTopAppBar(
            title = { Text("My Trips") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onCreateTravelClick) {
            Icon(Icons.Default.Add, contentDescription = "Add Trip")
        }
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState is TravelUiState.Loading -> {
                    LoadingIndicator()
                }

                travels.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No trips found", style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to create your first trip",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), contentPadding = padding
                    ) {
                        items(travels) { travel ->
                            TravelItem(
                                travel = travel, onClick = { travel.id?.let { onTravelClick(it) } })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelItem(
    travel: Travel, onClick: () -> Unit
) {
    val tripStatus = determineTripStatus(travel.startDate, travel.endDate)
    val statusTextColor = when (tripStatus) {
        "Pending" -> MaterialTheme.colorScheme.onSecondaryContainer
        "In Progress" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Completed" -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

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
                        text = travel.destination,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (travel.isOwner) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Owner",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Group trip name",
                    style = MaterialTheme.typography.titleSmallEmphasized
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    Text(
                        text = tripStatus.format(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = statusTextColor
                    )
                    Text(
                        text = "${travel.startDate} - ${travel.endDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "Accommodation:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = travel.accommodation,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall
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
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleLargeEmphasized.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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

@ThemePreviews
@Composable
private fun TravelListPreview() {
    PreviewWrapper {
        TravelListScreen(
            uiState = TravelUiState.Idle,
            travels = emptyList(),
            onGetAllTravels = {},
            onResetState = {},
            onCreateTravelClick = {},
            onTravelClick = {},
            onShowSnackbar = {})
    }
}

@ComponentPreview
@Composable
private fun TravelItemPreview() {
    val mockTravel = Travel(
        id = "1",
        groupCode = "PAR24",
        destination = "Paris, France",
        startDate = "2025-12-01",
        endDate = "2025-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = "Le Meurice Hotel",
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        isOwner = false
    )

    val mockOwnerTravelPending = Travel(
        id = "1",
        groupCode = "PAR24",
        destination = "Tokyo, Japan",
        startDate = "2025-12-01",
        endDate = "2025-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = "Le Meurice Hotel",
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        isOwner = true
    )

    val mockOwnerTravelProgress = Travel(
        id = "1",
        groupCode = "PAR24",
        destination = "Paris, France",
        startDate = "2025-05-25",
        endDate = "2025-05-30",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = "Le Meurice Hotel",
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        isOwner = true
    )

    val mockTravelCompleted = Travel(
        id = "1",
        groupCode = "PAR24",
        destination = "Paris, France",
        startDate = "2024-12-01",
        endDate = "2024-12-31",
        summary = "Romantic getaway exploring the City of Light with visits to the Eiffel Tower, Louvre Museum, and charming cafés",
        accommodation = "Le Meurice Hotel",
        reservationCode = "LMH-2024-098",
        extraInfo = "Winter season with holiday decorations",
        additionalInfo = "Museum passes and restaurant reservations confirmed",
        isOwner = false
    )

    PreviewWrapper {

        Column {
            TravelItem(
                travel = mockTravel, onClick = {})

            TravelItem(
                travel = mockOwnerTravelProgress, onClick = {})

            TravelItem(
                travel = mockOwnerTravelPending, onClick = {})

            TravelItem(
                travel = mockTravelCompleted, onClick = {})
        }
    }
}
