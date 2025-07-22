package com.serranoie.app.designsystem.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.theme.component.BalanceCircles
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.JumpToBottom
import com.serranoie.app.designsystemlib.ui.theme.component.OtpDisplayField
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.SelectField
import com.serranoie.app.designsystemlib.ui.theme.component.UserInput
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubbleWithAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpandableCard
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpenseCard
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.theme.component.card.SwipeActionsConfig
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExampleCategory(
    val title: String, val description: String, val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamplesScreen() {
    var selectedCategory by remember { mutableStateOf<ExampleCategory?>(null) }

    val categories = listOf(
        ExampleCategory(
            title = "Home", description = "Main dashboard and overview", icon = Icons.Default.Home
        ), ExampleCategory(
            title = "Itinerary",
            description = "Travel plans and schedules",
            icon = Icons.Default.Map
        ), ExampleCategory(
            title = "Expenses",
            description = "Budget tracking and expenses",
            icon = Icons.Default.MonetizationOn
        ), ExampleCategory(
            title = "Chat", description = "Messages and communication", icon = Icons.Default.Chat
        ), ExampleCategory(
            title = "Settings",
            description = "App preferences and configuration",
            icon = Icons.Default.Settings
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedCategory != null) {
            TopAppBar(title = { Text(selectedCategory!!.title) }, navigationIcon = {
                IconButton(onClick = { selectedCategory = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
            ExampleDetailScreen(selectedCategory!!)
        } else {
            TopAppBar(
                title = { Text("Examples") })
            ExampleCategoriesScreen(
                categories = categories, onCategorySelected = { selectedCategory = it })
        }
    }
}

@Composable
fun ExampleCategoriesScreen(
    categories: List<ExampleCategory>, onCategorySelected: (ExampleCategory) -> Unit
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
                onClick = { onCategorySelected(category) }) {
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
                            text = category.title, style = MaterialTheme.typography.titleMedium
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
        "Itinerary" -> ItineraryExampleContent()
        "Expenses" -> ExpensesExampleContent()
        "Chat" -> ChatExampleContent()
        "Settings" -> SettingsExampleContent()
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
        MockDestinationCard(
            destination = "Tokyo, Japan", groupName = "Tokyo Adventure Group"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
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

        MockSummarySection()

        Spacer(modifier = Modifier.height(16.dp))

        MockTodayTasksSection()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Accommodation", style = MaterialTheme.typography.headlineMedium
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

        MockTravelInfoSection()

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = "Trip Settings Screen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
    ICard(modifier = Modifier.fillMaxWidth(), content = {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ITINERO GROUP CODE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            OtpDisplayField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), otpText = "51712"
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
    }, onClick = { })
}

@Composable
fun MockTripInfoSection() {
    PaddedListGroup(title = "Trip Information".uppercase()) {
        CustomPaddedListItem(
            onClick = { }, position = PaddedListItemPosition.First
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
            onClick = { }, position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Trip Dates", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Dec 15, 2024 - Dec 22, 2024",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { }, position = PaddedListItemPosition.Middle
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
            onClick = { }, position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Accommodation Location", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "1-1-1 Shibuya, Tokyo, Japan",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = { }, position = PaddedListItemPosition.Last
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
            onClick = { }, position = PaddedListItemPosition.First
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
            onClick = { }, position = PaddedListItemPosition.Middle
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
            onClick = { }, position = PaddedListItemPosition.Last
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
            text = "DANGER ZONE", style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error
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
    destination: String, groupName: String
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
    title: String, value: String, subtitle: String, modifier: Modifier = Modifier
) {
    ICard(modifier = modifier, shape = RoundedCornerShape(16.dp), content = {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value, style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }, onClick = { })
}

@Composable
fun MockSummarySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Summary", style = MaterialTheme.typography.headlineMedium
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
            text = "Today's Tasks", style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Expense Summary Card
        ICard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), content = {
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
                        text = "Breakfast", style = MaterialTheme.typography.bodyMedium
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
                        text = "Train tickets", style = MaterialTheme.typography.bodyMedium
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
                        text = "Total Today", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "¥3,300",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }, onClick = { })

        Spacer(modifier = Modifier.height(12.dp))

        // Today's Itinerary Card
        ICard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), content = {
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
                    modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
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
                            text = "Senso-ji Temple", style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Visit Tokyo's oldest temple",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Itinerary Item 2
                Row(
                    modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
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
                            text = "Shibuya Crossing", style = MaterialTheme.typography.bodyMedium
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
                    modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
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
                            text = "Izakaya Dinner", style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Traditional Japanese pub experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }, onClick = { })
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
                    imageVector = Icons.Rounded.Edit, contentDescription = "Edit travel information"
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

@Composable
fun ItineraryExampleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        MockItineraryDateSection(
            date = LocalDate.now(), title = "Today - December 15, 2024"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MockItineraryDateSection(
            date = LocalDate.now().plusDays(1), title = "Tomorrow - December 16, 2024"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MockItineraryDateSection(
            date = LocalDate.now().plusDays(2), title = "Day 3 - December 17, 2024"
        )
    }
}

@Composable
fun MockItineraryDateSection(
    date: LocalDate, title: String
) {
    Row {
        // Date sidebar
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            // Activity cards
            MockItineraryActivity(
                name = "Senso-ji Temple Visit",
                time = "9:00 AM",
                location = "Asakusa, Tokyo",
                description = "Visit Tokyo's oldest temple and explore traditional shops",
                isCompleted = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            MockItineraryActivity(
                name = "Tokyo Skytree",
                time = "2:00 PM",
                location = "Sumida, Tokyo",
                description = "Observation deck with panoramic city views",
                isCompleted = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            MockItineraryActivity(
                name = "Izakaya Dinner",
                time = "7:00 PM",
                location = "Shibuya, Tokyo",
                description = "Traditional Japanese pub experience",
                isCompleted = false
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MockItineraryActivity(
    name: String, time: String, location: String, description: String, isCompleted: Boolean
) {
    ICard(
        swipeable = true,
        isCompleted = isCompleted,
        headerTitle = name,
        headerColor = MaterialTheme.colorScheme.tertiaryContainer,
        headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
        swipeActionsConfig = if (!isCompleted) {
            SwipeActionsConfig(
                threshold = 0.3f,
                icon = Icons.Default.Check,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                background = MaterialTheme.colorScheme.primary,
                stayDismissed = false,
                onDismiss = { })
        } else {
            SwipeActionsConfig(
                threshold = 0.3f,
                icon = Icons.Default.Close,
                iconTint = MaterialTheme.colorScheme.onError,
                background = MaterialTheme.colorScheme.error,
                stayDismissed = false,
                onDismiss = { })
        },
        onSwipe = { },
        content = {
            Column(
                modifier = Modifier.padding(
                    horizontal = 24.dp, vertical = 8.dp
                )
            ) {
                Text(
                    text = "🕒 $time", style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "📍 $location", style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "❓ $description", style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        onClick = { })
}

@Composable
fun ExpensesExampleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BalanceCircles(
            youOwe = 2500.0, youAreOwed = 5200.0
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "History of expenses", style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        MockExpenseDateSection(
            date = LocalDate.now(), title = "Today - December 15, 2024"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MockExpenseDateSection(
            date = LocalDate.now().plusDays(-1), title = "Yesterday - December 14, 2024"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MockExpenseDateSection(
            date = LocalDate.now().plusDays(-2), title = "December 13, 2024"
        )
    }
}

@Composable
fun MockExpenseDateSection(
    date: LocalDate, title: String
) {
    Row {
        // Date sidebar
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp, start = 8.dp)
        ) {
            // Mock expense items
            when (title) {
                "Today - December 15, 2024" -> {
                    ExpenseCard(
                        expenseName = "Lunch at Ramen Shop",
                        membersCount = 4,
                        amountOwed = 3200.0,
                        isCompleted = false,
                        isYours = true,
                        icon = Icons.Default.Restaurant,
                        modifier = Modifier.fillMaxWidth()
                    ) { }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExpenseCard(
                        expenseName = "Train Tickets",
                        membersCount = 4,
                        amountOwed = 800.0,
                        isCompleted = false,
                        isYours = false,
                        icon = Icons.Default.Train,
                        modifier = Modifier.fillMaxWidth()
                    ) { }
                }

                "Yesterday - December 14, 2024" -> {
                    ExpenseCard(
                        expenseName = "Hotel Room",
                        membersCount = 4,
                        amountOwed = 12000.0,
                        isCompleted = false,
                        isYours = false,
                        icon = Icons.Default.Home,
                        modifier = Modifier.fillMaxWidth()
                    ) { }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExpenseCard(
                        expenseName = "Dinner & Drinks",
                        membersCount = 4,
                        amountOwed = 8500.0,
                        isCompleted = false,
                        isYours = true,
                        icon = Icons.Default.Restaurant,
                        modifier = Modifier.fillMaxWidth()
                    ) { }
                }

                "December 13, 2024" -> {
                    ExpenseCard(
                        expenseName = "Flight Tickets",
                        membersCount = 4,
                        amountOwed = 45000.0,
                        isCompleted = false,
                        isYours = false,
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.fillMaxWidth()
                    ) { }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ChatExampleContent() {
    val chatListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val mockMessages = listOf(
        ChatMessage(
            id = "1",
            content = "Hey everyone! Welcome to our Tokyo trip planning group!",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:07 PM"
        ), ChatMessage(
            id = "2",
            content = "Thanks Isaac! Excited to be part of this adventure 🎌",
            authorId = "andrea",
            authorName = "Andrea Mena",
            timestamp = "8:08 PM"
        ), ChatMessage(
            id = "3",
            content = "I've been researching some amazing temples we should visit!",
            authorId = "carlos",
            authorName = "Carlos Rodriguez",
            timestamp = "8:09 PM"
        ), ChatMessage(
            id = "4",
            content = "That sounds great! I've been looking into the best ramen spots 🍜",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:10 PM"
        ), ChatMessage(
            id = "5",
            content = "Perfect! We should also check out Shibuya Crossing at night 🌃",
            authorId = "maria",
            authorName = "Maria Garcia",
            timestamp = "8:11 PM"
        ), ChatMessage(
            id = "6",
            content = "And don't forget about the cherry blossom season! 🌸",
            authorId = "david",
            authorName = "David Thompson",
            timestamp = "8:12 PM"
        ), ChatMessage(
            id = "7",
            content = "Should we book the hotel near Shibuya station?",
            authorId = "andrea",
            authorName = "Andrea Mena",
            timestamp = "8:13 PM"
        ), ChatMessage(
            id = "8",
            content = "That's a great idea! It's close to everything we want to see.",
            authorId = "isaac",
            authorName = "Isaac Serrano",
            timestamp = "8:14 PM"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tokyo Adventure Group",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "5 members",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = chatListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                items(items = mockMessages.reversed(), key = { it.id }) { message ->
                    ChatBubbleWithAvatar(
                        message = message.content,
                        isUserMe = message.authorId == "isaac",
                        timestamp = message.timestamp,
                        authorName = message.authorName,
                        onMessageClick = { },
                        onAvatarClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            JumpToBottom(
                enabled = true, onClicked = {
                    coroutineScope.launch {
                        chatListState.animateScrollToItem(0)
                    }
                }, modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        UserInput(
            onMessageSent = { content ->
            coroutineScope.launch {
                chatListState.animateScrollToItem(0)
            }
        }, resetScroll = {
            coroutineScope.launch {
                chatListState.scrollToItem(0)
            }
        }, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsExampleContent() {
    var themeMode by remember { mutableStateOf("System Default") }
    var isMaterialYou by remember { mutableStateOf(true) }

    val themeOptions = listOf("Light", "Dark", "System Default")
    val selectedThemeIndex = themeOptions.indexOf(themeMode)
    var showThemeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Choose Theme") },
                text = {
                    Column {
                        themeOptions.forEachIndexed { idx, option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .also { if (idx == selectedThemeIndex) it }) {
                                RadioButton(
                                    selected = selectedThemeIndex == idx, onClick = {
                                        themeMode = option
                                        showThemeDialog = false
                                    })
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Cancel")
                    }
                })
        }

        PaddedListGroup(
            title = "Look & Feel"
        ) {
            CustomPaddedListItem(
                onClick = { showThemeDialog = true }, position = PaddedListItemPosition.First
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "App theme", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Change the overall theme of the app.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Current: $themeMode",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Select theme",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.Last
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Material You", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Apply Material You colors based from your wallpaper to your app (Android 12+)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Switch(
                    checked = isMaterialYou, onCheckedChange = {
                        isMaterialYou = it
                    })
            }
        }

        PaddedListGroup(
            title = "App Information"
        ) {
            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.First
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Information", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "See the information of the app and the developer.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.Middle
            ) {
                Icon(
                    imageVector = Icons.Default.DensitySmall,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Website", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Visit our website for more extensive information about the app and the development of it.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.Middle
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Privacy Policy", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Read the privacy policy & terms of use of the app.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.Middle
            ) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Encountered a bug?", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Send us a report or issues you encounter creating a Bug/Issue report on GitHub.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CustomPaddedListItem(
                onClick = { }, position = PaddedListItemPosition.Last
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Version", style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Version 0.5", style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@DevicePreview
@Composable
fun ExamplesScreenPreview() {
    PreviewWrapper {
        ExamplesScreen()
    }
}