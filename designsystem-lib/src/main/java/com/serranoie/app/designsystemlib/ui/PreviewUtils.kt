package com.serranoie.app.designsystemlib.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    ItineroTheme {
        Surface {
            content()
        }
    }
}
