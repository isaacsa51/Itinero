/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: Trip.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: June 04 2025
 */

package com.serranoie.itinero.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String?,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val totalMembers: Int,
    val accommodation: Accommodation,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val groupCode: String,
    val ownerId: String,
)

@Serializable
data class Accommodation(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String?
)
