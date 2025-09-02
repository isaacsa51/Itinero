/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LanguageHelper.kt
 - Project: Itinero
 - Module: feature.auth.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.app.feature.auth.util

import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.UserPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.util.Language

/**
 * Helper class for handling language preferences during authentication flow
 */
class LanguageHelper(
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    /**
     * Sets up language preference after successful registration or login
     * This ensures the user's preference is synced with the server
     */
    suspend fun setupLanguageAfterAuth(): Result<String> {
        val currentLanguage = authPreferencesRepository.getLanguagePreference()
            ?: Language.getDeviceLanguage()

        return try {
            // Sync with server
            userPreferencesRepository.updateLanguagePreference(currentLanguage)
        } catch (e: Exception) {
            // If sync fails, at least ensure local preference is set
            authPreferencesRepository.saveLanguagePreference(currentLanguage)
            Result.Success(currentLanguage)
        }
    }

    /**
     * Gets the current language preference for display purposes
     */
    fun getCurrentLanguageDisplay(): String {
        val currentLanguage = authPreferencesRepository.getLanguagePreference()
            ?: Language.getDeviceLanguage()
        return Language.getLanguageDisplayName(currentLanguage)
    }

    /**
     * Gets the current language code
     */
    fun getCurrentLanguageCode(): String {
        return authPreferencesRepository.getLanguagePreference()
            ?: Language.getDeviceLanguage()
    }
}