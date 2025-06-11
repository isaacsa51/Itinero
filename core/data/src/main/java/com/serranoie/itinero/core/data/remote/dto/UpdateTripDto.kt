/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UpdateTripDto.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 10 June 2025
 */

package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTripDto(
    val groupName: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: AccommodationDto,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String
)
