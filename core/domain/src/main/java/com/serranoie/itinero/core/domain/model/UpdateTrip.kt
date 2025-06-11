/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UpdateTrip.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 10 June 2025
 */

package com.serranoie.itinero.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTrip(
    val groupName: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: UpdateTripAccommodation,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
)

@Serializable
data class UpdateTripAccommodation(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String?
)