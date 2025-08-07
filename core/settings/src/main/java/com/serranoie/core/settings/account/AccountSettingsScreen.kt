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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedExpandableItem
import com.serranoie.app.designsystemlib.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IPasswordField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.InputType
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListGroup
import com.serranoie.app.designsystemlib.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.designsystemlib.ui.theme.component.card.ICard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.itinero.core.domain.model.UserProfile

// TODO: Update account info once the backend can be able to send OTPs emails
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountSettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel? = null,
    onLogout: (() -> Unit)? = null
) {
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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
    val userProfile by settingsViewModel?.userProfile?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val isLoadingUserProfile by settingsViewModel?.isLoadingUserProfile?.collectAsState()
        ?: remember { mutableStateOf(false) }
    val userProfileError by settingsViewModel?.userProfileError?.collectAsState()
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

    LaunchedEffect(settingsViewModel) {
        settingsViewModel?.fetchUserProfile()
    }

    LaunchedEffect(userProfileError) {
        userProfileError?.let {
            snackbarHostState.showSnackbar(it)
            settingsViewModel?.clearErrors()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
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
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            ProfileSection(userProfile, isLoadingUserProfile, userProfileError)
            AccountManagementSection(userProfile, settingsViewModel)
            SecuritySection(
                onLogoutClick = { showLogoutDialog = true },
                onDeleteAccountClick = { showDeleteAccountDialog = true },
                isLoggingOut = isLoggingOut,
                isDeletingAccount = isDeletingAccount
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout", style = MaterialTheme.typography.titleLargeEmphasized
                )
            },
            text = { Text("Are you sure you want to logout from your account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        settingsViewModel?.logout { }
                    }, enabled = !isLoggingOut
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp), strokeWidth = 2.dp
                        )
                    } else {
                        Text("Logout")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }, enabled = !isLoggingOut
                ) {
                    Text("Cancel")
                }
            })
    }

    if (showDeleteAccountDialog) {
        AlertDialog(onDismissRequest = {
            if (!isDeletingAccount) {
                showDeleteAccountDialog = false
                deleteAccountPassword = ""
            }
        }, title = {
            Text(
                "Delete Account", style = MaterialTheme.typography.titleLargeEmphasized
            )
        }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(smallPadding)) {

                Text(text = "This action is irreversible. All your data will be permanently deleted. If your account is an owner of an existing group, this will also delete the group and all its data.")

                Text(
                    text = "In case that you want to keep an existing group but delete your account, please change the group owner.",
                    style = MaterialTheme.typography.labelSmall
                )

                Text("Please enter your password to confirm:")

                IPasswordField(
                    value = deleteAccountPassword,
                    onValueChange = { deleteAccountPassword = it },
                    label = "Password",
                )
            }
        }, confirmButton = {
            TextButton(
                onClick = {
                    settingsViewModel?.deleteAccount(deleteAccountPassword) {
                        showDeleteAccountDialog = false
                        deleteAccountPassword = ""
                    }
                }, enabled = !isDeletingAccount && deleteAccountPassword.isNotBlank()
            ) {
                if (isDeletingAccount) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), strokeWidth = 2.dp
                    )
                } else {
                    Text("Delete Account", color = MaterialTheme.colorScheme.error)
                }
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showDeleteAccountDialog = false
                    deleteAccountPassword = ""
                }, enabled = !isDeletingAccount
            ) {
                Text("Cancel")
            }
        })
    }
}

