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
    // State to control the visibility of the status bar.
    var visibility by remember { mutableStateOf(false) }

    // Animates the visibility of the status bar with vertical expansion and shrink effects.
    AnimatedVisibility(
        visible = visibility, // Controls whether the status bar is visible.
        enter = expandVertically(), // Animation for appearing: expands vertically.
        exit = shrinkVertically()   // Animation for disappearing: shrinks vertically.
    ) {
        // Display the status bar with the appropriate connectivity message and color.
        NetworkStatusBox(isConnected = isConnected)
    }

    // React to changes in connectivity state.
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            visibility = true // Show the status bar when disconnected.
        } else {
            delay(2000) // Delay hiding the status bar for 2 seconds after reconnecting.
            visibility = false // Hide the status bar after the delay.
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
    // Animate the background color change based on the connectivity state.
    val backgroundColor by animateColorAsState(
        if (isConnected) {
            MaterialTheme.colorScheme.tertiary
        } else MaterialTheme.colorScheme.error, label = ""
    )

    // Simple hardcoded strings - in production you'd use string resources
    val message = if (isConnected) {
        "Back online"
    } else {
        "No internet connection"
    }

    val iconResource = if (isConnected) {
        Icons.Default.Wifi // Icon for "connected" status.
    } else {
        Icons.Default.WifiOff // Icon for "disconnected" status.
    }

    // A box to display the connectivity message and icon.
    Box(
        modifier = Modifier
            .background(backgroundColor) // Background color changes based on connection state.
            .fillMaxWidth() // Make the box span the entire width.
            .wrapContentHeight()
            .padding(8.dp), // Padding around the content.
        contentAlignment = Alignment.Center // Center the content inside the box.
    ) {
        // Row to display an icon and a message horizontally.
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon representing the current connectivity state.
            Icon(
                imageVector = iconResource, // Resource ID for the icon.
                contentDescription = "", // Content description for accessibility.
                tint = MaterialTheme.colorScheme.surface // Icon color.
            )
            Spacer(modifier = Modifier.size(8.dp)) // Spacer between the icon and the text.
            // Text displaying the connectivity message.
            Text(
                text = message, color = MaterialTheme.colorScheme.surface, // Text color.
                fontSize = 15.sp // Font size.
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

