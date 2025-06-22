/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryItemDto.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItineraryItemDto(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String,
    val notes: String,
    val isCompleted: Boolean = false
)