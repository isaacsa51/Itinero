package com.serranoie.app.feature.settings.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.designsystem.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.OtpDisplayField
import com.serranoie.app.designsystem.ui.theme.component.card.ICard
import com.serranoie.app.designsystem.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystem.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.itinero.feature.settings.trip.TripSettingsViewModel
import com.serranoie.itinero.core.domain.model.Trip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripSettingsScreen(
    navController: NavController,
    tripId: String = "",
    scrollTo: String?,
    trip: Trip?,
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
                1 // Based on the current structure: 0=group code, 1=trip information, 2=management
            lazyListState.animateScrollToItem(tripInfoItemIndex)
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                Text(
                    "Trip Settings", maxLines = 1, overflow = TextOverflow.Ellipsis
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
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues), state = lazyListState
        ) {
            item {
                ICard(
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
                                            color = Color.White, shape = RoundedCornerShape(8.dp)
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

            // Trip Information Settings
            item(key = "tripInfo") {
                PaddedListGroup(
                    title = "Trip Information".uppercase(),
                ) {
                    CustomPaddedListItem(
                        onClick = {
                            navController.navigate(
                                Route.TripInfo.createRoute(
                                    tripId = tripId
                                )
                            )
                        }, position = PaddedListItemPosition.First
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trip Name", style = MaterialTheme.typography.bodyLarge
                            )
                            trip?.groupName?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    CustomPaddedListItem(
                        onClick = {
                            navController.navigate(
                                Route.TripInfo.createRoute(
                                    tripId = tripId
                                )
                            )
                        }, position = PaddedListItemPosition.Middle
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trip Dates", style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if (trip != null) "${trip.startDate} - ${trip.endDate}" else "Not set",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    CustomPaddedListItem(
                        onClick = {
                            navController.navigate(
                                Route.TripInfo.createRoute(
                                    tripId = tripId
                                )
                            )
                        }, position = PaddedListItemPosition.Middle
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Accomodation Name",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = trip?.accommodation?.name ?: "Not set",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    CustomPaddedListItem(
                        onClick = {
                            navController.navigate(
                                Route.TripInfo.createRoute(
                                    tripId = tripId
                                )
                            )
                        }, position = PaddedListItemPosition.Middle
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Accomodation location",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = trip?.accommodation?.location ?: "Not set",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                    CustomPaddedListItem(
                        onClick = {
                            navController.navigate(
                                Route.TripInfo.createRoute(
                                    tripId = tripId
                                )
                            )
                        }, position = PaddedListItemPosition.Last
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Accommodation number", style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = trip?.accommodation?.phone ?: "Not set",
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
                    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

                    CustomPaddedExpandableItem(
                        isExpanded = expanded,
                        onToggleExpanded = { expanded = !expanded },
                        position = PaddedListItemPosition.Middle,
                        defaultContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
                                )
                            }
                        },
                        expandedContent = {
                            AnimatedVisibility(
                                visible = expanded, enter = expandVertically(
                                    expandFrom = Alignment.Top
                                ) + fadeIn(), exit = shrinkVertically() + fadeOut()
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp)
                                ) {
                                    Text(
                                        text = "Pending Member Name",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Column {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            IButton(
                                                text = {
                                                    Text(
                                                        text = "Delete member",
                                                        style = MaterialTheme.typography.labelLargeEmphasized
                                                    )
                                                },
                                                onClick = { /*TODO*/ },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(36.dp),
                                                importance = ButtonImportance.Error,
                                            )
                                        }
                                    }
                                }
                            }
                        })

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
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error
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

@ThemePreviews
@Composable
private fun TripSettingsScreenPreview() {
    PreviewWrapper {
        val viewModel: TripSettingsViewModel = viewModel()
        TripSettingsScreen(
            navController = rememberNavController(),
            tripId = "12345",
            scrollTo = null,
            trip = Trip(
                id = "12345",
                groupName = "My Group",
                destination = "My Trip",
                startDate = "2025-06-07",
                endDate = "2025-06-10",
                summary = "Preview trip",
                totalMembers = 3,
                accommodation = com.serranoie.itinero.core.domain.model.Accommodation(
                    name = "My Hotel",
                    location = "123 Main St",
                    phone = "+1 1234567890",
                    checkIn = "15:00",
                    checkOut = "11:00",
                    mapUri = null
                ),
                reservationCode = "RES123",
                extraInfo = "",
                additionalInfo = "",
                groupCode = "ITN-12345",
                ownerId = "user123"
            ),
            viewModel = viewModel
        )
    }
}
