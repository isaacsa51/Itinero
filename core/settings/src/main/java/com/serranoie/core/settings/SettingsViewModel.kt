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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val context: Context) : ViewModel() {
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_MATERIAL_YOU = "material_you"
        private const val KEY_THEME_MODE = "theme_mode"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Store listener as a strong reference to prevent garbage collection
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        val themeModeValue = getThemeMode()
        val materialYouEnabledValue = getMaterialYouEnabled()
        val darkThemeValue = getDarkTheme()

        _themeMode.value = themeModeValue
        _isMaterialYouEnabled.value = materialYouEnabledValue
        _isDarkTheme.value = darkThemeValue
    }

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isMaterialYouEnabled = MutableStateFlow(getMaterialYouEnabled())
    val isMaterialYouEnabled: StateFlow<Boolean> = _isMaterialYouEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

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
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                ).versionName ?: "Unknown"
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
            }
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Failed to get app version", e)
            "Unknown"
        }
}
