package com.serranoie.app.feature.itinerary

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import java.util.Locale

/**
 * Displays a screen for creating or editing an itinerary event.
 *
 * Shows a form for entering event details such as name, time, location, and summary. If an existing item is provided, the form is pre-filled for editing; otherwise, it supports creating a new event. In creation mode, a sample suggestion and animated card are shown to assist the user. On saving, invokes the appropriate callback for creation or update, followed by a completion callback.
 *
 * @param existingItem The itinerary item to edit, or null to create a new event.
 * @param onCreateActivity Callback invoked with event details when creating a new event.
 * @param onUpdateActivity Callback invoked with the event ID and updated details when editing an existing event.
 * @param onSaveComplete Callback invoked after the save operation completes.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    existingItem: ItineraryItem? = null,
    onCreateActivity: (name: String, time: String, location: String, summary: String) -> Unit = { _, _, _, _ -> },
    onUpdateActivity: (id: String, name: String, time: String, location: String, summary: String) -> Unit = { _, _, _, _, _ -> },
    onSaveComplete: () -> Unit = {}
) {
    // Initialize fields with existing item data or empty values
    var eventName by remember { mutableStateOf(existingItem?.title ?: "") }
    var eventTime by remember { mutableStateOf(existingItem?.time ?: "") }
    var eventLocation by remember { mutableStateOf(existingItem?.location ?: "") }
    var eventSummary by remember { mutableStateOf(existingItem?.description ?: "") }

    // Check if we're in edit mode (any field has data)
    val isEditMode = eventName.isNotEmpty() || eventTime.isNotEmpty() ||
            eventLocation.isNotEmpty() || eventSummary.isNotEmpty()

    val sampleTask = TaskInfo(
        name = "Visit the Eiffel Tower",
        time = "12 Jun 2025, 10:00 AM",
        location = "Eiffel Tower, Paris, France", // TODO: Add that the user when tapping the location redirects to GMaps
        summary = "Daily itinerary planning and review"
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // Only show the help section and AI suggestion if NOT in edit mode
            if (!isEditMode) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                        text = "AI Text suggestion holder...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                AnimatedBorderCard(
                    modifier = Modifier.clickable {
                        eventName = sampleTask.name
                        eventTime = sampleTask.time
                        eventLocation = sampleTask.location
                        eventSummary = sampleTask.summary
                    },
                    title = sampleTask.name,
                    time = sampleTask.time,
                    location = sampleTask.location,
                    description = sampleTask.summary,
                )
            } else {
                // Show edit mode header
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (existingItem != null) "Edit Activity" else "Create Activity",
                        style = MaterialTheme.typography.headlineLargeEmphasized
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = if (existingItem != null)
                            "Update the activity details below" else
                            "Fill in the activity details below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = "Activity details".uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.colorScheme.outline
            )

            ITextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = "Event Name",
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            ITextField(
                value = eventTime,
                onValueChange = { eventTime = it },
                label = "Time",
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            ITextField(
                value = eventLocation,
                onValueChange = { eventLocation = it },
                label = "Location",
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            ITextField(
                value = eventSummary,
                onValueChange = { eventSummary = it },
                label = "Summary",
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            IButton(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                onClick = {
                    // Validate that required fields are not empty
                    if (eventName.isNotBlank() && eventTime.isNotBlank() &&
                        eventLocation.isNotBlank() && eventSummary.isNotBlank()
                    ) {

                        if (existingItem != null) {
                            // Update existing item
                            onUpdateActivity(
                                existingItem.id ?: "",
                                eventName,
                                eventTime,
                                eventLocation,
                                eventSummary
                            )
                        } else {
                            // Create new item
                            onCreateActivity(
                                eventName,
                                eventTime,
                                eventLocation,
                                eventSummary
                            )
                        }

                        onSaveComplete()
                    }
                },
                text = {
                    Text(
                        text = if (existingItem != null) "Update activity" else "Save activity",
                        style = MaterialTheme.typography.labelLargeEmphasized
                    )
                },
                importance = ButtonImportance.Primary
            )
        }
    }
}

data class TaskInfo(
    val name: String, val time: String, val location: String, val summary: String
)

/**
 * Displays a card with a continuously rotating animated gradient border and event details.
 *
 * The card shows a title and three rows for time, location, and description, each with an icon. The border animates with a multi-color gradient effect.
 *
 * @param title The main title displayed at the top of the card.
 * @param time The event time shown with a clock icon.
 * @param location The event location shown with a location icon.
 * @param description The event description shown with a help icon.
 * @param modifier Optional modifier for styling or layout.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedBorderCard(
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
            animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
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

/**
 * Displays a horizontal row with an icon and accompanying text, aligned vertically center.
 *
 * @param icon The icon to display at the start of the row.
 * @param text The text to display next to the icon.
 */
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

/**
 * Displays a preview of the CreateEventScreen composable for UI testing and design inspection.
 */
@ThemePreviews
@Composable
private fun CreateEventScreenPreview() {
    PreviewWrapper {
        CreateEventScreen(
            navController = rememberNavController(),
            existingItem = null,
            onCreateActivity = { _, _, _, _ -> },
            onUpdateActivity = { _, _, _, _, _ -> },
            onSaveComplete = { }
        )
    }
}
