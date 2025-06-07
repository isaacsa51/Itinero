package com.serranoie.app.feature.settings.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.OtpDisplayField
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.OutlinedCard
import com.serranoie.app.designsystem.ui.theme.component.FlexibleListGroup
import com.serranoie.app.designsystem.ui.theme.component.ListItem
import com.serranoie.app.designsystem.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystem.ui.theme.component.PaddedListItem
import com.serranoie.app.designsystem.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystem.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.itinero.feature.settings.trip.TripSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripSettingsScreen(
    navController: NavController,
    tripId: String = "",
    scrollTo: String?,
    viewModel: TripSettingsViewModel = viewModel()
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val qrBitmap = viewModel.qrBitmap.collectAsStateWithLifecycle().value
    val formattedCode = tripId.replace("ITN-", "")
    val lazyListState = rememberLazyListState()

    LaunchedEffect(tripId) {
        if (tripId.isNotEmpty()) {
            viewModel.setQrText(tripId)
            viewModel.generateQrCode()
        }
    }

    LaunchedEffect(scrollTo) {
        if (scrollTo == "tripInfo") {
            // Find the index of the tripInfo item
            val tripInfoItemIndex =
                3 // Based on the current structure: 0=group code, 1=members, 2=spacer, 3=trip info
            lazyListState.animateScrollToItem(tripInfoItemIndex)
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Trip Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues),
            state = lazyListState
        ) {
            item {
                OutlinedCard(
                    swipeable = false, isCompleted = false, modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ITINERO GROUP CODE",
                            style = MaterialTheme.typography.labelLargeEmphasized
                        )

                        OtpDisplayField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            otpText = formattedCode
                        )

                        qrBitmap?.let { bitmap ->
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Group QR Code",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "What's this code/QR for?",
                            style = MaterialTheme.typography.bodyLargeEmphasized,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "This code is your trip's unique & invitation code identifier. Share it only with people you want to invite to your Itinero planning group.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "They can only use this code within the app to join your group.\n",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Only the trip creator can manage group members.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // TODO: each item naviagte to trip info settings screen
            // Trip Information Settings
            item(key = "tripInfo") {
                PaddedListGroup(
                    title = "Trip Information".uppercase(),
                ) {
                    CustomPaddedListItem(
                        onClick = { },
                        position = PaddedListItemPosition.First
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trip Name",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Trip name holder",
                                style = MaterialTheme.typography.bodyMedium
                            )
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
                            Text(
                                text = "Trip Dates",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "7 of June - 10 of June, 2025",
                                style = MaterialTheme.typography.bodyMedium
                            )
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
                            Text(
                                text = "Accomodation Name",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Item value holder",
                                style = MaterialTheme.typography.bodyMedium
                            )
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
                            Text(
                                text = "Accomodation location",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Item value holder",
                                style = MaterialTheme.typography.bodyMedium
                            )
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
                            Text(
                                text = "Trip Name",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Trip name holder",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            /* TODO: Show only this list if the current user is the owner
            *       - Change the way that the members shows on the members list to display depending if its pending or already accepted
            *       - Do the same with members on Ownership change
            */
            // Group Management Settings
            item {
                PaddedListGroup(
                    title = "GROUP MANAGEMENT"
                ) {
                    CustomPaddedListItem(
                        onClick = { /* Handle invite action */ },
                        position = PaddedListItemPosition.First
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Invite New Member",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Share invitation code with others",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Navigate",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Manage Members with Expandable Content
                    var expanded by remember { mutableStateOf(false) }

                    CustomPaddedExpandableItem(
                        isExpanded = expanded,
                        onToggleExpanded = { expanded = !expanded },
                        position = PaddedListItemPosition.Middle,
                        defaultContent = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Manage Members",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Manage Members",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Review pending invitations",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        expandedContent = {
                            Text(
                                text = "Pending Member Name",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IButton(
                                    text = { Text("Accept") },
                                    onClick = { /* Handle accept action */ },
                                    modifier = Modifier.weight(1f, false),
                                    importance = ButtonImportance.Primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IButton(
                                    text = { Text("Ignore") },
                                    onClick = { /* Handle ignore action */ },
                                    modifier = Modifier.weight(1f, false),
                                    importance = ButtonImportance.Secondary
                                )
                            }
                        }
                    )

                    CustomPaddedListItem(
                        onClick = { /* Navigate to transfer ownership */ },
                        position = PaddedListItemPosition.Last
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Transfer Ownership",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Change the trip administrator",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Navigate",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Danger Zone
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "DANGER ZONE",
                        style = MaterialTheme.typography.labelLargeEmphasized.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    IButton(
                        text = {
                            Text(
                                text = "Leave Trip Group",
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        },
                        onClick = { /* Show leave group confirmation */ },
                        modifier = Modifier.fillMaxWidth(),
                        importance = ButtonImportance.Secondary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    IButton(
                        text = {
                            Text(
                                text = "Delete Trip",
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        },
                        onClick = { /* Show delete trip confirmation */ },
                        modifier = Modifier.fillMaxWidth(),
                        importance = ButtonImportance.Error,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandablePendingInvites(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = { onExpandedChange(!isExpanded) }
            )
            .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Rounded.AddCircleOutline,
                contentDescription = "Add Circle",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Text(
                text = "Name LastName",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    IButton(
                        text = {
                            Text(
                                text = "Delete member",
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                        },
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth(),
                        importance = ButtonImportance.Error,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun TripSettingsScreenPreview() {
    PreviewWrapper {
        val viewModel: TripSettingsViewModel = viewModel()
        TripSettingsScreen(
            navController = rememberNavController(),
            tripId = "12345",
            scrollTo = null,
            viewModel = viewModel
        )
    }
}
