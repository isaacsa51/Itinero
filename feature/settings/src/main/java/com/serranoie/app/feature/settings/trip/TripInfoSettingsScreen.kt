package com.serranoie.app.feature.settings.trip

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInput
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInputType
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.LocationInput
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.model.UpdateTripAccommodation

@Composable
fun TripInfoSettingsScreen(
    navController: NavController,
    tripId: String,
    trip: Trip?,
    onUpdateTripInfo: (String, UpdateTrip) -> Unit,
) {
    var groupName by remember { mutableStateOf(trip?.groupName ?: "") }
    var summary by remember { mutableStateOf(trip?.summary ?: "") }
    var tripStart by remember { mutableStateOf(Utils.parseDate(trip?.startDate ?: "")) }
    var tripEnd by remember { mutableStateOf(Utils.parseDate(trip?.endDate ?: "")) }
    var name by remember { mutableStateOf(trip?.accommodation?.name ?: "") }
    var checkIn by remember { mutableStateOf(Utils.parseDate(trip?.accommodation?.checkIn ?: "")) }
    var checkOut by remember {
        mutableStateOf(
            Utils.parseDate(
                trip?.accommodation?.checkOut ?: ""
            )
        )
    }
    var phoneNumber by remember { mutableStateOf(trip?.accommodation?.phone ?: "") }
    var reservationCode by remember {
        mutableStateOf(trip?.accommodation?.reservationCode ?: "")
    }
    var accommodationExtras by remember {
        mutableStateOf(trip?.accommodation?.extraInfo ?: "")
    }
    var location by remember {
        mutableStateOf(trip?.accommodation?.let { accommodation ->
            when {
                accommodation.location.isNotBlank() -> accommodation.location
                accommodation.name.isNotBlank() -> accommodation.name
                else -> ""
            }
        } ?: "")
    }
    var destination by remember { mutableStateOf(trip?.destination ?: "") }
    var isConfirmDestinationDialogOpen by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "GROUP INFORMATION")

            ITextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = "Group name",
                placeholder = "Enter group name or identifier",
                leadingIcon = Icons.Default.Numbers
            )

            ITextField(
                value = destination,
                onValueChange = { destination = it },
                label = "Destination",
                placeholder = "Enter destination",
                leadingIcon = Icons.Default.LocationOn
            )

            ITextField(
                value = summary,
                onValueChange = { summary = it },
                label = "Summary",
                placeholder = "Enter trip summary",
                leadingIcon = Icons.Default.Description
            )


            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                DateTimeInput(
                    selectedDateTime = tripStart,
                    onDateTimeSelected = { tripStart = it },
                    label = "Trip start date: ",
                    placeholder = trip?.startDate?.takeIf { it.isNotBlank() }
                        ?: "Select trip start date",
                    inputType = DateTimeInputType.DATE,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = extraSmallPadding))

                DateTimeInput(
                    selectedDateTime = tripEnd,
                    onDateTimeSelected = { tripEnd = it },
                    label = "Trip end date: ",
                    placeholder = trip?.endDate?.takeIf { it.isNotBlank() }
                        ?: "Select trip end date",
                    inputType = DateTimeInputType.DATE,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = extraSmallPadding))
            }

            SectionHeader(title = "ACCOMMODATION DETAILS")

            ITextField(
                value = name,
                onValueChange = { name = it },
                label = "Accommodation name",
                placeholder = "Enter accommodation name",
                leadingIcon = Icons.Default.Home
            )

            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                DateTimeInput(
                    selectedDateTime = checkIn,
                    onDateTimeSelected = { checkIn = it },
                    label = "Check-in: ",
                    placeholder = trip?.accommodation?.checkIn?.takeIf { it.isNotBlank() }
                        ?: "Select check-in date and time",
                    inputType = DateTimeInputType.BOTH,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = extraSmallPadding))

                DateTimeInput(
                    selectedDateTime = checkOut,
                    onDateTimeSelected = { checkOut = it },
                    label = "Check-out: ",
                    placeholder = trip?.accommodation?.checkOut?.takeIf { it.isNotBlank() }
                        ?: "Select check-out date and time",
                    inputType = DateTimeInputType.BOTH,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = extraSmallPadding))
            }

            ITextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Accommodation phone number",
                placeholder = "Enter phone number",
                leadingIcon = Icons.Default.Phone
            )

            ITextField(
                value = reservationCode,
                onValueChange = { reservationCode = it },
                label = "Reservation code/number",
                placeholder = "Enter reservation code or number",
                leadingIcon = Icons.Default.Numbers
            )

            LocationInput(
                value = location,
                label = "Accommodation location",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { location = it })

            ITextField(
                value = accommodationExtras,
                onValueChange = { accommodationExtras = it },
                label = "Accommodation extras",
                placeholder = "Enter accommodation extras (included services, special requests, etc.)",
                modifier = Modifier.heightIn(min = 100.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IOutlineButton(onClick = {
                    navController.popBackStack()
                }, modifier = Modifier.weight(1f), text = { Text("Cancel") })

                Spacer(modifier = Modifier.width(16.dp))

                IButton(
                    onClick = {
                        val accommodation = UpdateTripAccommodation(
                            name = name,
                            location = location,
                            phone = phoneNumber,
                            checkIn = Utils.formatDateTimeForStorage(checkIn),
                            checkOut = Utils.formatDateTimeForStorage(checkOut),
                            mapUri = trip?.accommodation?.mapUri,
                            latitude = trip?.accommodation?.latitude,
                            longitude = trip?.accommodation?.longitude,
                            reservationCode = reservationCode,
                            extraInfo = accommodationExtras
                        )

                        val updateTrip = UpdateTrip(
                            groupName = groupName,
                            destination = destination,
                            startDate = Utils.formatDateForStorage(tripStart),
                            endDate = Utils.formatDateForStorage(tripEnd),
                            summary = summary,
                            accommodation = accommodation,
                            reservationCode = reservationCode,
                            extraInfo = accommodationExtras,
                            additionalInfo = ""
                        )

                        Log.d("ITINERO", "Trip info: $updateTrip")
                        Log.d("ITINERO", "Button clicked")

                        onUpdateTripInfo(tripId, updateTrip)
                    },
                    modifier = Modifier.weight(1f),
                    text = { Text("Save") },
                    importance = ButtonImportance.Primary
                )
            }
        }
    }

    if (isConfirmDestinationDialogOpen) {
        AlertDialog(
            onDismissRequest = { isConfirmDestinationDialogOpen = false },
            title = { Text("Confirm Destination") },
            text = { Text("Are you sure you want to change the destination to $destination?") },
            confirmButton = {
                TextButton(onClick = {
                    isConfirmDestinationDialogOpen =
                        false/* TODO: Save all data including new destination */
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isConfirmDestinationDialogOpen = false
                    destination = trip?.destination ?: ""
                }) {
                    Text("Cancel")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLargeEmphasized,
        color = MaterialTheme.colorScheme.outline
    )
}

@DevicePreview
@Composable
private fun TripInfoSettingsScreenPreview() {
    val routeTripId = "ITN-12349"
    PreviewWrapper {
        TripInfoSettingsScreen(
            navController = rememberNavController(), tripId = routeTripId, trip = Trip(
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
                    mapUri = null,
                    latitude = 40.7128,
                    longitude = -74.0060,
                    reservationCode = "PREVIEW-RES123",
                    extraInfo = "Preview extras info"
                ),
                reservationCode = "RES123",
                extraInfo = "",
                additionalInfo = "",
                groupCode = "ITN-12345",
                ownerId = "user123"
            ), onUpdateTripInfo = { _, _ -> })
    }
}
