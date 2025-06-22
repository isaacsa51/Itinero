package com.serranoie.app.feature.welcome

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.feature.home.R

@Composable
fun WelcomeScreen(
    onNavigateToCreateTravel: () -> Unit = {},
    onNavigateToJoinTrip: () -> Unit = {}
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WelcomeOptions(
                onCreateTrip = onNavigateToCreateTravel,
                onJoinTrip = onNavigateToJoinTrip
            )
        }
    }
}

/**
 * Displays welcome options for creating a new trip or joining an existing trip.
 *
 * Presents a welcome message and two action buttons, allowing the user to either start a new trip or join one using a code.
 *
 * @param onCreateTrip Callback invoked when the "Create new trip" button is clicked.
 * @param onJoinTrip Callback invoked when the "Join with code" button is clicked.
 */
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

/**
 * Displays a stylized, clickable button with an image and a label for trip-related actions.
 *
 * The button shows an image above a text label and triggers the provided click handler when pressed.
 *
 * @param title The text label displayed below the image.
 * @param imageRes The drawable resource ID for the image shown on the button.
 * @param modifier Optional modifier for customizing the button's appearance and layout.
 * @param onClick Lambda invoked when the button is clicked.
 */
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
        WelcomeScreen(
            onNavigateToCreateTravel = {},
            onNavigateToJoinTrip = {}
        )
    }
}
