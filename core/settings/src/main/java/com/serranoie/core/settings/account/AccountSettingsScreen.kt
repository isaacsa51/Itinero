/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: AccountSettingsScreen.kt
 - Project: Itinero
 - Module: Itinero.core.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 01 agosto 2025
 */

package com.serranoie.core.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.core.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel? = null,
    onLogout: (() -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect states from ViewModel
    val isLoggingOut by settingsViewModel?.isLoggingOut?.collectAsState()
        ?: remember { mutableStateOf(false) }
    val isDeletingAccount by settingsViewModel?.isDeletingAccount?.collectAsState()
        ?: remember { mutableStateOf(false) }
    val logoutError by settingsViewModel?.logoutError?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val deleteAccountError by settingsViewModel?.deleteAccountError?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val accountActionSuccess by settingsViewModel?.accountActionSuccess?.collectAsState()
        ?: remember { mutableStateOf(null) }

    // Dialog states
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var deleteAccountPassword by remember { mutableStateOf("") }

    // Handle success/error messages
    LaunchedEffect(logoutError) {
        logoutError?.let {
            snackbarHostState.showSnackbar(it)
            settingsViewModel?.clearErrors()
        }
    }

    LaunchedEffect(deleteAccountError) {
        deleteAccountError?.let {
            snackbarHostState.showSnackbar(it)
            settingsViewModel?.clearErrors()
        }
    }

    LaunchedEffect(accountActionSuccess) {
        accountActionSuccess?.let {
            snackbarHostState.showSnackbar(it)
            settingsViewModel?.clearErrors()
            onLogout?.invoke()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                Text(
                    "Account Settings", maxLines = 1, overflow = TextOverflow.Ellipsis
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
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileSection()
            AccountManagementSection()
            SecuritySection(
                onLogoutClick = { showLogoutDialog = true },
                onDeleteAccountClick = { showDeleteAccountDialog = true },
                isLoggingOut = isLoggingOut,
                isDeletingAccount = isDeletingAccount
            )
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout from your account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        settingsViewModel?.logout {
                            // Navigation will be handled by LaunchedEffect when accountActionSuccess is set
                        }
                    },
                    enabled = !isLoggingOut
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Logout")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    enabled = !isLoggingOut
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete account confirmation dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeletingAccount) {
                    showDeleteAccountDialog = false
                    deleteAccountPassword = ""
                }
            },
            title = { Text("Delete Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This action is irreversible. All your data will be permanently deleted.")
                    Text("Please enter your password to confirm:")
                    ITextField(
                        value = deleteAccountPassword,
                        onValueChange = { deleteAccountPassword = it },
                        label = "Password",
                        placeholder = "Enter your password"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel?.deleteAccount(deleteAccountPassword) {
                            showDeleteAccountDialog = false
                            deleteAccountPassword = ""
                            // Navigation will be handled by LaunchedEffect when accountActionSuccess is set
                        }
                    },
                    enabled = !isDeletingAccount && deleteAccountPassword.isNotBlank()
                ) {
                    if (isDeletingAccount) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete Account", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        deleteAccountPassword = ""
                    },
                    enabled = !isDeletingAccount
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileSection() {
    PaddedListGroup(
        title = "Profile".toUpperCase(locale = Locale.current)
    ) {
        ICard(
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
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Isaac Serrano",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Text(
                                text = "isaac.serrano@example.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Manage your account information below",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            })
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AccountManagementSection() {
    val phoneSectionExpanded = remember { mutableStateOf(false) }
    val passwordExpanded = remember { mutableStateOf(false) }

    PaddedListGroup(
        title = "Account Management".toUpperCase(locale = Locale.current)
    ) {
        CustomPaddedExpandableItem(
            isExpanded = phoneSectionExpanded.value,
            onToggleExpanded = { phoneSectionExpanded.value = !phoneSectionExpanded.value },
            position = PaddedListItemPosition.First,
            defaultContent = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(basePadding))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Change phone number",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Update your phone number",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (phoneSectionExpanded.value) 90f else 0f)
                )
            },
            expandedContent = {
                HorizontalDivider(modifier = Modifier.padding(vertical = smallPadding))

                Column(modifier = Modifier.padding(horizontal = basePadding), verticalArrangement = Arrangement.spacedBy(smallPadding)) {
                    Text(
                        text = "Current phone number",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    ITextField(
                        value = "12345678910",
                        onValueChange = {},
                        label = "Phone number",
                        placeholder = "Edit your phone number",
                    )

                    IButton(
                        onClick = { },
                        importance = ButtonImportance.Primary,
                        height = 36.dp,
                        content = {
                            Text(text = "Save", style = MaterialTheme.typography.labelMediumEmphasized)
                        })
                }
            })

        CustomPaddedExpandableItem(
            isExpanded = passwordExpanded.value,
            onToggleExpanded = { passwordExpanded.value = !passwordExpanded.value },
            position = PaddedListItemPosition.Middle,
            defaultContent = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Update current password",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Change your existing password",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (passwordExpanded.value) 90f else 0f)
                )
            },
            expandedContent = {
                HorizontalDivider(modifier = Modifier.padding(vertical = smallPadding))

                Column(
                    modifier = Modifier.padding(horizontal = basePadding),
                    verticalArrangement = Arrangement.spacedBy(smallPadding)
                ) {
                    Text(
                        text = "In order to be able to change your password, you must enter your current password",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    ITextField(
                        value = "12345678910",
                        onValueChange = {},
                        label = "Current password",
                        placeholder = "Enter your password",
                    )

                    ITextField(
                        value = "asdfadf",
                        onValueChange = {},
                        label = "New password",
                        placeholder = "Enter your new password",
                    )


                    ITextField(
                        value = "asdfadf repeat",
                        onValueChange = {},
                        label = "Confirm your new password",
                        placeholder = "Confirm your new password",
                    )

                    IButton(
                        onClick = { },
                        importance = ButtonImportance.Primary,
                        height = 36.dp,
                        content = {
                            Text(text = "Update", style = MaterialTheme.typography.labelMediumEmphasized)
                        })
                }
            })

        CustomPaddedListItem(
            onClick = { }, position = PaddedListItemPosition.Last
        ) {
            Icon(
                imageVector = Icons.Default.ManageAccounts,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Change personal information",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "View and manage your personal information.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SecuritySection(
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    isLoggingOut: Boolean = false,
    isDeletingAccount: Boolean = false
) {
    PaddedListGroup(
        title = "Account session".toUpperCase(locale = Locale.current)
    ) {
        CustomPaddedListItem(
            onClick = onLogoutClick,
            position = PaddedListItemPosition.First,
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isLoggingOut) "Logging out..." else "Logout from current account",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Sign out of your account",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isLoggingOut) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        CustomPaddedListItem(
            onClick = onDeleteAccountClick,
            position = PaddedListItemPosition.Last,
        ) {
            if (isDeletingAccount) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isDeletingAccount) "Deleting account..." else "Delete Account",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Permanently delete your account and data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isDeletingAccount) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@DevicePreview
@Composable
private fun AccountSettingsPreview() {
    PreviewWrapper {
        AccountSettingsScreen(
            navController = rememberNavController(),
            onLogout = {}
        )
    }
}