/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserPreferencesRepository.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.result.Result

interface UserPreferencesRepository {
    suspend fun getUserLanguagePreference(): Result<String>
    suspend fun updateLanguagePreference(languageCode: String): Result<String>
    suspend fun getSupportedLanguages(): List<String>
}