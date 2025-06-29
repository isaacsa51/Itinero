/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UpdateItineraryItemDto.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateItineraryItemDto(
    val name: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val isCompleted: Boolean,
)
