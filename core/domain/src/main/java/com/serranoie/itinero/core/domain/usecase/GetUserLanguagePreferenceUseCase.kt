/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: GetUserLanguagePreferenceUseCase.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.repository.UserPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result

class GetUserLanguagePreferenceUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Result<String> {
        return userPreferencesRepository.getUserLanguagePreference()
    }
}