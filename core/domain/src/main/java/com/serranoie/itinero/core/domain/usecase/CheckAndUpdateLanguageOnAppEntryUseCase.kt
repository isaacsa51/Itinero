/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CheckAndUpdateLanguageOnAppEntryUseCase.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.UserPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.util.Language

class CheckAndUpdateLanguageOnAppEntryUseCase(
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Result<String> {
        val currentDeviceLanguage = Language.getDeviceLanguage()
        val storedLanguage = authPreferencesRepository.getLanguagePreference()

        return when {
            storedLanguage == null -> {
                setupLanguageFirstTime(currentDeviceLanguage)
            }

            storedLanguage != currentDeviceLanguage -> {
                updateLanguageToMatchDevice(currentDeviceLanguage, storedLanguage)
            }

            else -> {
                Result.Success(storedLanguage)
            }
        }
    }

    private suspend fun setupLanguageFirstTime(deviceLanguage: String): Result<String> {
        authPreferencesRepository.saveLanguagePreference(deviceLanguage)
        return if (authPreferencesRepository.isUserLoggedIn()) {
            syncLanguageWithServer(deviceLanguage)
        } else {
            Result.Success(deviceLanguage)
        }
    }

    private suspend fun updateLanguageToMatchDevice(
        newDeviceLanguage: String,
        oldStoredLanguage: String
    ): Result<String> {
        authPreferencesRepository.saveLanguagePreference(newDeviceLanguage)

        return if (authPreferencesRepository.isUserLoggedIn()) {
            syncLanguageWithServer(newDeviceLanguage)
        } else {
            Result.Success(newDeviceLanguage)
        }
    }

    private suspend fun syncLanguageWithServer(language: String): Result<String> {
        return try {
            when (val result = userPreferencesRepository.updateLanguagePreference(language)) {
                is Result.Success -> {
                    result
                }

                is Result.Error -> {
                    // Return success with local language since it's saved locally
                    Result.Success(language)
                }
            }
        } catch (e: Exception) {
            Result.Success(language)
        }
    }
}