/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CreateEventScreen.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import java.util.Locale
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateEventScreen(navController: NavController) {

    var eventName by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var eventSummary by remember { mutableStateOf("") }

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
                onClick = { },
                text = {
                    Text(
                        text = "Save activity",
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

                    maskFilter = android.graphics.BlurMaskFilter(
                        30f, android.graphics.BlurMaskFilter.Blur.NORMAL
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
        CreateEventScreen(navController = rememberNavController())
    }
}