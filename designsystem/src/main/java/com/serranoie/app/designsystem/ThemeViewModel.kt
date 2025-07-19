/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ThemeViewModel.kt
 - Project: Itinero
 - Module: Itinero.designsystem.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 julio 2025
 */

package com.serranoie.app.designsystem

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("settings")

class ThemeViewModel(private val context: Context) : ViewModel() {
    private val THEME_KEY = booleanPreferencesKey("dark_mode")
    private val MATERIAL_YOU_KEY = booleanPreferencesKey("material_you")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: false }

    val isMaterialYou: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[MATERIAL_YOU_KEY] ?: false }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[THEME_KEY] = isDark
            }
        }
    }

    fun toggleMaterialYou(useMaterialYou: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[MATERIAL_YOU_KEY] = useMaterialYou
            }
        }
    }
}