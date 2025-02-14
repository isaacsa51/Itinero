package org.serranoie.app.itinero

import org.serranoie.app.itinero.ui.theme.ItineroTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import itinero.composeapp.generated.resources.Res
import itinero.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.serranoie.app.itinero.ui.component.ItineroButton
import org.serranoie.feature.itinero.onboard.OnboardScreen

@Composable
fun App() {
    ItineroTheme {
        OnboardScreen(onNavigate = {})

        // ! Basic greeting component
        /*var showContent by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            ItineroButton(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }*/
    }
}