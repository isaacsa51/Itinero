/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LanguageRefreshManager.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LanguageRefreshManager {

    private val _languageChanged = MutableSharedFlow<String>(replay = 0)
    val languageChanged: SharedFlow<String> = _languageChanged.asSharedFlow()

    suspend fun notifyLanguageChanged(newLanguageCode: String) {
        _languageChanged.emit(newLanguageCode)
    }

    companion object {
        const val LANGUAGE_CHANGED = "language_changed"
        const val HEADERS_REFRESHED = "headers_refreshed"
        const val UI_REFRESHED = "ui_refreshed"
    }
}