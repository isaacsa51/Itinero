/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SettingsScreen.kt
 - Project: Itinero
 - Module: Itinero.core.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 21 julio 2025
 */

package com.serranoie.core.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeMode: String,
    isMaterialYouEnabled: Boolean,
    onThemeModeChanged: (String) -> Unit,
    onMaterialYouChanged: (Boolean) -> Unit
) {
    val themeOptions = listOf("Light", "Dark", "System Default")
    val selectedThemeIndex = themeOptions.indexOf(currentThemeMode)
    var showThemeDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                Text(
                    "Settings", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }, content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                })
            }, scrollBehavior = scrollBehavior
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text("Choose Theme") },
                    text = {
                        Column {
                            themeOptions.forEachIndexed { idx, option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .also { if (idx == selectedThemeIndex) it }) {
                                    RadioButton(
                                        selected = selectedThemeIndex == idx, onClick = {
                                            onThemeModeChanged(option)
                                            showThemeDialog = false
                                        })
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showThemeDialog = false }) {
                            Text("Cancel")
                        }
                    })
            }

            PaddedListGroup(
                title = "Look & Feel"
            ) {
                CustomPaddedListItem(
                    onClick = { showThemeDialog = true }, position = PaddedListItemPosition.First
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "App theme", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Change the overall theme of the app.",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Current: $currentThemeMode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Select theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.Last
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Material You", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Apply Material You colors based from your wallpaper to your app (Android 12+)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = isMaterialYouEnabled, onCheckedChange = {
                            onMaterialYouChanged(it)
                        })
                }
            }

            PaddedListGroup(
                title = "App Information"
            ) {
                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.First
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Information", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "See the information of the app and the developer.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.Middle
                ) {
                    Icon(
                        imageVector = Icons.Default.DensitySmall,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Website", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Visit our website for more extensive information about the app and the development of it.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.Middle
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Privacy Policy", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Read the privacy policy & terms of use of the app.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.Middle
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Encountered a bug?", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Send us a report or issues you encounter creating a Bug/Issue report on GitHub.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                CustomPaddedListItem(
                    onClick = { }, position = PaddedListItemPosition.Last
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Version", style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Version 0.5", style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@DevicePreview
@Composable
private fun SettingsScreenPreview() {
    PreviewWrapper {
        // For preview, we create the screen with the old signature for simplicity
        val navController = rememberNavController()
        var currentThemeMode by remember { mutableStateOf("System Default") }
        var isMaterialYouEnabled by remember { mutableStateOf(true) }
        SettingsScreen(
            navController = navController,
            currentThemeMode = currentThemeMode,
            isMaterialYouEnabled = isMaterialYouEnabled,
            onThemeModeChanged = { newThemeMode -> currentThemeMode = newThemeMode },
            onMaterialYouChanged = { newMaterialYouEnabled ->
                isMaterialYouEnabled = newMaterialYouEnabled
            }
        )
    }
}