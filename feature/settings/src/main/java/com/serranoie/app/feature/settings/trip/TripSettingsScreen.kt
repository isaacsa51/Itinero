package com.serranoie.app.feature.settings.trip

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.OutlinedCard
import com.serranoie.app.itinero.feature.settings.trip.TripSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSettingsScreen(
    navController: NavController,
    tripId: String = "",
    viewModel: TripSettingsViewModel = viewModel()
) {
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val qrBitmap = viewModel.qrBitmap.collectAsStateWithLifecycle().value
    val formattedCode = tripId.replace("ITN-", "")

    LaunchedEffect(tripId) {
        if (tripId.isNotEmpty()) {
            viewModel.setQrText(tripId)
            viewModel.generateQrCode()
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ), title = {
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                OutlinedCard(
                    swipeable = false, isCompleted = false, modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ITINERO GROUP CODE",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
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
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
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

            // Registered Members
            item {
                RegisteredMembers()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Trip Information Settings
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "TRIP INFORMATION",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsItem(
                        title = "Trip Name",
                        subtitle = "Summer Adventure 2023",
                        onClick = { /* Navigate to edit trip name */ },
                        showDivider = true
                    )

                    SettingsItem(
                        title = "Trip Dates",
                        subtitle = "Aug 15 - Aug 25, 2023",
                        onClick = { /* Navigate to edit trip dates */ },
                        showDivider = true
                    )

                    SettingsItem(
                        title = "Trip Description",
                        subtitle = "Our annual summer vacation exploring the coast",
                        onClick = { /* Navigate to edit description */ }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Group Management Settings
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "GROUP MANAGEMENT",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsItem(
                        title = "Invite New Member",
                        subtitle = "Share invitation code with others",
                        onClick = { /* Handle invite action */ },
                        showDivider = true
                    )

                    SettingsItem(
                        title = "Member Permissions",
                        subtitle = "Manage what group members can edit",
                        onClick = { /* Navigate to permissions screen */ },
                        showDivider = true
                    )

                    SettingsItem(
                        title = "Transfer Ownership",
                        subtitle = "Change the trip administrator",
                        onClick = { /* Navigate to transfer ownership */ }
                    )
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
                                style = MaterialTheme.typography.labelLarge
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
                                style = MaterialTheme.typography.labelLarge
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

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (showDivider) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
        }
    }
}

@Composable
private fun RegisteredMembers() {
    // The expandedStatus needs to be tracked with a mutable state
    var expandedStatus by remember { mutableStateOf(false) }

    Column {
        ExpandablePendingInvites(
            isExpanded = expandedStatus,
            onExpandedChange = { expandedStatus = it })
    }
}

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
                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                IButton(
                    text = {
                        Text(
                            text = "Delete member",
                            style = MaterialTheme.typography.labelLarge
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

@ThemePreviews
@Composable
private fun TripSettingsScreenPreview() {
    PreviewWrapper {
        val viewModel: TripSettingsViewModel = viewModel()
        TripSettingsScreen(
            navController = rememberNavController(),
            tripId = "12345",
            viewModel = viewModel
        )
    }
}
