package com.serranoie.itinero.core.domain.model

data class Trip(
    val id: String?,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val totalMembers: Int,
    val travelDirection: String,
    val hasPendingActions: Boolean,
    val accommodation: Accommodation,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val groupCode: String,
    val ownerId: String,
    val isOwner: Boolean = false // need to be calculated based on the logged in user
)

data class Accommodation(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String
)