@Composable
private fun ProfileSection(
    userProfile: UserProfile?, isLoadingUserProfile: Boolean, userProfileError: String?
) {
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
                    if (isLoadingUserProfile) {
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
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp), strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.height(smallPadding))
                                Text(
                                    text = "Loading profile...",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    } else if (userProfileError != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(smallPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile picture",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Failed to load profile",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = userProfileError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    } else if (userProfile != null) {
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
                                    text = userProfile.fullName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Text(
                                    text = userProfile.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                userProfile.phone?.let { phone ->
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Manage your account information below",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(smallPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile picture",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "No profile data",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Unable to load user information",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            })
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AccountManagementSection(
    userProfile: UserProfile? = null, settingsViewModel: SettingsViewModel? = null
) {
    val phoneSectionExpanded = remember { mutableStateOf(false) }
    val passwordExpanded = remember { mutableStateOf(false) }
    val personalInfoExpanded = remember { mutableStateOf(false) }

    // Local state for editing personal information
    var editingFirstName by remember { mutableStateOf("") }
    var editingLastName by remember { mutableStateOf("") }
    var editingEmail by remember { mutableStateOf("") }
    var editingPhone by remember { mutableStateOf("") }

    // Update local state when userProfile changes
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            // Use the separate name and lastName fields directly
            editingFirstName = userProfile.name
            editingLastName = userProfile.lastName
        } else {
            editingFirstName = ""
            editingLastName = ""
        }
        editingEmail = userProfile?.email ?: ""
        editingPhone = userProfile?.phone ?: ""
    }

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

                Column(
                    modifier = Modifier.padding(horizontal = basePadding),
                    verticalArrangement = Arrangement.spacedBy(smallPadding)
                ) {
                    Text(
                        text = "Update current phone number",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    ITextField(
                        value = editingPhone,
                        onValueChange = { editingPhone = it },
                        label = "Phone number",
                        placeholder = if (userProfile?.phone != null) "Edit your phone number" else "Enter your phone number",
                        inputType = InputType.PHONE
                    )

                    IButton(onClick = {
                        // TODO: Save phone number
                    }, importance = ButtonImportance.Primary, height = 36.dp, content = {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelMediumEmphasized
                        )
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
                    IPasswordField(
                        value = "",
                        onValueChange = {},
                        label = "Current password",
                    )

                    IPasswordField(
                        value = "",
                        onValueChange = {},
                        label = "New password",
                    )


                    IPasswordField(
                        value = "",
                        onValueChange = {},
                        label = "Confirm your new password",
                    )

                    IButton(
                        onClick = { },
                        importance = ButtonImportance.Primary,
                        height = 36.dp,
                        content = {
                            Text(
                                text = "Update",
                                style = MaterialTheme.typography.labelMediumEmphasized
                            )
                        })
                }
            })

        CustomPaddedExpandableItem(
            isExpanded = personalInfoExpanded.value,
            onToggleExpanded = { personalInfoExpanded.value = !personalInfoExpanded.value },
            position = PaddedListItemPosition.Last,
            defaultContent = {
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (personalInfoExpanded.value) 90f else 0f)
                )
            },
            expandedContent = {
                HorizontalDivider(modifier = Modifier.padding(vertical = smallPadding))

                Column(
                    modifier = Modifier.padding(horizontal = basePadding),
                    verticalArrangement = Arrangement.spacedBy(smallPadding)
                ) {
                    Text(
                        text = "Update your personal information",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    ITextField(
                        value = editingFirstName,
                        onValueChange = { editingFirstName = it },
                        label = "First name(s)",
                        placeholder = "Enter your first name(s)",
                        inputType = InputType.TEXT
                    )

                    ITextField(
                        value = editingLastName,
                        onValueChange = { editingLastName = it },
                        label = "Last name",
                        placeholder = "Enter your last name",
                        inputType = InputType.TEXT
                    )

                    ITextField(
                        value = editingEmail,
                        onValueChange = { editingEmail = it },
                        label = "Email address",
                        placeholder = "Enter your email address",
                        inputType = InputType.EMAIL
                    )

                    IButton(onClick = {
                        // Create updated UserProfile with separate name and lastName
                        userProfile?.let { currentProfile ->
                            val updatedProfile = currentProfile.copy(
                                name = editingFirstName.trim(),
                                lastName = editingLastName.trim(),
                                email = editingEmail.trim()
                            )

                            // Save personal information using SettingsViewModel
                            settingsViewModel?.updatePersonalInfo(updatedProfile)
                        }
                    }, importance = ButtonImportance.Primary, height = 36.dp, content = {
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.labelMediumEmphasized
                        )
                    })
                }
            })
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
                    modifier = Modifier.size(20.dp), strokeWidth = 2.dp
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
            navController = rememberNavController(), onLogout = {})
    }
}

@DevicePreview
@Composable
private fun ProfileSectionPreview() {
    PreviewWrapper {
        ProfileSection(
            userProfile = UserProfile(
                id = 123,
                name = "Jane",
                lastName = "Doe",
                email = "jane.doe@example.com",
                phone = "+1234567890"
            ), isLoadingUserProfile = false, userProfileError = null
        )
    }
}