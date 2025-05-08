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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.IFilledTextField

@Composable
fun HomeScreen() {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
        ExpandablePendingInvites(
            isExpanded = isInviteExpanded,
            onExpandedChange = { isInviteExpanded= it }
        )

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

        ExpandableHotelCard(
            isExpanded = isExpanded, onExpandedChange = { isExpanded = it })

        Spacer(modifier = Modifier.height(16.dp))

        TravelInfoCard()
    }
}

@Composable
fun ExpandableHotelCard(
    isExpanded: Boolean, onExpandedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource, indication = null
            ) {
                onExpandedChange(!isExpanded)
            }
            .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hotel/AirBnB Details",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
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
        }
    }
}

@Composable
fun ExpandablePendingInvites(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource, indication = null
            ) {
                onExpandedChange(!isExpanded)
            }
            .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.AddCircleOutline,
                contentDescription = "Add Circle",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Text(
                text = "Pending actions",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f).padding(start = 16.dp)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
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
        }
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
    outlineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    outlineWidth: Dp = 1.dp,
    cardColors: CardColors = CardDefaults.elevatedCardColors(),
) {
    Card(
        modifier = modifier.then(
            Modifier.border(
                width = outlineWidth, color = outlineColor, shape = RoundedCornerShape(16.dp)
            )
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = cardColors
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun PeopleInfoCard(
    confirmedCount: Int,
    names: List<String>,
    modifier: Modifier = Modifier,
    outlineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    cardColors: CardColors = CardDefaults.elevatedCardColors(),
    outlineWidth: Dp = 1.dp
) {
    Card(
        modifier = modifier.then(
            Modifier.border(
                width = outlineWidth, color = outlineColor, shape = RoundedCornerShape(16.dp)
            )
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = cardColors
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "People", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                text = "$confirmedCount total",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
//            Row(
//                modifier = Modifier.padding(top = 4.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                names.take(2).forEach {
//                    Icon(
//                        imageVector = Icons.Rounded.Person,
//                        contentDescription = null
//                    )
//                }
//                if (names.size > 2) {
//                    Text(text = "+${names.size - 2}", color = Color.Gray)
//                }
//            }
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
