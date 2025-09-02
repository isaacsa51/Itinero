package com.serranoie.itinero.core.data.remote.dto.trip

import kotlinx.serialization.Serializable

@Serializable
data class CreateTripDto(
    val ownerId: Int,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: CreateAccommodationDto,
    val groupName: String,
    val reservationCode: String? = null,
    val extraInfo: String? = null
)

@Serializable
data class CreateAccommodationDto(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val latitude: Double?,
    val longitude: Double?,
    val reservationCode: String? = null,
    val extraInfo: String? = null
)
