/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserPreferencesDto.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesResponse(
    val success: Boolean,
    val message: String,
    val language: String,
    val supportedLanguages: List<String>
)

@Serializable
data class UpdateLanguageRequest(
    val language: String
)

@Serializable
data class UpdateLanguageResponse(
    val success: Boolean,
    val message: String,
    val language: String
)