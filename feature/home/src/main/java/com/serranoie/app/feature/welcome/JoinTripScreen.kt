package com.serranoie.app.feature.welcome

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystemlib.ui.theme.component.OtpInputField
import com.serranoie.app.feature.TravelUiState

/**
 * Displays a screen for joining a trip by entering a 5-character code or scanning a QR code.
 *
 * Shows input fields and buttons for joining a trip, handling loading and error states, and navigation actions.
 *
 * @param uiState The current UI state for the join trip process.
 * @param onTripJoined Callback invoked with the trip code (prefixed with "ITN-") when the user attempts to join.
 * @param onNavigateBack Callback invoked when the user navigates back.
 * @param onNavigateToCameraScanner Callback invoked when the user chooses to scan a QR code.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JoinTripScreen(
    uiState: TravelUiState = TravelUiState.Idle,
    onTripJoined: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToCameraScanner: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("") }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text("Join a trip") }, navigationIcon = {
                    IconButton(onClick = onNavigateBack, content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    })
                }, scrollBehavior = scrollBehavior
            )
        },

        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ingresa el código del viaje",
                style = typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            OtpInputField(
                otpText = otpCode, otpCount = 5, onOtpTextChange = { otp, _ ->
                    otpCode = otp
                }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            if (uiState is TravelUiState.Error) {
                Text(
                    text = uiState.message,
                    style = typography.bodySmall,
                    color = colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (uiState is TravelUiState.Loading) {
                LoadingIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                IButton(
                    text = { Text("Unirme") },
                    onClick = { onTripJoined("ITN-$otpCode") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otpCode.length == 5
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            IOutlineButton(
                text = { Text("Escanear código QR") },
                onClick = { onNavigateToCameraScanner() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is TravelUiState.Loading
            )
        }
    }
}

@ThemePreviews
@Composable
fun JoinTripScreenPreview() {
    PreviewWrapper {
        JoinTripScreen(
            uiState = TravelUiState.Idle,
            onTripJoined = {},
            onNavigateBack = {},
            onNavigateToCameraScanner = {})
    }
}
