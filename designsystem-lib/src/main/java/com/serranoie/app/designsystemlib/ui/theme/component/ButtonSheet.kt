package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.bottomSheetButtonSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.bottomSheetCloseSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.bottomSheetContentSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.bottomSheetIconSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.bottomSheetTitleSpacing
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

@Composable
fun BottomSheetContent(
    scannedCode: String, onCopy: () -> Unit, onShare: () -> Unit, onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scanned QR Code", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(bottomSheetTitleSpacing))
        Text(
            scannedCode,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(bottomSheetContentSpacing))

        Row(horizontalArrangement = Arrangement.spacedBy(bottomSheetButtonSpacing)) {
            Button(onClick = onCopy) {
                Icon(Icons.Default.Settings, contentDescription = "Copy")
                Spacer(modifier = Modifier.width(bottomSheetIconSpacing))
                Text("Copy")
            }
            Button(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Share")
                Spacer(modifier = Modifier.width(bottomSheetIconSpacing))
                Text("Share")
            }
        }
        Spacer(modifier = Modifier.height(bottomSheetCloseSpacing))
        OutlinedButton(onClick = onClose) {
            Text("Close")
        }
    }
}

@ComponentPreview
@Composable
private fun PreviewBottomSheetContentShort() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            BottomSheetContent(
                scannedCode = "https://google.com",
                onCopy = {},
                onShare = {},
                onClose = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PreviewBottomSheetContentLong() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            BottomSheetContent(
                scannedCode = "https://www.example.com/this/is/a/very/long/url/to/show/overflow/and/wrap/in/the/bottomsheet/dialog/for/the/purposes/of/previewing/in/compose/ui",
                onCopy = {},
                onShare = {},
                onClose = {},
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PreviewBottomSheetContentWithEmptyCode() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            BottomSheetContent(
                scannedCode = "",
                onCopy = {},
                onShare = {},
                onClose = {},
            )
        }
    }
}