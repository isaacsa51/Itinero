package com.serranoie.itinero.core.domain.model

data class TripRequest(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: String,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String
)
