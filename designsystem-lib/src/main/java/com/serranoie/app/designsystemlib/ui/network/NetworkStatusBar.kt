package com.serranoie.app.designsystemlib.ui.network

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@Composable
fun NetworkStatusBar(
    isConnected: Boolean
) {
    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        NetworkStatusBox(isConnected = isConnected)
    }

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            visibility = true
        } else {
            delay(2000)
            visibility = false
        }
    }
}


/**
 * A composable function to display the connectivity status message.
 *
 * @param isConnected A boolean indicating whether the network is connected.
 */
@Composable
private fun NetworkStatusBox(isConnected: Boolean) {
    val backgroundColor by animateColorAsState(
        if (isConnected) {
            MaterialTheme.colorScheme.tertiary
        } else MaterialTheme.colorScheme.error, label = ""
    )

    val message = if (isConnected) {
        "Back online"
    } else {
        "No internet connection"
    }

    val iconResource = if (isConnected) {
        Icons.Default.Wifi
    } else {
        Icons.Default.WifiOff
    }

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconResource,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = message, color = MaterialTheme.colorScheme.surface,
                fontSize = 15.sp
            )
        }
    }
}

@ExperimentalAnimationApi
@ComponentPreview
@Composable
fun NetworkStatusBarPreview() {
    PreviewWrapper {
        Column {
            NetworkStatusBox(isConnected = true)

            NetworkStatusBox(isConnected = false)
        }
    }
}

