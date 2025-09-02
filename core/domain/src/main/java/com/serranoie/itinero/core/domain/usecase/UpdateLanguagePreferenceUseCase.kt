/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UpdateLanguagePreferenceUseCase.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.repository.UserPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result

class UpdateLanguagePreferenceUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(languageCode: String): Result<String> {
        return userPreferencesRepository.updateLanguagePreference(languageCode)
    }
}