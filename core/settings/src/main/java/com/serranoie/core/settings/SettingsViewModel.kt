/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SettingsViewModel.kt
 - Project: Itinero
 - Module: Itinero.core.settings.main
 -
 - This file belongs to the project: Itinero. 
 - Last edited: 21 julio 2025
 */

package com.serranoie.core.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context, private val authUseCase: AuthUseCase) : ViewModel() {
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_MATERIAL_YOU = "material_you"
        private const val KEY_THEME_MODE = "theme_mode"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Store listener as a strong reference to prevent garbage collection
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_THEME_MODE -> {
                _themeMode.value = getThemeMode()
                _isDarkTheme.value = getDarkTheme()
            }

            KEY_MATERIAL_YOU -> {
                _isMaterialYouEnabled.value = getMaterialYouEnabled()
            }
        }
    }

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isMaterialYouEnabled = MutableStateFlow(getMaterialYouEnabled())
    val isMaterialYouEnabled: StateFlow<Boolean> = _isMaterialYouEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Account management states
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount.asStateFlow()

    private val _logoutError = MutableStateFlow<String?>(null)
    val logoutError: StateFlow<String?> = _logoutError.asStateFlow()

    private val _deleteAccountError = MutableStateFlow<String?>(null)
    val deleteAccountError: StateFlow<String?> = _deleteAccountError.asStateFlow()

    private val _accountActionSuccess = MutableStateFlow<String?>(null)
    val accountActionSuccess: StateFlow<String?> = _accountActionSuccess.asStateFlow()

    init {
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onCleared() {
        super.onCleared()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    fun setThemeMode(mode: String) {
        sharedPrefs.edit {
            putString(KEY_THEME_MODE, mode)
        }

        updateStateFromPreferences()
    }

    fun setMaterialYouEnabled(enabled: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_MATERIAL_YOU, enabled)
        }

        updateStateFromPreferences()
    }

    private fun updateStateFromPreferences() {
        val themeModeValue = getThemeMode()
        val materialYouEnabledValue = getMaterialYouEnabled()
        val darkThemeValue = getDarkTheme()

        _themeMode.value = themeModeValue
        _isMaterialYouEnabled.value = materialYouEnabledValue
        _isDarkTheme.value = darkThemeValue
    }

    private fun getThemeMode(): String {
        val value = sharedPrefs.getString(KEY_THEME_MODE, "System Default") ?: "System Default"
        return value
    }

    private fun getMaterialYouEnabled(): Boolean {
        val value = sharedPrefs.getBoolean(KEY_MATERIAL_YOU, false)
        return value
    }

    private fun getDarkTheme(): Boolean {
        val themeMode = getThemeMode()
        val isDark = when (themeMode) {
            "Light" -> false
            "Dark" -> true
            "System Default" -> {
                val uiMode =
                    context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                uiMode == Configuration.UI_MODE_NIGHT_YES
            }

            else -> {
                val uiMode =
                    context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                uiMode == Configuration.UI_MODE_NIGHT_YES
            }
        }
        return isDark
    }

    /**
     * Gets the app version string from the package manager.
     */
    val appVersion: String
        get() = try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName, PackageManager.PackageInfoFlags.of(0)
                ).versionName ?: "Unknown"
            } else {
                @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0).versionName
                    ?: "Unknown"
            }
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Failed to get app version", e)
            "Unknown"
        }

    /**
     * Logs out the current user by calling the logout endpoint
     */
    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoggingOut.value = true
                _logoutError.value = null

                authUseCase.logout()

                // Clear user data from SharedPreferences
                clearUserData()
                _accountActionSuccess.value = "Successfully logged out"
                onSuccess()

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Logout failed", e)
                _logoutError.value = "Logout failed: ${e.message}"
            } finally {
                _isLoggingOut.value = false
            }
        }
    }

    /**
     * Deletes the current user account by calling the delete account endpoint
     */
    fun deleteAccount(password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isDeletingAccount.value = true
                _deleteAccountError.value = null

                if (password.isBlank()) {
                    _deleteAccountError.value = "Password is required for account deletion"
                    return@launch
                }

                // Call the use case with password for server-side validation
                authUseCase.deleteAccountUseCase(password)

                // Clear all user data (only reached if API call succeeds)
                clearUserData()
                _accountActionSuccess.value = "Account successfully deleted"
                onSuccess()

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Account deletion failed", e)

                // Handle specific error cases
                val errorMessage = when {
                    e.message?.contains("401") == true ||
                            e.message?.contains("unauthorized") == true ||
                            e.message?.contains("password") == true -> "Invalid password. Please try again."

                    e.message?.contains("403") == true -> "Account deletion not allowed."
                    e.message?.contains("network") == true -> "Network error. Please check your connection and try again."
                    else -> "Account deletion failed: ${e.message}"
                }

                _deleteAccountError.value = errorMessage
            } finally {
                _isDeletingAccount.value = false
            }
        }
    }

    /**
     * Clears all user data from SharedPreferences
     */
    private fun clearUserData() {
        sharedPrefs.edit {
            clear()
        }
    }

    /**
     * Clears error messages
     */
    fun clearErrors() {
        _logoutError.value = null
        _deleteAccountError.value = null
        _accountActionSuccess.value = null
    }
}
