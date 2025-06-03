package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTripDto(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: AccommodationDto,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String
)
