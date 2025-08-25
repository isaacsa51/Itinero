package com.serranoie.itinero.core.domain.model

data class CreateTrip(
    val ownerId: Int,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: Accommodation,
    val groupName: String,
    val reservationCode: String,
    val extraInfo: String
)
