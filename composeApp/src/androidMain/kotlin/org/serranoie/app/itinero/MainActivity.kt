package org.serranoie.app.itinero

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.serranoie.app.itinero.navigation.AppContent
import org.serranoie.core.itinero.designsystem.ui.theme.ItineroTheme
import org.serranoie.feature.itinero.auth.AuthScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ItineroTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
//                   AppContent()
                    AuthScreen()
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun AppAndroidPreview() {
    ItineroTheme {
        Surface {
            App()
        }
    }
}