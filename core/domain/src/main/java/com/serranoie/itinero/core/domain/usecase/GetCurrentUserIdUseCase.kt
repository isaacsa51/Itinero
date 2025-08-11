/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: GetCurrentUserIdUseCase.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 07 August 2025
 */

package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository

/**
 * Use case to get the current logged-in user's ID
 */
class GetCurrentUserIdUseCase(
    private val authPreferencesRepository: AuthPreferencesRepository
) {
    operator fun invoke(): String? {
        return authPreferencesRepository.getUserId()?.toString()
    }
}