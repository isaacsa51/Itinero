package com.serranoie.app.feature.itinerary

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInput
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.SwipeButton
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.theme.component.formatMyDate
import com.serranoie.app.designsystemlib.ui.utils.Constants
import com.serranoie.app.designsystemlib.ui.utils.Utils.errorFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ItinerarySuggestion(
    val name: String, val time: String, val location: String, val summary: String
)

val aiSuggestion = ItinerarySuggestion(
    name = "AI Suggestion", time = run {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 2)
        SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault()).format(calendar.time)
    }, location = "Paris, France", summary = "Summary"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateEventScreen(
    existingItem: ItineraryItem? = null,
    onCreateActivity: (name: String, time: String, location: String, description: String) -> Unit = { _, _, _, _ -> },
    onUpdateActivity: (id: String, name: String, time: String, location: String, description: String) -> Unit = { _, _, _, _, _ -> },
    onDeleteActivity: (id: String) -> Unit = { },
    onCompleted: (id: String) -> Unit = {},
    onSaveComplete: () -> Unit = {},
    onBack: (() -> Unit),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var eventName by remember { mutableStateOf(existingItem?.name ?: "") }
    var eventTime by remember { mutableStateOf(existingItem?.time ?: "") }
    var eventLocation by remember { mutableStateOf(existingItem?.location ?: "") }
    var eventSummary by remember { mutableStateOf(existingItem?.description ?: "") }

    var showCard by remember { mutableStateOf(true) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    var selectedDateTime by remember {
        mutableStateOf<Date?>(existingItem?.let { item ->
            try {
                val dateTimeString = "${item.date} ${item.time}"
                SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault()).parse(dateTimeString)
            } catch (e: Exception) {
                Log.e(
                    "CreateEventScreen",
                    "Error parsing existing item date/time: ${item.date} ${item.time}",
                    e
                )
                Date()
            }
        } ?: Date())
    }

    var reminderAtEventTime by remember { mutableStateOf(false) }
    var customReminder by remember { mutableStateOf(false) }
    var customReminderHours by remember { mutableIntStateOf(0) }
    var customReminderMinutes by remember { mutableIntStateOf(15) }
    var showHoursDropdown by remember { mutableStateOf(false) }
    var showMinutesDropdown by remember { mutableStateOf(false) }

    val isEditMode = existingItem != null
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val scrollState = scrollBehavior.state
    val appBarExpanded by remember {
        derivedStateOf { scrollState.collapsedFraction < 0.9f }
    }
    val expandedAppBarHeight = 180.dp
    val headerTranslation = (expandedAppBarHeight / 2)

    val scrollStateContent = rememberScrollState()

    LaunchedEffect(isEditMode, existingItem) {
        if (isEditMode) {
            existingItem?.let {
                eventName = it.name
                eventTime = it.time
                if (!isEditMode || eventTime.isEmpty()) {
                    eventTime = it.time
                    if (!isEditMode) {
                        selectedDateTime =
                            SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault()).parse(
                                eventTime
                            ) ?: Date()
                    }
                }

                eventLocation = it.location
                eventSummary = it.description
            }
        }
    }

    val canSave =
        eventName.isNotBlank() && eventTime.isNotBlank() && eventLocation.isNotBlank() && eventSummary.isNotBlank() && !isSaving

    val handleSave: () -> Unit = {
        if (canSave && !isSaving) {
            isSaving = true

            coroutineScope.launch {
                kotlinx.coroutines.delay(5000L)
                isSaving = false
            }

            if (existingItem != null) {
                onUpdateActivity(
                    existingItem.id ?: "", eventName, eventTime, eventLocation, eventSummary
                )
            } else {
                onCreateActivity(
                    eventName, eventTime, eventLocation, eventSummary
                )
            }
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            isSaving = false
        }
    }

    eventName.ifBlank { "Need help planning your day?" }
    val dateSubtitle = selectedDateTime?.let { formatMyDate(it) } ?: ""

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CollapsedEventHeader(
                    modifier = Modifier,
                    visible = !appBarExpanded,
                    eventName = eventName,
                    dateSubtitle = dateSubtitle,
                    onBack = onBack,
                    canSave = canSave,
                    onSave = handleSave
                )
                TopAppBar(
                    title = {
                        ExpandedEventHeader(
                            modifier = Modifier.graphicsLayer {
                                translationY =
                                    scrollState.collapsedFraction * headerTranslation.toPx()
                            },
                            visible = appBarExpanded,
                            eventName = eventName,
                        )
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ), scrollBehavior = scrollBehavior
                )
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollStateContent)
        ) {
            HeaderSection(
                isEditMode = isEditMode,
                existingItem = existingItem,
                sampleTask = aiSuggestion,
                showCard = showCard,
                onCardClick = {
                    eventName = aiSuggestion.name
                    eventTime = aiSuggestion.time
                    if (!isEditMode || eventTime.isEmpty()) {
                        eventTime = aiSuggestion.time
                        if (!isEditMode) {
                            selectedDateTime =
                                SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault()).parse(
                                    eventTime
                                ) ?: Date()
                        }
                    }

                    eventLocation = aiSuggestion.location
                    eventSummary = aiSuggestion.summary
                    showCard = false
                })

            ActivityDetailsSection(
                eventName = eventName,
                onEventNameChange = { eventName = it },
                selectedDateTime = selectedDateTime,
                onDateTimeSelected = { newDate ->
                    selectedDateTime = newDate
                    eventTime = formatMyDate(newDate)
                },
                eventLocation = eventLocation,
                onEventLocationChange = { eventLocation = it },
                eventSummary = eventSummary,
                onEventSummaryChange = { eventSummary = it },
            )

            // TODO: Add functionality to display notifications
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
                coroutineScope = coroutineScope,
                onCompleted = onCompleted,
                handleSave = handleSave,
                onDeleteActivity = onDeleteActivity,
                onSaveComplete = onSaveComplete,
                context = context,
                canSave = canSave,
                isSaving = isSaving
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActivityDetailsSection(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    selectedDateTime: Date?,
    onDateTimeSelected: (Date) -> Unit,
    eventLocation: String,
    onEventLocationChange: (String) -> Unit,
    eventSummary: String,
    onEventSummaryChange: (String) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(Constants.basePadding, Constants.smallPadding),
            text = "Activity details".uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.outline
        )

        ITextField(
            value = eventName,
            onValueChange = onEventNameChange,
            label = "Event Name",
            modifier = Modifier.padding(Constants.basePadding, 0.dp)
        )

        DateTimeInput(
            selectedDateTime = selectedDateTime,
            onDateTimeSelected = onDateTimeSelected,
            modifier = Modifier.padding(
                top = Constants.smallPadding * 1.5f,
                start = Constants.basePadding,
                end = Constants.basePadding,
                bottom = Constants.extraSmallPadding
            )
        )

        ITextField(
            value = eventLocation,
            onValueChange = onEventLocationChange,
            label = "Location",
            modifier = Modifier.padding(
                top = 0.dp,
                start = Constants.basePadding,
                end = Constants.basePadding,
                bottom = Constants.extraSmallPadding
            )
        )

        ITextField(
            value = eventSummary,
            onValueChange = onEventSummaryChange,
            label = "Description",
            modifier = Modifier.padding(Constants.basePadding, 0.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HeaderSection(
    isEditMode: Boolean,
    existingItem: ItineraryItem?,
    sampleTask: ItinerarySuggestion,
    showCard: Boolean,
    onCardClick: () -> Unit
) {
    if (!isEditMode) {
        Column(modifier = Modifier.padding(Constants.basePadding)) {
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
                            text = "Take a look at the suggestion below and add it to your itinerary. You can always edit it later.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.padding(Constants.smallPadding))

                        Text(
                            text = "AI Text suggestion holder...\n" + "Seems like lately you are traveling around the downtown part of the city, maybe suggest going into a café with a view of the Eiffel Tower?",
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
            ICard(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = interactionSource, indication = null
                    ) { onCardClick() }
                    .scale(scale)
                    .padding(Constants.basePadding),
                showAnimatedBorder = true,
                headerTitle = sampleTask.name,
                content = {
                    Column(
                        modifier = Modifier.padding(Constants.basePadding)
                    ) {
                        Text(
                            text = "${sampleTask.location} — ${sampleTask.time}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${sampleTask.summary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                })
        }
    } else {
        Column(modifier = Modifier.padding(Constants.basePadding)) {
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
                        text = if (isExisting) "Update the activity details below" else "Fill in the activity details below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
        Column(modifier = Modifier.padding(Constants.basePadding, 0.dp)) {
            Text(
                modifier = Modifier.padding(Constants.smallPadding),
                text = "Reminder".uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.outline
            )

            ICard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Constants.commonCornerRadius),
                content = {
                    Column(modifier = Modifier.padding(Constants.basePadding)) {
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
                                modifier = Modifier.padding(start = Constants.smallPadding)
                            )
                        }

                        Spacer(modifier = Modifier.padding(Constants.extraSmallPadding))

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
                                modifier = Modifier.padding(start = Constants.smallPadding)
                            )
                        }

                        AnimatedVisibility(
                            visible = customReminder, enter = slideInHorizontally(
                                initialOffsetX = { -it }, animationSpec = tween(300)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        start = Constants.largePadding + Constants.extraSmallPadding,
                                        top = Constants.smallPadding,
                                        bottom = Constants.smallPadding
                                    )
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Remind me:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.padding(Constants.extraSmallPadding))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Constants.smallPadding),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f, false)) {
                                        ICard(
                                            modifier = Modifier.clickable {
                                                onShowHoursDropdownChange(true)
                                            },
                                            shape = RoundedCornerShape(Constants.extraSmallPadding),
                                            onClick = { onShowHoursDropdownChange(true) },
                                            content = {
                                                Text(
                                                    text = if (customReminderHours == 0) "0 hours" else "$customReminderHours hour${if (customReminderHours > 1) "s" else ""}",
                                                    modifier = Modifier.padding(Constants.smallPadding),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            })

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
                                            },
                                            shape = RoundedCornerShape(Constants.extraSmallPadding),
                                            onClick = { onShowMinutesDropdownChange(true) },
                                            content = {
                                                Text(
                                                    text = "$customReminderMinutes minute${if (customReminderMinutes > 1) "s" else ""}",
                                                    modifier = Modifier.padding(Constants.smallPadding),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            })

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
                })
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
    coroutineScope: CoroutineScope,
    onCompleted: (String) -> Unit,
    handleSave: () -> Unit,
    onDeleteActivity: (String) -> Unit,
    onSaveComplete: () -> Unit,
    context: android.content.Context,
    canSave: Boolean,
    isSaving: Boolean
) {
    val isEditMode = existingItem != null
    val isCompleted = existingItem?.isCompleted ?: false
    val view = LocalView.current

    Column(modifier = Modifier.padding(bottom = Constants.basePadding)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.basePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Column(
                    modifier = Modifier
                        .padding(top = Constants.basePadding)
                        .weight(1f)
                ) {
                    SwipeButton(
                        text = if (isCompleted) "Mark as not completed" else "Mark as completed",
                        isComplete = isCompleted,
                        onSwipe = {
                            if (!isCompleted) {
                                coroutineScope.launch {
                                    existingItem?.id?.let { onCompleted(it) }
                                }
                            } else {
                                coroutineScope.launch {
                                    existingItem?.id?.let { onCompleted(it) }
                                }
                            }
                        })
                }
            }
        }

        if (isEditMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Constants.basePadding),
                horizontalArrangement = Arrangement.spacedBy(Constants.basePadding)
            ) {
                IButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = Constants.basePadding),
                    onClick = handleSave,
                    text = {
                        Text(
                            text = "Update activity",
                            style = MaterialTheme.typography.labelLargeEmphasized
                        )
                    },
                    importance = ButtonImportance.Primary
                )

                Box(
                    modifier = Modifier.padding(top = Constants.basePadding)
                ) {
                    IButton(
                        onClick = { /* Handled by overlay */ },
                        text = { Text("Delete") },
                        importance = ButtonImportance.Error,
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .combinedClickable(onClick = {
                                view.errorFeedback()
                                Toast.makeText(
                                    context, "Please hold longer to delete", Toast.LENGTH_SHORT
                                ).show()
                            }, onLongClick = {
                                existingItem?.id?.let {
                                    onDeleteActivity(it)
                                }
                            })
                    )
                }
            }
        } else {
            IButton(
                modifier = Modifier
                    .padding(top = if (isEditMode) Constants.smallPadding else Constants.basePadding)
                    .padding(Constants.basePadding)
                    .fillMaxWidth(), onClick = handleSave, text = {
                    Text(
                        text = "Save activity",
                        style = MaterialTheme.typography.labelLargeEmphasized
                    )
                }, importance = ButtonImportance.Primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollapsedEventHeader(
    modifier: Modifier,
    visible: Boolean,
    eventName: String,
    dateSubtitle: String,
    onBack: () -> Unit,
    canSave: Boolean,
    onSave: () -> Unit,
) {
    TopAppBar(modifier = modifier, navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back"
            )
        }
    }, title = {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween()),
            exit = fadeOut(animationSpec = tween())
        ) {
            Column {
                Text(
                    text = if (eventName.isBlank()) "Need help planning your day?" else eventName,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (dateSubtitle.isNotEmpty()) {
                    Text(
                        text = dateSubtitle, style = MaterialTheme.typography.labelLargeEmphasized
                    )
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandedEventHeader(
    modifier: Modifier,
    visible: Boolean,
    eventName: String,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween()),
        exit = fadeOut(animationSpec = tween())
    ) {
        Column(modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = Constants.basePadding)) {
            Text(
                text = eventName.ifBlank { "Need help planning your day?" },
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLargeEmphasized
            )
        }
    }

}

@DevicePreview
@Composable
private fun CreateEventScreenPreview() {
    PreviewWrapper {
        CreateEventScreen(
            existingItem = null,
            onCreateActivity = { _, _, _, _ -> },
            onUpdateActivity = { _, _, _, _, _ -> },
            onDeleteActivity = {},
            onSaveComplete = { },
            onBack = { })
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon, contentDescription = null, tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(Constants.smallIconSize - 8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
