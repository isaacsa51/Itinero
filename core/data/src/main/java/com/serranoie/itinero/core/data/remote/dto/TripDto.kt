package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val totalMembers: Int,
    val travelDirection: String,
    val accommodation: AccommodationDto,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val groupCode: String,
    val ownerId: String
)

@Serializable
data class AccommodationDto(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String
)
