package com.serranoie.app.itinero.feature.settings.trip

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystem.ui.theme.component.LocationField

@Composable
fun TripInfoSettingsScreen(navController: NavController) {

    var name by remember { mutableStateOf("Hotel Hilton") }
    var checkIn by remember { mutableStateOf("10/02/2025 at 15:00 Hours") }
    var checkOut by remember { mutableStateOf("20/02/2025 at 12:00 Hours") }
    var phoneNumber by remember { mutableStateOf("+1 (555) 123-4567") }
    var reservationCode by remember { mutableStateOf("HH123456789") }
    var accommodationExtras by remember { mutableStateOf("Room with sea view, includes breakfast") }
    var miscInfo by remember { mutableStateOf("Near city center, 20min walk to the beach") }
    var location by remember { mutableStateOf("Hotel Hilton, 342 5th Avenue, New York, NY 10001") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(title = "ACCOMMODATION DETAILS")

            ITextField(
                value = name,
                onValueChange = { name = it },
                label = "Accommodation name",
                placeholder = "Enter accommodation name",
                leadingIcon = Icons.Default.Home
            )

            ITextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                label = "Check in date",
                placeholder = "Enter check-in date and time",
                leadingIcon = Icons.Default.CalendarToday
            )

            ITextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                label = "Check out date",
                placeholder = "Enter check-out date and time",
                leadingIcon = Icons.Default.CalendarToday
            )

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

            LocationField(
                value = location,
                label = "Accommodation location",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { location = it }
            )

            SectionHeader(title = "ADDITIONAL INFORMATION")

            ITextField(
                value = accommodationExtras,
                onValueChange = { accommodationExtras = it },
                label = "Accommodation extras",
                placeholder = "Enter accommodation extras (included services, special requests, etc.)",
                modifier = Modifier.heightIn(min = 100.dp)
            )

            ITextField(
                value = miscInfo,
                onValueChange = { miscInfo = it },
                label = "Miscellaneous information",
                placeholder = "Enter any other relevant information",
                leadingIcon = Icons.Default.LocationOn,
                modifier = Modifier.heightIn(min = 80.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IOutlineButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    text = { Text("Cancel") })

                Spacer(modifier = Modifier.width(16.dp))

                IButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    text = { Text("Save") },
                    importance = ButtonImportance.Primary
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@ThemePreviews
@Composable
private fun TripInfoSettingsScreenPreview() {
    PreviewWrapper {
        TripInfoSettingsScreen(navController = rememberNavController())
    }
}