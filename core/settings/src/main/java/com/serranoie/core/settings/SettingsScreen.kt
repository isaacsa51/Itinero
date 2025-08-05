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

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.LabelChip
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.card.ChatAvatar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    currentThemeMode: String,
    isMaterialYouEnabled: Boolean,
    onThemeModeChanged: (String) -> Unit,
    onMaterialYouChanged: (Boolean) -> Unit,
    userName: String = "Isaac Serrano",
    userStatus: String = "OWNER"
) {
    val viewModelThemeMode by settingsViewModel.themeMode.collectAsState()
    val viewModelMaterialYou by settingsViewModel.isMaterialYouEnabled.collectAsState()

    val themeOptions = listOf("Light", "Dark", "System Default")
    val selectedThemeIndex = themeOptions.indexOf(viewModelThemeMode)
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
                                ) {
                                    RadioButton(
                                        selected = selectedThemeIndex == idx, onClick = {
                                            android.util.Log.d(
                                                "SettingsScreen",
                                                "Theme changed from $viewModelThemeMode to $option"
                                            )
                                            settingsViewModel.setThemeMode(option)
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

            AccountInformationSection(
                navController = navController,
                userName = userName,
                userStatus = userStatus
            )

            LookAndFeelSection(
                viewModelThemeMode = viewModelThemeMode,
                viewModelMaterialYou = viewModelMaterialYou,
                settingsViewModel = settingsViewModel,
                onThemeModeChanged = onThemeModeChanged,
                onMaterialYouChanged = onMaterialYouChanged,
                onShowThemeDialog = { showThemeDialog = true }
            )

            AppInformationSection()
        }
    }
}

@Composable
private fun AccountInformationSection(
    navController: NavController,
    userName: String,
    userStatus: String = "MEMBER"
) {
    PaddedListGroup(
        title = "Account Information".toUpperCase(locale = Locale.current)
    ) {
        ProfileAccountSettings(
            navController = navController,
            userName = userName,
            userStatus = userStatus
        )
    }
}

@Composable
private fun LookAndFeelSection(
    viewModelThemeMode: String,
    viewModelMaterialYou: Boolean,
    settingsViewModel: SettingsViewModel? = null,
    onThemeModeChanged: ((String) -> Unit)? = null,
    onMaterialYouChanged: ((Boolean) -> Unit)? = null,
    onShowThemeDialog: () -> Unit
) {
    PaddedListGroup(
        title = "Look & Feel".toUpperCase(locale = Locale.current)
    ) {
        CustomPaddedListItem(
            onClick = { onShowThemeDialog() }, position = PaddedListItemPosition.First
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "App theme",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Change the overall theme of the app.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Current: $viewModelThemeMode",
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
            onClick = {
                settingsViewModel?.setMaterialYouEnabled(!viewModelMaterialYou)
                onMaterialYouChanged?.invoke(!viewModelMaterialYou)
            },
            position = PaddedListItemPosition.Last
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enable Material You",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Apply Material You colors based from your wallpaper into the app (Android 12+)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = viewModelMaterialYou,
                onCheckedChange = { newValue ->
                    settingsViewModel?.setMaterialYouEnabled(newValue)
                    onMaterialYouChanged?.invoke(newValue)
                }
            )
        }
    }
}

@Composable
private fun AppInformationSection() {
    val context = LocalContext.current

    PaddedListGroup(
        title = "App Information".toUpperCase(locale = Locale.current)
    ) {
        CustomPaddedListItem(
            onClick = {
                Utils.openWebLink(context, "https://www.github.com/isaacsa51/Itinero")
            }, position = PaddedListItemPosition.First
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Visit our website for more extensive information about the app and the development of it.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        CustomPaddedListItem(
            onClick = {
                Utils.openWebLink(context, "https://www.github.com/isaacsa51/Itinero")
            }, position = PaddedListItemPosition.Middle
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Read the privacy policy & terms of use of the app.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        CustomPaddedListItem(
            onClick = {
                Utils.openWebLink(
                    context, "https://www.github.com/isaacsa51/Itinero/issues"
                )
            }, position = PaddedListItemPosition.Middle
        ) {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Encountered a bug?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Send us a report about the issue you encounter via creating a Bug/Issue report on GitHub.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        CustomPaddedListItem(
            onClick = {
                //clipboardManager.setText(versionName)
            }, position = PaddedListItemPosition.Last
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Version",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                val versionName = try {
                    context.packageManager.getPackageInfo(
                        context.packageName, 0
                    ).versionName
                } catch (e: Exception) {
                    "Unknown"
                }

                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileAccountSettings(
    navController: NavController,
    userName: String = "Isaac Serrano",
    userStatus: String = "OWNER"
) {
    ICard(
        onClick = { navController.navigate(Route.Profile.route) },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(smallPadding),
                modifier = Modifier
                    .standardPadding()
                    .fillMaxWidth()
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChatAvatar(
                        authorName = userName, modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                    )

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = userName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                        )

                        Text(
                            text = "View and edit your account information",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    LabelChip(
                        text = userStatus,
                        textStyle = MaterialTheme.typography.labelSmallEmphasized,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        borderColor = MaterialTheme.colorScheme.outline,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@DevicePreview
@Composable
private fun SettingsScreenPreview() {
    PreviewWrapper {
        val navController = rememberNavController()
        var currentThemeMode by remember { mutableStateOf("System Default") }
        var isMaterialYou by remember { mutableStateOf(true) }

        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = {
                        Text(
                            "Settings", maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }, navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    }, scrollBehavior = scrollBehavior
                )
            }) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AccountInformationSection(
                    navController = navController,
                    userName = "Isaac Serrano",
                    userStatus = "MEMBER"
                )

                LookAndFeelSection(
                    viewModelThemeMode = currentThemeMode,
                    viewModelMaterialYou = isMaterialYou,
                    onThemeModeChanged = { currentThemeMode = it },
                    onMaterialYouChanged = { isMaterialYou = it },
                    onShowThemeDialog = { }
                )

                AppInformationSection()
            }
        }
    }
}