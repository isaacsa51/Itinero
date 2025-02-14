package org.serranoie.app.itinero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.serranoie.app.itinero.ui.theme.ItineroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    ItineroTheme {
        Surface {
            App()
        }
    }
}