/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: DateTimeInput.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero. 
 - Last edited: 27 junio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.dateToString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A reusable date and time input component that displays a clickable card
 * showing the current date/time and opens date/time pickers when clicked.
 *
 * @param selectedDateTime The currently selected date and time, or null to use current date/time
 * @param onDateTimeSelected Callback invoked when a new date/time is selected
 * @param modifier Modifier to be applied to the component
 * @param label Optional label prefix for the display text (defaults to "Date: ")
 * @param placeholder Text to show when no date is selected (defaults to current date/time)
 * @param enabled Whether the component is enabled for interaction
 * @param shape The shape of the card container
 * @param color The background color of the card
 * @param contentPadding Padding applied inside the card content
 * @param verticalPadding Vertical padding around the entire component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeInput(
    selectedDateTime: Date?,
    onDateTimeSelected: (Date) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date: ",
    placeholder: String? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = Color.Transparent,
    contentPadding: Dp = basePadding,
) {
    val currentDateTime = selectedDateTime ?: Date()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDateTime.time,
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    val displayText = remember(selectedDateTime) {
        placeholder ?: dateToString(currentDateTime)
    }

    ICard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier.clickable { showDatePicker = true }
                } else {
                    Modifier
                }), shape = shape, color = color, content = {
        Text(
            text = "$label$displayText",
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            },
            modifier = Modifier.padding(contentPadding),
        )
    }, onClick = if (enabled) {
        { showDatePicker = true }
    } else {
        { }
    })

    if (showDatePicker && enabled) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis

                        if (selectedDateMillis != null) {
                            val selectedCalendar = Calendar.getInstance().apply {
                                timeInMillis = selectedDateMillis
                                timeZone = TimeZone.getDefault()
                            }

                            // Handle timezone adjustments if needed
                            val currentCalendar = Calendar.getInstance().apply {
                                timeZone = TimeZone.getDefault()
                            }

                            if (selectedCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR) || selectedCalendar.get(
                                    Calendar.DAY_OF_YEAR
                                ) != currentCalendar.get(
                                    Calendar.DAY_OF_YEAR
                                )
                            ) {
                                selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
                            }

                            onDateTimeSelected(selectedCalendar.time)
                            showDatePicker = false
                            showTimePicker = true
                        }
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker && enabled) {
        ITimePicker(
            onDismissed = { showTimePicker = false },
            onConfirmButtonClicked = {
                TextButton(
                    onClick = {
                        val baseDate = selectedDateTime ?: Date()
                        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                            time = baseDate
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        onDateTimeSelected(calendar.time)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            onDismissButtonClicked = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            androidx.compose.material3.TimePicker(state = timePickerState)
        }
    }
}

fun formatMyDate(date: Date): String {
    val targetFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a",
        Locale.getDefault())
    return targetFormat.format(date)
}

@ComponentPreview
@Composable
private fun DateTimeInputPreview() {
    var selectedDateTime by remember { mutableStateOf<Date?>(Date()) }
    
    PreviewWrapper {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default DateTimeInput with current date
            DateTimeInput(
                selectedDateTime = selectedDateTime,
                onDateTimeSelected = { selectedDateTime = it }
            )
            
            // DateTimeInput with custom label
            DateTimeInput(
                selectedDateTime = selectedDateTime,
                onDateTimeSelected = { selectedDateTime = it },
                label = "Event Date: "
            )
            
            // Disabled DateTimeInput
            DateTimeInput(
                selectedDateTime = selectedDateTime,
                onDateTimeSelected = { selectedDateTime = it },
                label = "Disabled: ",
                enabled = false
            )
            
            // DateTimeInput with null selected date
            DateTimeInput(
                selectedDateTime = null,
                onDateTimeSelected = { selectedDateTime = it },
                label = "Select Date: "
            )
            
            // DateTimeInput with custom placeholder
            DateTimeInput(
                selectedDateTime = null,
                onDateTimeSelected = { selectedDateTime = it },
                label = "Custom: ",
                placeholder = "No date selected"
            )
        }
    }
}

