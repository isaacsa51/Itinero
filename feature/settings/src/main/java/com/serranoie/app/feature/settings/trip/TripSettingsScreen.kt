package com.serranoie.app.feature.settings.trip

import android.graphics.Bitmap
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.OtpDisplayField
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.MemberStatus
import com.serranoie.itinero.core.domain.model.MembershipStatus
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripMember
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSettingsScreen(
    navController: NavController,
    tripId: String = "",
    scrollTo: String?,
    trip: Trip?,
    qrBitmap: Bitmap?,
    membersUiState: TripMembersUiState,
    currentUserMembershipStatus: MembershipStatus?,
    onGenerateQrCode: (String) -> Unit,
    onFetchMembers: (String) -> Unit,
    onAcceptMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit,
    onRejectMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit,
    onRemoveMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val formattedCode = tripId.replace("ITN-", "")
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        if (tripId.isNotEmpty()) {
            onGenerateQrCode(tripId)
        }
    }

    LaunchedEffect(scrollTo) {
        if (scrollTo == "tripInfo") {
            val tripInfoItemIndex = 1
            lazyListState.animateScrollToItem(tripInfoItemIndex)
        }
    }

    Scaffold(topBar = {
        MediumTopAppBar(
            title = {
            Text("Trip Settings", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        }, scrollBehavior = scrollBehavior
        )
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues), state = lazyListState
        ) {
            item {
                GroupCodeCard(formattedCode = formattedCode, qrBitmap = qrBitmap)
            }

            item(key = "tripInfo") {
                TripInfoSection(navController = navController, tripId = tripId, trip = trip)
            }

            item {
                Spacer(modifier = Modifier.height(mediumPadding))
            }

            if (currentUserMembershipStatus?.isOwner == true) {
                item {
                    GroupManagementSection(
                        tripId = tripId,
                        membersUiState = membersUiState,
                        onFetchMembers = onFetchMembers,
                        onAcceptMember = onAcceptMember,
                        onRejectMember = onRejectMember,
                        onRemoveMember = onRemoveMember,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(mediumPadding))
                }
            }

            item {
                DangerZoneSection(currentUserMembershipStatus = currentUserMembershipStatus)
            }

            item {
                Spacer(modifier = Modifier.height(mediumPadding))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GroupCodeCard(formattedCode: String, qrBitmap: Bitmap?) {
    ICard(modifier = Modifier.standardPadding(), isCompleted = false, swipeable = false, content = {
        Column(modifier = Modifier.padding(basePadding + extraSmallPadding)) {
            Text(
                modifier = Modifier.padding(top = basePadding),
                text = "ITINERO GROUP CODE",
                style = MaterialTheme.typography.labelLargeEmphasized
            )

            OtpDisplayField(
                modifier = Modifier
                    .fillMaxWidth()
                    .standardPadding(horizontal = 0.dp, vertical = basePadding),
                otpText = formattedCode
            )

            qrBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(basePadding))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(smallPadding)
                        .shimmerable(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Group QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                color = Color.White, shape = RoundedCornerShape(commonCornerRadius)
                            )
                            .padding(smallPadding)
                    )
                }
            }

            Spacer(modifier = Modifier.height(smallPadding))

            Text(
                text = "What's this code/QR for?",
                style = MaterialTheme.typography.bodyLargeEmphasized,
                modifier = Modifier.padding(bottom = smallPadding)
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
    })
}

@Composable
private fun TripInfoSection(navController: NavController, tripId: String, trip: Trip?) {
    PaddedListGroup(title = "Trip Information".uppercase()) {
        CustomPaddedListItem(
            onClick = {
                navController.navigate(Route.TripInfo.createRoute(tripId = tripId))
            }, position = PaddedListItemPosition.First
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Trip Name",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                trip?.groupName?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }

        CustomPaddedListItem(
            onClick = {
                navController.navigate(Route.TripInfo.createRoute(tripId = tripId))
            }, position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Trip Dates",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
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
                navController.navigate(Route.TripInfo.createRoute(tripId = tripId))
            }, position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Accommodation Name",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
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
                navController.navigate(Route.TripInfo.createRoute(tripId = tripId))
            }, position = PaddedListItemPosition.Middle
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Accommodation Location",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
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
                navController.navigate(Route.TripInfo.createRoute(tripId = tripId))
            }, position = PaddedListItemPosition.Last
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Accommodation Number",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
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

@Composable
private fun GroupManagementSection(
    tripId: String,
    membersUiState: TripMembersUiState,
    onFetchMembers: (String) -> Unit,
    onAcceptMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit,
    onRejectMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit,
    onRemoveMember: (String, Int, () -> Unit, (String) -> Unit) -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 360f else 0f)

    // Fetch members when expanded
    LaunchedEffect(expanded) {
        if (expanded && membersUiState is TripMembersUiState.Idle) {
            onFetchMembers(tripId)
        }
    }

    PaddedListGroup(title = "GROUP MANAGEMENT") {
        CustomPaddedListItem(
            onClick = { /* Handle invite action */ }, position = PaddedListItemPosition.First
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Invite New Member", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
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

        // Manage Members with Expandable Content
        CustomPaddedExpandableItem(
            isExpanded = expanded,
            onToggleExpanded = { expanded = !expanded },
            position = PaddedListItemPosition.Middle,
            defaultContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardPadding(horizontal = basePadding, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Manage Members", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                        Text(
                            text = "Review pending invitations and members",
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
                    visible = expanded,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    MembersListContent(
                        membersUiState = membersUiState,
                        onAcceptMember = { member, onAccept, onError ->
                            onAcceptMember(tripId, member.id, onAccept, onError)
                        },
                        onRejectMember = { member, onReject, onError ->
                            onRejectMember(tripId, member.id, onReject, onError)
                        },
                        onRemoveMember = { member, onRemove, onError ->
                            onRemoveMember(tripId, member.id, onRemove, onError)
                        })
                }
            })

        CustomPaddedListItem(
            onClick = { /* Navigate to transfer ownership */ },
            position = PaddedListItemPosition.Last
        ) {
            Spacer(modifier = Modifier.width(basePadding))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Transfer Ownership", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
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
private fun MembersListContent(
    membersUiState: TripMembersUiState,
    onAcceptMember: (TripMember, () -> Unit, (String) -> Unit) -> Unit,
    onRejectMember: (TripMember, () -> Unit, (String) -> Unit) -> Unit,
    onRemoveMember: (TripMember, () -> Unit, (String) -> Unit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = smallPadding * 1.5f, start = smallPadding * 1.5f, end = smallPadding * 1.5f
            )
    ) {
        when (membersUiState) {
            is TripMembersUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .standardPadding()
                ) {
                    repeat(3) { index ->
                        ShimmerProvider {
                            MemberItemCard(
                                member = TripMember(
                                id = index,
                                name = "Loading Member",
                                surname = "Loading Surname",
                                email = "loading@example.com",
                                status = MemberStatus.PENDING
                            ), onAccept = {}, onReject = {}, onRemove = {})
                        }
                    }
                }
            }

            is TripMembersUiState.Success -> {
                if (membersUiState.members.isEmpty()) {
                    Text(
                        text = "No members found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.standardPadding()
                    )
                } else {
                    membersUiState.members.forEachIndexed { index, member ->
                        MemberItemCard(
                            member = member,
                            onAccept = { onAcceptMember(member, {}, {}) },
                            onReject = { onRejectMember(member, {}, {}) },
                            onRemove = { onRemoveMember(member, {}, {}) },
                        )

                        if (membersUiState.members.size > 1 && index < membersUiState.members.size - 1) {
                           HorizontalDivider(
                                modifier = Modifier.padding(vertical = smallPadding),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        } else if (index < membersUiState.members.size - 1) {
                            Spacer(modifier = Modifier.height(smallPadding))
                        }
                    }
                }
            }

            is TripMembersUiState.Error -> {
                Text(
                    text = "Error: ${membersUiState.message}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.standardPadding()
                )
            }

            is TripMembersUiState.Idle -> {
                Text(
                    text = "Loading members...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.standardPadding()
                )
            }
        }
    }
}

@Composable
private fun MemberItemCard(
    member: TripMember, onAccept: () -> Unit, onReject: () -> Unit, onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = smallPadding * 1.5f)
    ) {
        Text(
            text = member.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(bottom = extraSmallPadding)
                .shimmerable()
        )

        Text(
            text = member.email,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(bottom = smallPadding)
                .shimmerable()
        )

        when (member.status) {
            MemberStatus.PENDING -> {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IButton(
                        text = { Text("Reject", style = MaterialTheme.typography.labelLarge) },
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        importance = ButtonImportance.Error
                    )

                    Spacer(modifier = Modifier.width(smallPadding))

                    IButton(
                        text = { Text("Accept", style = MaterialTheme.typography.labelLarge) },
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        importance = ButtonImportance.Primary
                    )
                }
            }

            MemberStatus.ACCEPTED -> {
                IButton(
                    text = { Text("Remove Member", style = MaterialTheme.typography.labelLarge) },
                    onClick = onRemove,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    importance = ButtonImportance.Error
                )
            }

            MemberStatus.OWNER -> {}
        }
    }
}

