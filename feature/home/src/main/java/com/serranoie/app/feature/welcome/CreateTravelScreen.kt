package com.serranoie.app.feature.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.designsystem.ui.theme.component.SelectField
import com.serranoie.app.feature.TravelUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateTravelScreen(
    uiState: TravelUiState,
    onTravelCreated: (String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))

    var showDatePicker by remember { mutableStateOf(false) }

    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var accommodation by remember { mutableStateOf("") }
    var reservationCode by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(topBar = {
        MediumFlexibleTopAppBar(
            title = { Text("Create New Trip") }, navigationIcon = {
            IconButton(onClick = onNavigateBack, content = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            })
        }, scrollBehavior = scrollBehavior
        )
    }, snackbarHost = { SnackbarHost(snackState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionLabel(text = "Basic Information")
            ITextField(
                value = destination,
                onValueChange = { destination = it },
                label = "Destination",
                leadingIcon = Icons.Default.LocationOn,
                modifier = Modifier.fillMaxWidth()
            )

            SelectField(
                value = if (startDate.isNotBlank() && endDate.isNotBlank()) {
                    "$startDate to $endDate"
                } else {
                    "Select travel dates"
                },
                onSelect = { showDatePicker = true },
                label = "Travel Date",
                leadingIcon = Icons.Rounded.CalendarToday
            )

            ITextField(
                value = summary,
                onValueChange = { summary = it },
                label = "Summary",
                leadingIcon = Icons.Default.Description,
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel(text = "Accommodation Details")
            ITextField(
                value = accommodation,
                onValueChange = { accommodation = it },
                label = "Accommodation",
                leadingIcon = Icons.Default.Hotel,
                modifier = Modifier.fillMaxWidth()
            )

            ITextField(
                value = reservationCode,
                onValueChange = { reservationCode = it },
                label = "Reservation Code",
                leadingIcon = Icons.Default.Numbers,
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel(text = "Additional Information")
            ITextField(
                value = extraInfo,
                onValueChange = { extraInfo = it },
                label = "Extra Info",
                leadingIcon = Icons.Default.Info,
                modifier = Modifier.fillMaxWidth()
            )

            ITextField(
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                label = "Additional Info",
                leadingIcon = Icons.Default.Info,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            IButton(
                onClick = {
                    onTravelCreated(
                        destination,
                        startDate,
                        endDate,
                        summary,
                        accommodation,
                        reservationCode,
                        extraInfo,
                        additionalInfo,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
                text = { Text("Create Trip") })
        }

        if (showDatePicker) {
            DateRangePickerModal(onDateRangeSelected = { (start, end) ->
                if (start != null && end != null) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    startDate = formatter.format(Date(start))
                    endDate = formatter.format(Date(end))
                }
                showDatePicker = false
            }, onDismiss = { showDatePicker = false })
        }

        if (uiState is TravelUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit, onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                onDateRangeSelected(
                    Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                )
                onDismiss()
            }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun CreateTravelScreenPreview() {
    PreviewWrapper {
//        CreateTravelScreen(onTravelCreated = {}, onNavigateBack = {})
    }
}
