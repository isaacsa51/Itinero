package com.serranoie.itinero.core.data.remote.dto.trip

import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
    val id: String,
    val groupName: String? = null,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val totalMembers: Int,
    val accommodation: AccommodationDto,
    val reservationCode: String? = null,
    val extraInfo: String? = null,
    val additionalInfo: String? = null,
    val groupCode: String,
    val ownerId: String
)

@Serializable
data class AccommodationDto(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String? = null,
    val mapUri: String? = null,
    val latitude: Double?,
    val longitude: Double?,
    val reservationCode: String,
    val extraInfo: String
)
