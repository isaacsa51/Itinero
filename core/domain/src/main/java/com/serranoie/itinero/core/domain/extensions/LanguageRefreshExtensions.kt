/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LanguageRefreshExtensions.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.extensions

import com.serranoie.itinero.core.domain.manager.LanguageRefreshManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun CoroutineScope.observeLanguageChanges(
    languageRefreshManager: LanguageRefreshManager,
    onLanguageChanged: (String) -> Unit
) {
    languageRefreshManager.languageChanged
        .onEach { newLanguage ->
            onLanguageChanged(newLanguage)
        }
        .launchIn(this)
}

/**
 * Extension function to refresh language-dependent data
 */
suspend inline fun <T> refreshOnLanguageChange(
    languageRefreshManager: LanguageRefreshManager,
    crossinline refreshAction: suspend () -> T
): T {
    // Perform the refresh action
    val result = refreshAction()

    // The refresh has been triggered, components should update
    return result
}