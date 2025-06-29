/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryItem.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ItineraryItem(
    val id: Int,
    val groupCode: String,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val isCompleted: Boolean = false
)
