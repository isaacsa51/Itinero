package com.serranoie.app.feature.home

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Screen
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.IFilledTextField
import com.serranoie.app.designsystem.ui.theme.component.card.ExpandableCard
import com.serranoie.app.feature.home.navigation.bottombar.BottomBarNav


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


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
            onClick = { /* Handle FAB click */ },
            icon = { Icon(Icons.Rounded.AddCircleOutline, contentDescription = "Add") },
            text = { Text("Add Trip") },
            expanded = true
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), true)
                .padding(paddingValues)
        ) {
            TripDetailsScreen(navController)
        }
    }
}

@Composable
fun TripDetailsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }
    var isInviteExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExpandableCard(
            title = "Pending actions",
            isExpanded = isInviteExpanded,
            onExpandedChange = { isInviteExpanded = it },
            headerIcon = Icons.Rounded.AddCircleOutline,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            titleStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        ) {
            Text(
                text = "map sdk location holder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ADDRESS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = "Antonio Dovali Jaime 70, Santa Fe, Zedec Sta Fé, Álvaro Obregón, 01219 Mexico City, CDMX",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DestinationCard(
            country = "Germany",
            route = "Country 1 > Country 2",
            flightTime = "2 h 25 min flight",
            navController = navController
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateInfoCard(
                title = "Travel date",
                value = "5 days",
                subtitle = "08/02/2025 - 13/02/2025",
                modifier = Modifier.weight(1f)
            )
            PeopleInfoCard(
                confirmedCount = 3, names = listOf("Isaac", "Name"), modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SummarySection()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Accommodation",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExpandableCard(
            title = "Hotel/AirBnB Details",
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
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = "Antonio Dovali Jaime 70, Santa Fe, Zedec Sta Fé, Álvaro Obregón, 01219 Mexico City, CDMX",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TravelInfoCard(navController)
    }
}

@Composable
fun DestinationCard(
    country: String, route: String, flightTime: String, navController: NavHostController
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
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.clickable { navController.navigate(Screen.TRIP_SETTINGS.name) },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = country,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = route, style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = flightTime, style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PeopleInfoCard(
    confirmedCount: Int,
    names: List<String>,
    modifier: Modifier = Modifier,
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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "$confirmedCount total",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                names.take(names.size).forEach {
                    Icon(
                        imageVector = Icons.Rounded.SupervisedUserCircle, contentDescription = null
                    )
                }
                if (names.size > 2) {
                    Text(text = "+${names.size - 2}", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun TravelInfoCard(navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Travel Information",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { navController.navigate(Screen.TRIP_INFO.name) }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "More travel information options"
                )
            }
        }

        IFilledTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = "",
            onValueChange = {},
            label = "Destination",
            placeholder = "Enter your destination"
        )
    }
}

@Composable
fun SummarySection() {
    Column {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Brief user summary goes here. AI summary will be set below users one...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "• Keep the summary concise (2-3 sentences max).\n" + "• Highlight key aspects.\n" + "• Allow users to edits text.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@ThemePreviews
@Composable
fun HomeScreenPreview() {
    PreviewWrapper {
        HomeScreen()
    }
}
