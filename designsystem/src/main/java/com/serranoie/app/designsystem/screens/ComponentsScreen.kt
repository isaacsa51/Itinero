package com.serranoie.app.designsystem.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.designsystemlib.ui.utils.AIShimmer
import com.serranoie.app.designsystemlib.ui.theme.component.AnimatedStrikethroughText
import com.serranoie.app.designsystemlib.ui.theme.component.BalanceCircles
import com.serranoie.app.designsystemlib.ui.theme.component.BottomSheetContent
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.CustomSettingsItem
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInput
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInputType
import com.serranoie.app.designsystemlib.ui.theme.component.FlexibleListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.LargeDropdownMenu
import com.serranoie.app.designsystemlib.ui.theme.component.ListItem
import com.serranoie.app.designsystemlib.ui.theme.component.LocationInput
import com.serranoie.app.designsystemlib.ui.theme.component.MarqueeText
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.RecordButton
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.theme.component.SwipeButton
import com.serranoie.app.designsystemlib.ui.theme.component.UserInput
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubble
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatBubbleWithAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatConversation
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatMessage
import com.serranoie.app.designsystemlib.ui.theme.component.card.CompactChatBubble
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import java.time.LocalDate
import java.util.Date

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ComponentsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Component Library",
            style = MaterialTheme.typography.displaySmallEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Comprehensive showcase of all custom components",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cards Section
        ComponentSection(title = "Card Components") {
            CardComponentsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Balance Circles Section
        ComponentSection(title = "Balance Circles") {
            BalanceCirclesShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Chat Components Section
        ComponentSection(title = "Chat Components") {
            ChatComponentsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Interactive Components Section
        ComponentSection(title = "Interactive Components") {
            InteractiveComponentsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input Components Section
        ComponentSection(title = "Input Components") {
            InputComponentsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lists & Settings Section
        ComponentSection(title = "Lists & Settings") {
            ListsAndSettingsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Animation Components Section
        ComponentSection(title = "Animation Components") {
            AnimationComponentsShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Sheet Section
        ComponentSection(title = "Bottom Sheet Components") {
            BottomSheetShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Modifier Extensions Section
        ComponentSection(title = "Modifier Extensions") {
            ModifierExtensionsShowcase()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ComponentSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun CardComponentsShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Card Variants",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Basic Card
        ICard(
            isCompleted = false,
            swipeable = false,
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Basic Card",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Simple card without header",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            onClick = { }
        )

        // Card with Header
        ICard(
            isCompleted = false,
            swipeable = false,
            headerTitle = "Card with Header",
            headerColor = MaterialTheme.colorScheme.primaryContainer,
            headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "This card has a colorful header",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            onClick = { }
        )

        // Swipeable Card
        ICard(
            isCompleted = false,
            onSwipe = { },
            swipeable = true,
            headerTitle = "Swipeable Card",
            headerColor = MaterialTheme.colorScheme.tertiaryContainer,
            headerTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Swipe to mark as completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            onClick = { }
        )

        // Completed Card
        ICard(
            isCompleted = true,
            swipeable = false,
            headerTitle = "Completed Card",
            headerColor = MaterialTheme.colorScheme.secondaryContainer,
            headerTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "This card is marked as completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            onClick = { }
        )
    }
}

@Composable
fun ChatComponentsShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Chat Bubble Variants",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // User Messages
        Text(
            text = "User Messages",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            ChatBubble(
                message = "Hello! How are you doing today?",
                isUserMe = true,
                timestamp = "10:30 AM"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatBubble(
                message = "This is a longer message to show how the chat bubble handles multiple lines of text content properly.",
                isUserMe = true,
                timestamp = "10:31 AM"
            )
        }

        HorizontalDivider()

        // Other User Messages
        Text(
            text = "Other User Messages",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            ChatBubble(
                message = "Hi there! I'm doing great, thanks for asking!",
                isUserMe = false,
                timestamp = "10:32 AM"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatBubble(
                message = "How has your day been so far?",
                isUserMe = false,
                timestamp = "10:33 AM"
            )
        }

        HorizontalDivider()

        // Chat Bubbles with Avatars
        Text(
            text = "Chat Bubbles with Initials Avatars",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            ChatBubbleWithAvatar(
                message = "Hey there! This message shows my initials avatar.",
                authorName = "Isaac Serrano",
                timestamp = "10:35 AM",
                isUserMe = false,
                showAvatar = true,
                showAuthor = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatBubbleWithAvatar(
                message = "Here's another message from Isaac - same color!",
                authorName = "Isaac Serrano",
                timestamp = "10:36 AM",
                isUserMe = false,
                showAvatar = false,
                showAuthor = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChatBubbleWithAvatar(
                message = "Hi everyone! I'm Andrea with a different color.",
                authorName = "Andrea Mena",
                timestamp = "10:37 AM",
                isUserMe = false,
                showAvatar = true,
                showAuthor = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatBubbleWithAvatar(
                message = "Another message from Andrea - same color as before!",
                authorName = "Andrea Mena",
                timestamp = "10:38 AM",
                isUserMe = false,
                showAvatar = false,
                showAuthor = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChatBubbleWithAvatar(
                message = "My own message (no avatar shown for current user)",
                authorName = "Me",
                timestamp = "10:39 AM",
                isUserMe = true
            )
        }

        HorizontalDivider()

        // Chat Conversation (with initials avatars)
        Text(
            text = "Chat Conversation with Initials",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ChatConversation(
            messages = listOf(
                ChatMessage(
                    id = "1",
                    content = "Hey everyone! Ready for today's presentation?",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:00 AM"
                ),
                ChatMessage(
                    id = "2",
                    content = "Yes, I'll be there in 5 minutes.",
                    authorId = "me",
                    authorName = "Me",
                    timestamp = "09:01 AM"
                ),
                ChatMessage(
                    id = "3",
                    content = "Perfect! I've prepared the slides.",
                    authorId = "andrea",
                    authorName = "Andrea Mena",
                    timestamp = "09:02 AM"
                ),
                ChatMessage(
                    id = "4",
                    content = "Great work, Andrea! Looking forward to it.",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:03 AM"
                ),
                ChatMessage(
                    id = "5",
                    content = "Thanks Isaac! Should we start with the overview?",
                    authorId = "andrea",
                    authorName = "Andrea Mena",
                    timestamp = "09:04 AM"
                ),
                ChatMessage(
                    id = "6",
                    content = "Let me join you both now.",
                    authorId = "carlos",
                    authorName = "Carlos Rodriguez",
                    timestamp = "09:05 AM"
                ),
                ChatMessage(
                    id = "7",
                    content = "Sounds good! I'll share my screen.",
                    authorId = "isaac",
                    authorName = "Isaac Serrano",
                    timestamp = "09:06 AM"
                )
            ),
            currentUserId = "me"
        )

        HorizontalDivider()

        // Compact Chat Bubbles
        Text(
            text = "Compact Chat Bubbles",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CompactChatBubble(
                message = "This is a compact message for previews or lists",
                isUserMe = false,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            CompactChatBubble(
                message = "Compact user message",
                isUserMe = true,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Custom Styled Chat Bubbles
        Text(
            text = "Custom Styled Chat Bubbles",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            ChatBubble(
                message = "This bubble has custom colors!",
                isUserMe = false,
                timestamp = "10:40 AM",
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                textColor = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatBubble(
                message = "This one has a custom shape!",
                isUserMe = true,
                timestamp = "10:41 AM",
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun InteractiveComponentsShowcase() {
    var swipeComplete by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var swipeOffset by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Swipe Button
        Text(
            text = "Swipe Button",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        SwipeButton(
            text = "Swipe to activate",
            isComplete = swipeComplete,
            onSwipe = { swipeComplete = !swipeComplete }
        )

        HorizontalDivider()

        // Date Range Toolbar
        Text(
            text = "Date Range Toolbar",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            DateRangeToolbar(date = LocalDate.now())
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Content for this date",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        HorizontalDivider()

        // Record Button
        Text(
            text = "Record Button",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        RecordButton(
            recording = recording,
            swipeOffset = { swipeOffset },
            onSwipeOffsetChange = { swipeOffset = it },
            onStartRecording = { recording = true; true },
            onFinishRecording = { recording = false },
            onCancelRecording = { recording = false }
        )

        HorizontalDivider()

        // User Input
        Text(
            text = "User Input (Chat Input)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        UserInput(
            onMessageSent = { /* Handle message */ }
        )
    }
}

@Composable
fun InputComponentsShowcase() {
    var selectedDateTime by remember { mutableStateOf<Date?>(Date()) }
    var selectedDropdownIndex by remember { mutableStateOf(-1) }
    var selectedLocation by remember { mutableStateOf("") }

    val dropdownItems = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    val dropdown = remember { LargeDropdownMenu() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // DateTime Input
        Text(
            text = "DateTime Input",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        DateTimeInput(
            selectedDateTime = selectedDateTime,
            onDateTimeSelected = { selectedDateTime = it },
            label = "Date & Time: ",
            inputType = DateTimeInputType.BOTH,
            leadingIcon = Icons.Default.CalendarToday
        )

        // Date Only Input
        DateTimeInput(
            selectedDateTime = selectedDateTime,
            onDateTimeSelected = { selectedDateTime = it },
            label = "Date Only: ",
            inputType = DateTimeInputType.DATE
        )

        // Time Only Input
        DateTimeInput(
            selectedDateTime = selectedDateTime,
            onDateTimeSelected = { selectedDateTime = it },
            label = "Time Only: ",
            inputType = DateTimeInputType.TIME
        )

        HorizontalDivider()

        // Location Field
        Text(
            text = "Location Field",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        LocationInput(
            value = selectedLocation,
            onValueChange = { selectedLocation = it },
            label = "Enter Location"
        )

        LocationInput(
            value = "Times Square, New York, NY 10036",
            onValueChange = { },
            label = "Hotel Location"
        )

        LocationInput(
            value = "",
            onValueChange = { },
            label = "Empty Location"
        )

        HorizontalDivider()

        // Large Dropdown Menu
        Text(
            text = "Large Dropdown Menu",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        dropdown.LargeDropdownMenu(
            label = "Select Option",
            items = dropdownItems,
            selectedIndex = selectedDropdownIndex,
            onItemSelected = { index, _ -> selectedDropdownIndex = index }
        )
    }
}

@Composable
fun ListsAndSettingsShowcase() {
    var isExpanded1 by remember { mutableStateOf(false) }
    var isExpanded2 by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Flexible List Groups
        Text(
            text = "Flexible List Groups",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlexibleListGroup(
            title = "Basic Settings Group"
        ) {
            ListItem(
                title = "Notifications",
                subtitle = "App alerts and sounds",
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                showDivider = true
            )
            ListItem(
                title = "Privacy",
                subtitle = "Data and permissions",
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                showDivider = true
            )
            ListItem(
                title = "Account",
                subtitle = "Manage your account settings",
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
            )
        }

        // Flexible List Group with Custom Content
        FlexibleListGroup(
            title = "Custom Content Group"
        ) {
            CustomSettingsItem(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Custom Item with Value",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "This shows custom layout with trailing value",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Enabled",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            CustomSettingsItem(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date Preferences",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Format and timezone settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "UTC+2",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        HorizontalDivider()

        // Padded List Groups
        Text(
            text = "Padded List Groups",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        PaddedListGroup(
            title = "Account Settings"
        ) {
            PaddedListItem(
                title = "Profile",
                subtitle = "Manage your profile information",
                icon = Icons.Default.Settings,
                onClick = { },
                position = PaddedListItemPosition.First
            )
            PaddedListItem(
                title = "Security",
                subtitle = "Password and authentication",
                icon = Icons.Default.Settings,
                onClick = { },
                position = PaddedListItemPosition.Middle
            )
            PaddedListItem(
                title = "Preferences",
                subtitle = "App behavior and defaults",
                icon = Icons.Default.Settings,
                onClick = { },
                position = PaddedListItemPosition.Last
            )
        }

        // Custom Padded List Group
        PaddedListGroup(
            title = "Advanced Settings"
        ) {
            CustomPaddedListItem(
                onClick = { },
                position = PaddedListItemPosition.First
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Developer Options",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Debug settings and logging",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Beta",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            CustomPaddedExpandableItem(
                isExpanded = isExpanded1,
                onToggleExpanded = { isExpanded1 = !isExpanded1 },
                position = PaddedListItemPosition.Middle,
                defaultContent = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Network Settings",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Configure network preferences",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded1) Icons.Default.Settings else Icons.Default.Settings,
                        contentDescription = if (isExpanded1) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                expandedContent = {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Wi-Fi Settings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Configure automatic connection and saved networks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Mobile Data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Data usage limits and roaming settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            CustomPaddedExpandableItem(
                isExpanded = isExpanded2,
                onToggleExpanded = { isExpanded2 = !isExpanded2 },
                position = PaddedListItemPosition.Last,
                defaultContent = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Options",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Reset app data and settings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded2) Icons.Default.Settings else Icons.Default.Settings,
                        contentDescription = if (isExpanded2) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                expandedContent = {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "⚠️ Warning",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "These actions cannot be undone:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "• Reset app preferences",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "• Clear all user data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "• Reset to factory defaults",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        HorizontalDivider()

        // Single Padded Items
        Text(
            text = "Single Padded Items",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        PaddedListItem(
            title = "Standalone Setting",
            subtitle = "This is a single padded item",
            icon = Icons.Default.Settings,
            onClick = { },
            position = PaddedListItemPosition.Single
        )

        CustomPaddedListItem(
            onClick = { },
            position = PaddedListItemPosition.Single
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Custom Single Item",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Standalone custom padded item",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Active",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        HorizontalDivider()

        // Mixed Content Example
        Text(
            text = "Mixed Content Example",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlexibleListGroup(
            title = "Complex Settings Group"
        ) {
            ListItem(
                title = "Standard Item",
                subtitle = "Regular list item with divider",
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                showDivider = true
            )

            CustomSettingsItem(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Custom Item in Flexible Group",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Mixed",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                title = "Another Standard Item",
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
            )
        }
    }
}

@Composable
fun BalanceCirclesShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            text = "Financial Balance Components",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Equal amounts
        Text(
            text = "Equal Amounts",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 150.0,
            youAreOwed = 150.0
        )

        HorizontalDivider()

        // You owe more
        Text(
            text = "You Owe More",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 250.0,
            youAreOwed = 100.0
        )

        HorizontalDivider()

        // You are owed more
        Text(
            text = "You Are Owed More",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 75.0,
            youAreOwed = 300.0
        )

        HorizontalDivider()

        // Small amounts
        Text(
            text = "Small Amounts",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 12.50,
            youAreOwed = 8.25
        )

        HorizontalDivider()

        // Large amounts
        Text(
            text = "Large Amounts",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 1250.0,
            youAreOwed = 850.0
        )

        HorizontalDivider()

        // Zero amounts
        Text(
            text = "Zero Amounts",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 0.0,
            youAreOwed = 0.0
        )

        HorizontalDivider()

        // One-sided balance
        Text(
            text = "One-Sided Balance",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 0.0,
            youAreOwed = 125.0
        )

        HorizontalDivider()

        // Custom colors
        Text(
            text = "Custom Colors",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BalanceCircles(
            youOwe = 100.0,
            youAreOwed = 150.0,
            oweColor = MaterialTheme.colorScheme.error,
            owedColor = MaterialTheme.colorScheme.primaryContainer,
            oweTextColor = MaterialTheme.colorScheme.onError,
            owedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun AnimationComponentsShowcase() {
    var strikethroughVisible by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Animated Strikethrough Text
        Text(
            text = "Animated Strikethrough Text",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val strikethroughText = "This text can be struck through with animation"
                AnimatedStrikethroughText(
                    text = strikethroughText,
                    isVisible = strikethroughVisible,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { strikethroughVisible = !strikethroughVisible }
                )

                Text(
                    text = "Tap to toggle: ${if (strikethroughVisible) "ON" else "OFF"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { strikethroughVisible = !strikethroughVisible }
                )
            }
        }

        HorizontalDivider()

        // Marquee Text
        Text(
            text = "Marquee Text",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MarqueeText(
                text = "This is a very long text that will scroll horizontally when it doesn't fit in the container and demonstrates the marquee effect",
                gradientEdgeColor = MaterialTheme.colorScheme.surfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun BottomSheetShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Bottom Sheet Content",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomSheetContent(
                scannedCode = "https://example.com/qr-code-showcase",
                onCopy = { /* Handle copy */ },
                onShare = { /* Handle share */ },
                onClose = { /* Handle close */ }
            )
        }
    }
}

@Composable
fun ModifierExtensionsShowcase() {
    var isShimmering by remember { mutableStateOf(true) }
    var isAiShimmering by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Shimmer Effects",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Shimmer: ${if (isShimmering) "ON" else "OFF"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )

        ShimmerProvider(isLoading = isShimmering) {
            ICard(
                isCompleted = false,
                swipeable = false,
                content = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Card with Shimmer Effect",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.shimmerable()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This text shows the shimmer loading effect",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.shimmerable()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap anywhere to toggle shimmer effect",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.shimmerable()
                        )
                    }
                },
                onClick = { isShimmering = !isShimmering }
            )
        }
        
        Text(
            text = "Shimmer AI Example",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "AI Shimmer: ${if (isAiShimmering) "ON" else "OFF"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )

        ShimmerProvider(isLoading = isAiShimmering) {
            ICard(
                isCompleted = false,
                swipeable = false,
                content = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Card with AI Suggestion Shimmer Effect",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.AIShimmer()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This text shows the shimmer loading effect",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.AIShimmer()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap anywhere to toggle shimmer effect",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.AIShimmer()
                        )
                    }
                },
                onClick = { isAiShimmering = !isAiShimmering }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComponentsScreenPreview() {
    ItineroTheme {
        ComponentsScreen()
    }
}