/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: InitializeLanguagePreferenceUseCase.kt
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

class InitializeLanguagePreferenceUseCase(
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): String {
        println("🚀 LANGUAGE INIT: Starting language preference initialization...")

        val storedLanguage = authPreferencesRepository.getLanguagePreference()
        if (storedLanguage != null && Language.isLanguageSupported(storedLanguage)) {
            println("📱 LANGUAGE INIT: Found cached language '$storedLanguage', using it for notifications")
            return storedLanguage
        }

        val deviceLanguage = Language.getDeviceLanguage()
        println("🔍 LANGUAGE INIT: No cached preference, detecting device language: '$deviceLanguage'")

        authPreferencesRepository.saveLanguagePreference(deviceLanguage)
        println("💾 LANGUAGE INIT: Saved '$deviceLanguage' to local cache")

        if (authPreferencesRepository.isUserLoggedIn()) {
            println("🔐 LANGUAGE INIT: User is logged in, syncing language with server...")
            try {
                when (val result =
                    userPreferencesRepository.updateLanguagePreference(deviceLanguage)) {
                    is Result.Success -> {
                        println("✅ LANGUAGE INIT: Server sync successful, using server response: '${result.data}'")
                        println("🌍 LANGUAGE INIT: All API requests will now include Accept-Language: ${result.data}")
                        println(
                            "🔔 LANGUAGE INIT: Push notifications will be sent in: ${
                                Language.getLanguageDisplayName(
                                    result.data
                                )
                            }"
                        )
                        return result.data
                    }

                    is Result.Error -> {
                        println("⚠️ LANGUAGE INIT: Server sync failed (${result.exception.message}), continuing with device language")
                        println("🔄 LANGUAGE INIT: Will retry sync on next successful API call")
                        return deviceLanguage
                    }
                }
            } catch (e: Exception) {
                println("💥 LANGUAGE INIT: Network error during sync: ${e.message}")
                println("📱 LANGUAGE INIT: Continuing with offline device language: '$deviceLanguage'")
                return deviceLanguage
            }
        } else {
            println("🔓 LANGUAGE INIT: User not logged in, using device language locally")
            println("⏳ LANGUAGE INIT: Will sync with server after login")
        }

        println("🎯 LANGUAGE INIT: Initialization complete with language: '$deviceLanguage'")
        return deviceLanguage
    }
}