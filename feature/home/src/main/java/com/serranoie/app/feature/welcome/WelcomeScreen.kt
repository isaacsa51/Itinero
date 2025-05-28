package com.serranoie.app.feature.welcome

import android.media.Image
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.designsystem.ui.theme.component.OtpInputField
import com.serranoie.app.feature.home.R

enum class TripAction { CREATE, JOIN }

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeScreen() {
    Scaffold { padding ->
        var selectedAction by remember { mutableStateOf<TripAction?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (selectedAction == null) Arrangement.Center else Arrangement.Top
        ) {
            if (selectedAction != null) {
                // Show welcome title at the top when an action is selected
                Text(
                    text = "Bienvenido a Itinero!",
                    style = typography.titleLargeEmphasized,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            AnimatedContent(
                targetState = selectedAction,
                transitionSpec = {
                    when (targetState) {
                        null -> {
                            // Returning to welcome - slide down from up
                            slideInVertically(
                                animationSpec = tween(400),
                                initialOffsetY = { -it / 2 }
                            ) + fadeIn(tween(400)) with
                                    slideOutVertically(
                                        animationSpec = tween(400),
                                        targetOffsetY = { it / 2 }
                                    ) + fadeOut(tween(400))
                        }

                        TripAction.CREATE -> {
                            // Create trip - slide in from left
                            slideInHorizontally(
                                animationSpec = tween(500),
                                initialOffsetX = { -it }
                            ) + fadeIn(tween(500)) with
                                    slideOutHorizontally(
                                        animationSpec = tween(500),
                                        targetOffsetX = { it }
                                    ) + fadeOut(tween(500))
                        }

                        TripAction.JOIN -> {
                            // Join trip - slide in from right
                            slideInHorizontally(
                                animationSpec = tween(500),
                                initialOffsetX = { it }
                            ) + fadeIn(tween(500)) with
                                    slideOutHorizontally(
                                        animationSpec = tween(500),
                                        targetOffsetX = { -it }
                                    ) + fadeOut(tween(500))
                        }
                    }
                },
                label = "TripActionAnimation",
                modifier = Modifier.fillMaxWidth()
            ) { action ->
                when (action) {
                    null -> WelcomeOptions(
                        onCreateTrip = { selectedAction = TripAction.CREATE },
                        onJoinTrip = { selectedAction = TripAction.JOIN }
                    )

                    TripAction.CREATE -> CreateTripInputs(onBack = { selectedAction = null })

                    TripAction.JOIN -> JoinTripInputs(onBack = { selectedAction = null })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeOptions(onCreateTrip: () -> Unit, onJoinTrip: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido a Itinero!",
            style = typography.displaySmallEmphasized,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Comienza a planear, ver y manejar todo tipo de información de tu próximo viaje creando un grupo o accediendo a uno!",
            style = typography.bodyMedium,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionTripButton(
                title = "Crear nuevo viaje",
                imageRes = R.drawable.img_add_travel,
                modifier = Modifier.weight(1f),
                onClick = onCreateTrip
            )
            Spacer(modifier = Modifier.width(16.dp))
            ActionTripButton(
                title = "Unirme con código",
                imageRes = R.drawable.img_scan,
                modifier = Modifier.weight(1f),
                onClick = onJoinTrip
            )
        }
    }
}

@Composable
fun CreateTripInputs(onBack: () -> Unit) {
    var tripName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var travelDirection by remember { mutableStateOf("OUTBOUND") }
    var accommodationName by remember { mutableStateOf("") }
    var accommodationPhone by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var reservationCode by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear nuevo viaje",
            style = typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Basic Trip Information
        Text(
            text = "Información básica",
            style = typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Nombre del viaje",
            value = tripName,
            onValueChange = { tripName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Destino",
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Descripción/Resumen",
            value = summary,
            onValueChange = { summary = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Describe tu viaje (opcional)"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ITextField(
                label = "Fecha inicio (YYYY-MM-DD)",
                value = startDate,
                onValueChange = { startDate = it },
                modifier = Modifier.weight(1f)
            )
            ITextField(
                label = "Fecha fin (YYYY-MM-DD)",
                value = endDate,
                onValueChange = { endDate = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Dirección del viaje",
            value = travelDirection,
            onValueChange = { travelDirection = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "OUTBOUND o RETURN"
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Accommodation Information
        Text(
            text = "Información de alojamiento",
            style = typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Nombre del alojamiento",
            value = accommodationName,
            onValueChange = { accommodationName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Teléfono del alojamiento",
            value = accommodationPhone,
            onValueChange = { accommodationPhone = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ITextField(
                label = "Check-in (YYYY-MM-DD HH:MM)",
                value = checkIn,
                onValueChange = { checkIn = it },
                modifier = Modifier.weight(1f)
            )
            ITextField(
                label = "Check-out (YYYY-MM-DD HH:MM)",
                value = checkOut,
                onValueChange = { checkOut = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Código de reservación (opcional)",
            value = reservationCode,
            onValueChange = { reservationCode = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Location Information
        Text(
            text = "Información de ubicación",
            style = typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Nombre de la ubicación",
            value = locationName,
            onValueChange = { locationName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ITextField(
                label = "Latitud",
                value = latitude,
                onValueChange = { latitude = it },
                modifier = Modifier.weight(1f)
            )
            ITextField(
                label = "Longitud",
                value = longitude,
                onValueChange = { longitude = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Additional Information
        Text(
            text = "Información adicional",
            style = typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Información extra (opcional)",
            value = extraInfo,
            onValueChange = { extraInfo = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Información extra (opcional)"
        )
        Spacer(modifier = Modifier.height(16.dp))

        ITextField(
            label = "Información adicional (opcional)",
            value = additionalInfo,
            onValueChange = { additionalInfo = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Información adicional (opcional)"
        )
        Spacer(modifier = Modifier.height(32.dp))

        val isFormValid = tripName.isNotBlank() &&
                destination.isNotBlank() &&
                startDate.isNotBlank() &&
                endDate.isNotBlank() &&
                accommodationName.isNotBlank() &&
                locationName.isNotBlank() &&
                latitude.isNotBlank() &&
                longitude.isNotBlank()

        IButton(
            text = { Text("Crear") },
            onClick = {
                // TODO: Handle trip creation with all form data
                // Calculate totalDays from startDate and endDate
                // Generate groupCode
                // Set hasPendingActions to false initially
                // Set totalMembers to 1 initially
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← Volver")
        }
    }
}

@Composable
fun JoinTripInputs(onBack: () -> Unit) {
    var otpCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unirme a un viaje",
            style = typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Ingresa el código del viaje",
            style = typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OtpInputField(
            otpText = otpCode,
            otpCount = 5,
            onOtpTextChange = { otp, isComplete ->
                otpCode = otp
                // You can handle completion here if needed
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        IButton(
            text = { Text("Unirme") },
            onClick = { /* Unirse con código */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = otpCode.length == 5
        )
        Spacer(modifier = Modifier.height(16.dp))

        IOutlineButton(
            text = { Text("Escanear código QR") },
            onClick = { /* Abrir cámara */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← Volver")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionTripButton(
    title: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@ThemePreviews
@Composable
private fun WelcomeScreenPreview() {
    PreviewWrapper {
        WelcomeScreen()
    }
}
