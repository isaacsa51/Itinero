package com.serranoie.app.designsystem.ui

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.serranoie.app.designsystem.ui.theme.ItineroTheme

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    ItineroTheme {
        Surface {
            content()
        }
    }
}