package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.model.TripRequest
import java.util.UUID

fun TripRequest.toDomain(userId: String): Travel {
    return Travel(
        id = UUID.randomUUID().toString(), // Generate a temporary ID
        groupCode = generateGroupCode(), // Generate a temporary group code
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        accommodation = accommodation,
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        isOwner = true // The user creating the trip is the owner
    )
}

// Helper function to generate a random group code
private fun generateGroupCode(): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..8)
        .map { allowedChars.random() }
        .joinToString("")
}
