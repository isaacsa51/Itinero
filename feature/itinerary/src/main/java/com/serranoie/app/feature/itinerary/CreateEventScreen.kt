package com.serranoie.app.feature.itinerary

import android.graphics.BlurMaskFilter
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.TimePickerDialog
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.dateToString
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

data class TaskInfo(
    val name: String, val time: String, val location: String, val summary: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    existingItem: ItineraryItem? = null,
    onCreateActivity: (name: String, time: String, location: String, summary: String) -> Unit = { _, _, _, _ -> },
    onUpdateActivity: (id: String, name: String, time: String, location: String, summary: String) -> Unit = { _, _, _, _, _ -> },
    onSaveComplete: () -> Unit = {}
) {
    var eventName by remember { mutableStateOf(existingItem?.title ?: "") }
    var eventTime by remember { mutableStateOf(existingItem?.time ?: "") }
    var eventLocation by remember { mutableStateOf(existingItem?.location ?: "") }
    var eventSummary by remember { mutableStateOf(existingItem?.description ?: "") }
    var showCard by remember { mutableStateOf(true) }
    var selectedDateTime by remember { mutableStateOf<Date?>(null) }
    val view = LocalView.current

    var reminderAtEventTime by remember { mutableStateOf(false) }
    var customReminder by remember { mutableStateOf(false) }
    var customReminderHours by remember { mutableStateOf(0) }
    var customReminderMinutes by remember { mutableStateOf(15) }
    var showHoursDropdown by remember { mutableStateOf(false) }
    var showMinutesDropdown by remember { mutableStateOf(false) }

    val isEditMode =
        eventName.isNotEmpty() || eventTime.isNotEmpty() || eventLocation.isNotEmpty() || eventSummary.isNotEmpty()

    val sampleTask = TaskInfo(
        name = "Visit the Eiffel Tower",
        time = "12 Jun 2025, 10:00 AM",
        location = "Eiffel Tower, Paris, France",
        summary = "Daily itinerary planning and review"
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(
                isEditMode = isEditMode,
                existingItem = existingItem,
                sampleTask = sampleTask,
                showCard = showCard,
                onCardClick = {
                    eventName = sampleTask.name
                    eventTime = sampleTask.time
                    eventLocation = sampleTask.location
                    eventSummary = sampleTask.summary
                    showCard = false
                })

            ActivityDetailsSection(
                eventName = eventName,
                onEventNameChange = { eventName = it },
                selectedDateTime = selectedDateTime,
                onDateTimeChange = { date ->
                    selectedDateTime = date
                    eventTime = dateToString(date)
                },
                eventLocation = eventLocation,
                onEventLocationChange = { eventLocation = it },
                eventSummary = eventSummary,
                onEventSummaryChange = { eventSummary = it },
                view = view
            )

            ReminderSection(
                visible = eventTime.isNotBlank(),
                reminderAtEventTime = reminderAtEventTime,
                onReminderAtEventTimeChange = { reminderAtEventTime = it },
                customReminder = customReminder,
                onCustomReminderChange = { customReminder = it },
                customReminderHours = customReminderHours,
                onCustomReminderHoursChange = { customReminderHours = it },
                customReminderMinutes = customReminderMinutes,
                onCustomReminderMinutesChange = { customReminderMinutes = it },
                showHoursDropdown = showHoursDropdown,
                onShowHoursDropdownChange = { showHoursDropdown = it },
                showMinutesDropdown = showMinutesDropdown,
                onShowMinutesDropdownChange = { showMinutesDropdown = it })

            SaveButtonSection(
                existingItem = existingItem,
                eventName = eventName,
                eventTime = eventTime,
                eventLocation = eventLocation,
                eventSummary = eventSummary,
                onCreateActivity = onCreateActivity,
                onUpdateActivity = onUpdateActivity,
                onSaveComplete = onSaveComplete
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActivityDetailsSection(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    selectedDateTime: Date?,
    onDateTimeChange: (Date) -> Unit,
    eventLocation: String,
    onEventLocationChange: (String) -> Unit,
    eventSummary: String,
    onEventSummaryChange: (String) -> Unit,
    view: View
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = "Activity details".uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.outline
        )

        ITextField(
            value = eventName,
            onValueChange = onEventNameChange,
            label = "Event Name",
            modifier = Modifier.padding(16.dp, 0.dp)
        )

        Column(modifier = Modifier.padding(16.dp, 0.dp)) {
            DateHolder(
                selectedDateTime = selectedDateTime, onSelectedDateTimeResponse = onDateTimeChange
            )
        }

        ITextField(
            value = eventLocation,
            onValueChange = onEventLocationChange,
            label = "Location",
            modifier = Modifier.padding(16.dp, 0.dp)

        )

        ITextField(
            value = eventSummary,
            onValueChange = onEventSummaryChange,
            label = "Summary",
            modifier = Modifier.padding(16.dp, 0.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HeaderSection(
    isEditMode: Boolean,
    existingItem: ItineraryItem?,
    sampleTask: TaskInfo,
    showCard: Boolean,
    onCardClick: () -> Unit
) {
    if (!isEditMode) {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedContent(
                targetState = isEditMode, transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it }, animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it }, animationSpec = tween(500)
                    )
                }) { editMode ->
                if (!editMode) {
                    Column {
                        Text(
                            text = "Need help planning your day?",
                            style = MaterialTheme.typography.headlineLargeEmphasized
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        Text(
                            text = "Take a look at the suggestion below and add them to your itinerary. You can always edit them later.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        Text(
                            text = "AI Text suggestion holder...\n" +
                                    "Seems like lately you are traveling around the downtown part of the city, maybe suggest going into a café with a view of the Eiffel Tower?",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 100f)
        )

        AnimatedVisibility(
            visible = showCard,
            exit = fadeOut(animationSpec = tween(400)) + scaleOut(animationSpec = tween(400)) + slideOutVertically(
                animationSpec = tween(400), targetOffsetY = { -it })
        ) {
            AnimatedBorderCard(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = interactionSource, indication = null
                    ) { onCardClick() }
                    .scale(scale),
                title = sampleTask.name,
                time = sampleTask.time,
                location = sampleTask.location,
                description = sampleTask.summary,
            )
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedContent(
                targetState = existingItem != null, transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it }, animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it }, animationSpec = tween(500)
                    )
                }) { isExisting ->
                Column {
                    Text(
                        text = if (isExisting) "Edit Activity" else "Create Activity",
                        style = MaterialTheme.typography.headlineLargeEmphasized
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = if (isExisting) "Update the activity details below" else "Fill in the activity details below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedBorderCard(
    title: String,
    time: String,
    location: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 10.dp
    val gradientColors =
        listOf(Color(0xFFC26A92), Color(0xFF4A8DD8), Color(0xFF784CF0), Color(0xFF4A8DD8))

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val radius = max(size.width / 2f, size.height / 2f)
                val angle = Math.toRadians(rotation.toDouble())
                val directionAngle = angle + Math.PI / 4
                val startOffsetX = -radius * cos(directionAngle)
                val startOffsetY = -radius * sin(directionAngle)
                val endOffsetX = radius * cos(directionAngle)
                val endOffsetY = radius * sin(directionAngle)

                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 4.dp.toPx()
                    shader = LinearGradientShader(
                        from = Offset(
                            (centerX + startOffsetX).toFloat(), (centerY + startOffsetY).toFloat()
                        ), to = Offset(
                            (centerX + endOffsetX).toFloat(), (centerY + endOffsetY).toFloat()
                        ), colors = gradientColors, colorStops = null
                    )
                    maskFilter = BlurMaskFilter(
                        30f, BlurMaskFilter.Blur.NORMAL
                    )
                }

                drawIntoCanvas {
                    it.nativeCanvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        cornerRadius.toPx(),
                        cornerRadius.toPx(),
                        paint
                    )
                }
            }) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .drawWithContent {
                    val size = this.size
                    drawContent()
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val radius = max(size.width / 2f, size.height / 2f)
                    val angle = Math.toRadians(rotation.toDouble())
                    val directionAngle = angle + Math.PI / 4
                    val startOffsetX = -radius * cos(directionAngle)
                    val startOffsetY = -radius * sin(directionAngle)
                    val endOffsetX = radius * cos(directionAngle)
                    val endOffsetY = radius * sin(directionAngle)
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 5.dp.toPx()
                        shader = LinearGradientShader(
                            from = Offset(
                                (centerX + startOffsetX).toFloat(),
                                (centerY + startOffsetY).toFloat()
                            ), to = Offset(
                                (centerX + endOffsetX).toFloat(), (centerY + endOffsetY).toFloat()
                            ), colors = gradientColors, colorStops = null
                        )
                    }

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawRoundRect(
                            0f,
                            0f,
                            size.width,
                            size.height,
                            cornerRadius.toPx(),
                            cornerRadius.toPx(),
                            paint
                        )
                    }
                }) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(18.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    InfoRow(icon = Icons.Default.Schedule, text = time)
                    InfoRow(icon = Icons.Default.Place, text = location)
                    InfoRow(icon = Icons.Default.HelpOutline, text = description)
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector, text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon, contentDescription = null, tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateHolder(
    selectedDateTime: Date?, onSelectedDateTimeResponse: (Date) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateTime?.time,
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }
    var formattedDate by remember(selectedDateTime) {
        mutableStateOf(selectedDateTime?.let { date ->
            dateToString(date)
        } ?: "Invalid date")
    }

    ICard(
        modifier = Modifier
            .padding(vertical = basePadding)
            .fillMaxWidth()
            .clickable {
                showDatePicker = true
            },
        color = Color.Transparent,
        content = {
            Text(
                text = "Date: $formattedDate",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(basePadding),
            )
        },
        onClick = { showDatePicker = true },
    )

    if (showDatePicker) {
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

                            val currentCalendar = Calendar.getInstance().apply {
                                timeZone = TimeZone.getDefault()
                            }

                            if (selectedCalendar.get(Calendar.YEAR) != currentCalendar.get(
                                    Calendar.YEAR
                                ) || selectedCalendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(
                                    Calendar.DAY_OF_YEAR
                                )
                            ) {
                                selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
                            }

                            onSelectedDateTimeResponse(selectedCalendar.time)
                            showDatePicker = false
                            showTimePicker = true
                        }
                    },
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateTime?.let { nonNullDate ->
                        val calendar = Calendar.getInstance(TimeZone.getDefault())
                        calendar.time = nonNullDate
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        calendar.timeZone = TimeZone.getDefault()

                        onSelectedDateTimeResponse(calendar.time)
                        showTimePicker = false
                        formattedDate = dateToString(calendar.time)
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                }) {
                    Text("Cancel")
                }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReminderSection(
    visible: Boolean,
    reminderAtEventTime: Boolean,
    onReminderAtEventTimeChange: (Boolean) -> Unit,
    customReminder: Boolean,
    onCustomReminderChange: (Boolean) -> Unit,
    customReminderHours: Int,
    onCustomReminderHoursChange: (Int) -> Unit,
    customReminderMinutes: Int,
    onCustomReminderMinutesChange: (Int) -> Unit,
    showHoursDropdown: Boolean,
    onShowHoursDropdownChange: (Boolean) -> Unit,
    showMinutesDropdown: Boolean,
    onShowMinutesDropdownChange: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = visible, enter = slideInHorizontally(
            initialOffsetX = { -it }, animationSpec = tween(500)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp, 0.dp)) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "Reminder".uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.outline
            )

            ICard(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), content = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = reminderAtEventTime,
                                onCheckedChange = onReminderAtEventTimeChange
                            )
                            Text(
                                text = "Remind me at event time",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = customReminder, onCheckedChange = onCustomReminderChange
                            )
                            Text(
                                text = "Custom reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = customReminder, enter = slideInHorizontally(
                                initialOffsetX = { -it }, animationSpec = tween(300)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 40.dp, top = 8.dp, bottom = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Remind me:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.padding(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f, false)) {
                                        ICard(
                                            modifier = Modifier.clickable {
                                                onShowHoursDropdownChange(true)
                                            }, shape = RoundedCornerShape(4.dp), content = {
                                                Text(
                                                    text = if (customReminderHours == 0) "0 hours" else "$customReminderHours hour${if (customReminderHours > 1) "s" else ""}",
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }, onClick = { }
                                        )

                                        DropdownMenu(
                                            expanded = showHoursDropdown,
                                            onDismissRequest = { onShowHoursDropdownChange(false) },
                                            modifier = Modifier.width(120.dp)
                                        ) {
                                            (0..24).forEach { hour ->
                                                DropdownMenuItem(text = {
                                                    Text(
                                                        text = if (hour == 0) "0 hours" else "$hour hour${if (hour > 1) "s" else ""}",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }, onClick = {
                                                    onCustomReminderHoursChange(hour)
                                                    onShowHoursDropdownChange(false)
                                                })
                                            }
                                        }
                                    }

                                    Text(
                                        text = "and",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Box(modifier = Modifier.weight(1f, false)) {
                                        ICard(
                                            modifier = Modifier.clickable {
                                                onShowMinutesDropdownChange(true)
                                            }, shape = RoundedCornerShape(4.dp), content = {
                                                Text(
                                                    text = "$customReminderMinutes minute${if (customReminderMinutes > 1) "s" else ""}",
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }, onClick = { }
                                        )

                                        DropdownMenu(
                                            expanded = showMinutesDropdown,
                                            onDismissRequest = { onShowMinutesDropdownChange(false) },
                                            modifier = Modifier.width(120.dp)
                                        ) {
                                            listOf(5, 10, 15, 30, 45).forEach { minutes ->
                                                DropdownMenuItem(text = {
                                                    Text("$minutes minute${if (minutes > 1) "s" else ""}")
                                                }, onClick = {
                                                    onCustomReminderMinutesChange(minutes)
                                                    onShowMinutesDropdownChange(false)
                                                })
                                            }
                                        }
                                    }

                                    Text(
                                        text = "before",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }, onClick = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SaveButtonSection(
    existingItem: ItineraryItem?,
    eventName: String,
    eventTime: String,
    eventLocation: String,
    eventSummary: String,
    onCreateActivity: (String, String, String, String) -> Unit,
    onUpdateActivity: (String, String, String, String, String) -> Unit,
    onSaveComplete: () -> Unit
) {
    IButton(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 8.dp)
            .fillMaxWidth(), onClick = {
        if (eventName.isNotBlank() && eventTime.isNotBlank() && eventLocation.isNotBlank() && eventSummary.isNotBlank()) {
            if (existingItem != null) {
                onUpdateActivity(
                    existingItem.id ?: "", eventName, eventTime, eventLocation, eventSummary
                )
            } else {
                onCreateActivity(
                    eventName, eventTime, eventLocation, eventSummary
                )
            }
            onSaveComplete()
        }
    }, text = {
        Text(
            text = if (existingItem != null) "Update activity" else "Save activity",
            style = MaterialTheme.typography.labelLargeEmphasized
        )
    }, importance = ButtonImportance.Primary
    )
}

@ThemePreviews
@Composable
private fun CreateEventScreenPreview() {
    PreviewWrapper {
        CreateEventScreen(
            navController = rememberNavController(),
            existingItem = null,
            onCreateActivity = { _, _, _, _ -> },
            onUpdateActivity = { _, _, _, _, _ -> },
            onSaveComplete = { })
    }
}