@ComponentPreview
@Composable
private fun MemberItemCardPreview() {
    PreviewWrapper {
        Column(
            modifier = Modifier.standardPadding(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                basePadding
            )
        ) {
            Text(
                text = "Pending Member",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = smallPadding)
            )

            MemberItemCard(
                member = TripMember(
                    id = 1,
                    name = "John Doe",
                    surname = "Doe",
                    email = "john.doe@example.com",
                    status = MemberStatus.PENDING
                ),
                onAccept = {},
                onReject = {},
                onRemove = {}
            )

            Text(
                text = "Accepted Member",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = smallPadding)
            )

            MemberItemCard(
                member = TripMember(
                    id = 2,
                    name = "Jane Smith",
                    surname = "Smith",
                    email = "jane.smith@example.com",
                    status = MemberStatus.ACCEPTED
                ),
                onAccept = {},
                onReject = {},
                onRemove = {}
            )

            Text(
                text = "Owner Member",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = smallPadding)
            )

            MemberItemCard(
                member = TripMember(
                    id = 3,
                    name = "Alice Johnson",
                    surname = "Johnson",
                    email = "alice.johnson@example.com",
                    status = MemberStatus.OWNER
                ),
                onAccept = {},
                onReject = {},
                onRemove = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DangerZoneSection(currentUserMembershipStatus: MembershipStatus?) {
    Column(modifier = Modifier.standardPadding()) {
        Text(
            text = "DANGER ZONE", style = MaterialTheme.typography.labelLargeEmphasized.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error
            )
        )

        Spacer(modifier = Modifier.height(basePadding))

        IButton(
            text = {
                Text(
                    text = "Leave Trip Group", style = MaterialTheme.typography.labelLargeEmphasized
                )
            },
            onClick = { /* Show leave group confirmation */ },
            modifier = Modifier.fillMaxWidth(),
            importance = ButtonImportance.Secondary,
        )

        Spacer(modifier = Modifier.height(smallPadding * 1.5f))

        if (currentUserMembershipStatus?.isOwner == true) {
            IButton(
                text = {
                    Text(
                        text = "Delete Trip", style = MaterialTheme.typography.labelLargeEmphasized
                    )
                },
                onClick = { /* Show delete trip confirmation */ },
                modifier = Modifier.fillMaxWidth(),
                importance = ButtonImportance.Error,
            )
        }
    }
}

@DevicePreview
@Composable
private fun TripSettingsScreenPreview() {
    PreviewWrapper {
        TripSettingsScreen(
            navController = rememberNavController(),
            tripId = "ITN-12345",
            scrollTo = null,
            trip = Trip(
                id = "12345",
                groupName = "My Group",
                destination = "My Trip",
                startDate = "2025-06-07",
                endDate = "2025-06-10",
                summary = "Preview trip",
                totalMembers = 3,
                accommodation = Accommodation(
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
            qrBitmap = null,
            membersUiState = TripMembersUiState.Idle,
            currentUserMembershipStatus = MembershipStatus(
                status = "OWNER", isOwner = true, isMember = true, isPending = false
            ),
            onGenerateQrCode = {},
            onFetchMembers = {},
            onAcceptMember = { _, _, _, _ -> },
            onRejectMember = { _, _, _, _ -> },
            onRemoveMember = { _, _, _, _ -> })
    }
}
