package com.serranoie.app.designsystem.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.OtpDisplayField
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.SelectField
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpandableCard
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard

data class ExampleCategory(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamplesScreen() {
    var selectedCategory by remember { mutableStateOf<ExampleCategory?>(null) }

    val categories = listOf(
        ExampleCategory(
            title = "Home",
            description = "Main dashboard and overview",
            icon = Icons.Default.Home
        ),
        ExampleCategory(
            title = "Itinerary",
            description = "Travel plans and schedules",
            icon = Icons.Default.Map
        ),
        ExampleCategory(
            title = "Expenses",
            description = "Budget tracking and expenses",
            icon = Icons.Default.MonetizationOn
        ),
        ExampleCategory(
            title = "Chat",
            description = "Messages and communication",
            icon = Icons.Default.Chat
        ),
        ExampleCategory(
            title = "Settings",
            description = "App preferences and configuration",
            icon = Icons.Default.Settings
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedCategory != null) {
            TopAppBar(
                title = { Text(selectedCategory!!.title) },
                navigationIcon = {
                    IconButton(onClick = { selectedCategory = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            ExampleDetailScreen(selectedCategory!!)
        } else {
            TopAppBar(
                title = { Text("Examples") }
            )
            ExampleCategoriesScreen(
                categories = categories,
                onCategorySelected = { selectedCategory = it }
            )
        }
    }
}

@Composable
fun ExampleCategoriesScreen(
    categories: List<ExampleCategory>,
    onCategorySelected: (ExampleCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = { onCategorySelected(category) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleDetailScreen(category: ExampleCategory) {
    when (category.title) {
        "Home" -> HomeExampleContent()
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${category.title} Screen Content",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun HomeExampleContent() {
    var isAccommodationExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Destination Card
        MockDestinationCard(
            destination = "Tokyo, Japan",
            groupName = "Tokyo Adventure Group"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MockInfoCard(
                title = "Travel date",
                value = "5 days left",
                subtitle = "Dec 15 - Dec 22",
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
            )

            MockInfoCard(
                title = "People",
                value = "4 total",
                subtitle = "Group ready",
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Section
        MockSummarySection()

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Tasks Section
        MockTodayTasksSection()

        Spacer(modifier = Modifier.height(16.dp))

        // Accommodation Section
        Text(
            text = "Accommodation",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExpandableCard(
            title = "Details",
            isExpanded = isAccommodationExpanded,
            onExpandedChange = { isAccommodationExpanded = it },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            showDivider = true
        ) {
            Text(
                text = "Map location would be displayed here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ADDRESS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = "1-1-1 Shibuya, Tokyo, Japan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Travel Information Section
        MockTravelInfoSection()

        Spacer(modifier = Modifier.height(32.dp))

        // Screen Divider
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outline
        )

        // Trip Settings Screen Label
        Text(
            text = "Trip Settings Screen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Trip Settings Content
        MockTripSettingsContent()
    }
}

@Composable
fun MockTripSettingsContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Group Code Card
        MockGroupCodeCard()

        Spacer(modifier = Modifier.height(24.dp))

        // Trip Information Section
        MockTripInfoSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Group Management Section
        MockGroupManagementSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Danger Zone Section
        MockDangerZoneSection()
    }
}

@Composable
fun MockGroupCodeCard() {
    ICard(
        modifier = Modifier.fillMaxWidth(),
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ITINERO GROUP CODE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                OtpDisplayField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    otpText = "51712"
                )

                Text(
                    text = "What's this code for?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "This code is your trip's unique invitation identifier. Share it only with people you want to invite to your Itinero planning group.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Only the trip creator can manage group members.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        onClick = { }
    )
}

@Composable
fun MockTripInfoSection() {
    PaddedListGroup(title = "Trip Information".uppercase()) {
        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.First
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Trip Name", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Tokyo Adventure Group", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Trip Dates", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Dec 15, 2024 - Dec 22, 2024", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Accommodation Name", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Hotel Shibuya Sky", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Accommodation Location", style = MaterialTheme.typography.bodyLarge)
                Text(text = "1-1-1 Shibuya, Tokyo, Japan", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Last
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Accommodation Number", style = MaterialTheme.typography.bodyLarge)
                Text(text = "+81-3-1234-5678", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun MockGroupManagementSection() {
    PaddedListGroup(title = "GROUP MANAGEMENT") {
        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.First
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Invite New Member", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Share invitation code with others",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Manage Members", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Review pending invitations and members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Last
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Transfer Ownership", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Change the trip administrator",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MockDangerZoneSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "DANGER ZONE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        IButton(
            text = {
                Text(
                    text = "Leave Trip Group",
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            importance = ButtonImportance.Secondary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        IButton(
            text = {
                Text(
                    text = "Delete Trip",
                )
            },
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            importance = ButtonImportance.Error,
        )
    }
}

@Composable
fun MockDestinationCard(
    destination: String,
    groupName: String
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
            Text(
                text = "Destination",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = destination,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = groupName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MockInfoCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    ICard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        content = {
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
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = { }
    )
}

@Composable
fun MockSummarySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "An exciting 7-day adventure through Tokyo, exploring traditional temples, modern districts, and experiencing authentic Japanese culture including visits to Senso-ji Temple, Shibuya Crossing, and Mount Fuji day trip.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AI-generated insights: Perfect timing for cherry blossom season. Recommended budget: ¥15,000 per day. Weather forecast shows mild temperatures ideal for walking tours.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MockTodayTasksSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Expense Summary Card
        ICard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            content = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Expense Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Breakfast",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "¥2,500",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Train tickets",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "¥800",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Today",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "¥3,300",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Today's Itinerary Card
        ICard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            content = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Today's Itinerary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Itinerary Item 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "9:00 AM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(80.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Senso-ji Temple",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Visit Tokyo's oldest temple in Asakusa",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Itinerary Item 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "2:00 PM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(80.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Shibuya Crossing",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Experience the world's busiest pedestrian crossing",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Itinerary Item 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "7:00 PM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(80.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Izakaya Dinner",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Traditional Japanese pub experience",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            onClick = { }
        )
    }
}

@Composable
fun MockTravelInfoSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Travel Information",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit travel information"
                )
            }
        }

        SelectField(
            value = "Hotel Shibuya Sky",
            onSelect = { },
            label = "Accommodation",
            leadingIcon = Icons.Rounded.SupervisedUserCircle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        SelectField(
            value = "+81-3-1234-5678",
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
                value = "Dec 15, 3:00 PM",
                onSelect = { },
                label = "Check-In",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
            SelectField(
                value = "Dec 22, 11:00 AM",
                onSelect = { },
                label = "Check-Out",
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
        }

        SelectField(
            value = "TKY-ABC-123",
            onSelect = { },
            label = "Reservation Code",
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@ThemePreviews
@Composable
fun ExamplesScreenPreview() {
    PreviewWrapper {
        ExamplesScreen()
    }
}