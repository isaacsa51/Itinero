package com.serranoie.app.feature.itinerary

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import java.util.Locale
import kotlinx.coroutines.delay

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
    var showCard by remember { mutableStateOf(true) }

    // Check if we're in edit mode (any field has data)
    val isEditMode = eventName.isNotEmpty() || eventTime.isNotEmpty() ||
            eventLocation.isNotEmpty() || eventSummary.isNotEmpty()

    val sampleTask = TaskInfo(
        name = "Visit the Eiffel Tower",
        time = "12 Jun 2025, 10:00 AM",
        location = "Eiffel Tower, Paris, France", // TODO: Add that the user when tapping the location redirects to GMaps
        summary = "Daily itinerary planning and review"
    )

    // Animation values for text field positions
    val textFieldsOffset by animateFloatAsState(
        targetValue = if (showCard && !isEditMode) 0f else 0f,
        animationSpec = tween(durationMillis = 400)
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

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 100f)
                )

                AnimatedVisibility(
                    visible = showCard,
                    exit = fadeOut(animationSpec = tween(400)) +
                            scaleOut(animationSpec = tween(400)) +
                           slideOutVertically(
                               animationSpec = tween(400),
                               targetOffsetY = { -it }
                           )
                ) {
                    AnimatedBorderCard(
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                eventName = sampleTask.name
                                eventTime = sampleTask.time
                                eventLocation = sampleTask.location
                                eventSummary = sampleTask.summary
                                showCard = false
                            }
                            .scale(scale),
                        title = sampleTask.name,
                        time = sampleTask.time,
                        location = sampleTask.location,
                        description = sampleTask.summary,
                    )
                }
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

            // Text fields section
            Column {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "Activity details".uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    color = MaterialTheme.colorScheme.outline
                )

                // Track when values change to trigger highlight animation
                var highlightName by remember { mutableStateOf(false) }
                var highlightTime by remember { mutableStateOf(false) }
                var highlightLocation by remember { mutableStateOf(false) }
                var highlightSummary by remember { mutableStateOf(false) }

                LaunchedEffect(eventName) {
                    if (eventName.isNotBlank() && !highlightName) {
                        delay(50)
                        highlightName = true
                        delay(800)
                        highlightName = false
                    }
                }

                LaunchedEffect(eventTime) {
                    if (eventTime.isNotBlank() && !highlightTime) {
                        delay(100)
                        highlightTime = true
                        delay(800)
                        highlightTime = false
                    }
                }

                LaunchedEffect(eventLocation) {
                    if (eventLocation.isNotBlank() && !highlightLocation) {
                        delay(150)
                        highlightLocation = true
                        delay(800)
                        highlightLocation = false
                    }
                }

                LaunchedEffect(eventSummary) {
                    if (eventSummary.isNotBlank() && !highlightSummary) {
                        delay(200)
                        highlightSummary = true
                        delay(800)
                        highlightSummary = false
                    }
                }

                ITextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = "Event Name",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .alpha(if (highlightName) 0.7f else 1f)
                        .scale(if (highlightName) 1.02f else 1f)
                )

                ITextField(
                    value = eventTime,
                    onValueChange = { eventTime = it },
                    label = "Time",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .alpha(if (highlightTime) 0.7f else 1f)
                        .scale(if (highlightTime) 1.02f else 1f)
                )

                ITextField(
                    value = eventLocation,
                    onValueChange = { eventLocation = it },
                    label = "Location",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .alpha(if (highlightLocation) 0.7f else 1f)
                        .scale(if (highlightLocation) 1.02f else 1f)
                )

                ITextField(
                    value = eventSummary,
                    onValueChange = { eventSummary = it },
                    label = "Summary",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .alpha(if (highlightSummary) 0.7f else 1f)
                        .scale(if (highlightSummary) 1.02f else 1f)
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
}

data class TaskInfo(
    val name: String, val time: String, val location: String, val summary: String
)

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
