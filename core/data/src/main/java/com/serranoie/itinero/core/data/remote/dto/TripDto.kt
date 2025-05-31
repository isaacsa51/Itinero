package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
    val id: String = "",
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: String,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val groupCode: String = "",
    val ownerId: String = "",
    // val totalDays: Int
)