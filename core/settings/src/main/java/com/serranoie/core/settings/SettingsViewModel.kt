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
import android.content.res.Configuration
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_MATERIAL_YOU = "material_you"
        private const val KEY_THEME_MODE = "theme_mode"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Theme mode flow (Light, Dark, System Default)
    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Material You enabled flow
    private val _isMaterialYouEnabled = MutableStateFlow(getMaterialYouEnabled())
    val isMaterialYouEnabled: StateFlow<Boolean> = _isMaterialYouEnabled.asStateFlow()

    // Is dark theme (computed based on theme mode and system setting)
    private val _isDarkTheme = MutableStateFlow(getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        // Simplified preference change listener with debug logging
        sharedPrefs.registerOnSharedPreferenceChangeListener { _, key ->
            Log.d("SettingsViewModel", "Preference changed: $key")
            _themeMode.value = getThemeMode()
            _isMaterialYouEnabled.value = getMaterialYouEnabled()
            _isDarkTheme.value = getDarkTheme()
            Log.d(
                "SettingsViewModel",
                "themeMode = ${_themeMode.value}, materialYou = ${_isMaterialYouEnabled.value}, darkTheme = ${_isDarkTheme.value}"
            )
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Setting theme mode: $mode")

            // Update SharedPreferences
            sharedPrefs.edit {
                putString(KEY_THEME_MODE, mode)
            }

            // Force immediate update of StateFlows
            val newThemeMode = getThemeMode()
            val newDarkTheme = getDarkTheme()

            Log.d(
                "SettingsViewModel",
                "Calculated values - newThemeMode: $newThemeMode, newDarkTheme: $newDarkTheme"
            )

            // Manually update StateFlows to ensure they trigger
            if (_themeMode.value != newThemeMode) {
                _themeMode.value = newThemeMode
                Log.d("SettingsViewModel", "Updated themeMode StateFlow to: $newThemeMode")
            }

            if (_isDarkTheme.value != newDarkTheme) {
                _isDarkTheme.value = newDarkTheme
                Log.d("SettingsViewModel", "Updated isDarkTheme StateFlow to: $newDarkTheme")
            }

            Log.d(
                "SettingsViewModel",
                "After setThemeMode: themeMode = ${_themeMode.value}, darkTheme = ${_isDarkTheme.value}"
            )
        }
    }

    fun setMaterialYouEnabled(enabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Setting Material You: $enabled")

            // Update SharedPreferences
            sharedPrefs.edit {
                putBoolean(KEY_MATERIAL_YOU, enabled)
            }

            // Force immediate update of StateFlow
            if (_isMaterialYouEnabled.value != enabled) {
                _isMaterialYouEnabled.value = enabled
                Log.d("SettingsViewModel", "Updated isMaterialYouEnabled StateFlow to: $enabled")
            }

            Log.d(
                "SettingsViewModel",
                "After setMaterialYouEnabled: materialYou = ${_isMaterialYouEnabled.value}"
            )
        }
    }

    private fun getThemeMode(): String {
        return sharedPrefs.getString(KEY_THEME_MODE, "System Default") ?: "System Default"
    }

    private fun getMaterialYouEnabled(): Boolean {
        return sharedPrefs.getBoolean(KEY_MATERIAL_YOU, true)
    }

    private fun getDarkTheme(): Boolean {
        val themeMode = getThemeMode()
        return when (themeMode) {
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
    }
}
