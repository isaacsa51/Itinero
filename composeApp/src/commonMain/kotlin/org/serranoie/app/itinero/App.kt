package org.serranoie.app.itinero

import org.serranoie.core.itinero.designsystem.ui.theme.ItineroTheme
import androidx.compose.runtime.Composable
import org.serranoie.feature.itinero.onboard.JourneyContent

@Composable
fun App() {
    ItineroTheme {
//        OnboardScreen(onNavigate = {})

        JourneyContent()

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