package org.serranoie.app.itinero

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.serranoie.core.itinero.designsystem.ui.theme.ItineroTheme
import org.serranoie.feature.itinero.onboard.OnboardScreen

@Composable
fun App() {
    ItineroTheme {
        Surface {
            OnboardScreen(onNavigateAuth = { })
        }
    }
}