package com.serranoie.app.itinero.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Person2
import androidx.compose.material.icons.rounded.Person4
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.IFilledTextField
import com.serranoie.app.designsystem.ui.theme.component.card.ExpandableCard
import com.serranoie.app.itinero.navigation.bottombar.BottomBarNav

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomBarNav(navController = navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Handle FAB click */ },
                icon = { Icon(Icons.Rounded.AddCircleOutline, contentDescription = "Add") },
                text = { Text("Add Trip") },
                expanded = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), true)
                .padding(paddingValues)
        ) {
            TripDetailsScreen()
        }
    }
}

@Composable
fun TripDetailsScreen() {
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
            // Content remains the same
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
            country = "Germany", route = "Country 1 > Country 2", flightTime = "2 h 25 min flight"
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
                confirmedCount = 3, 
                names = listOf("Isaac", "Name"),
                modifier = Modifier.weight(1f)
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

        TravelInfoCard()
    }
}

@Composable
fun DestinationCard(country: String, route: String, flightTime: String) {
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
                    modifier = Modifier.clickable { /* Handle settings click */ },
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
                    text = route,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = flightTime,
                    style = MaterialTheme.typography.bodyMedium
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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = cardColors
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
   ){
       Column(
           modifier = Modifier.padding(16.dp)
       ) {
           Text(text = "People", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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
                       imageVector = Icons.Rounded.SupervisedUserCircle,
                       contentDescription = null
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
fun TravelInfoCard() {
    Column {
        Text(
            text = "Travel Information",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

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
fun OnboardScreenPreview() {
    PreviewWrapper {
        HomeScreen()
    }
}
