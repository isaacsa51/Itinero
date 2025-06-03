package com.serranoie.itinero.core.domain.model

data class CreateTrip(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: Accommodation,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String
)
