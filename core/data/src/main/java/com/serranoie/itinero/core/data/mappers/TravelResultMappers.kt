package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.remote.dto.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.model.TripRequest
import java.util.UUID

fun TripDto.toDomain(): Travel {
    return Travel(
        id = null,
        groupCode = null,
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

fun CreateTripDto.toDomain(): Travel {
    return Travel(
        id = null,
        groupCode = null,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        accommodation = accommodation.toDomain().toString(),
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        isOwner = true // The user creating the trip is the owner
    )
}

fun AccommodationDto.toDomain(): Accommodation {
    return Accommodation(
        name = name,
        phone = phone,
        checkIn = checkIn,
        checkOut = checkOut,
        location = location,
        mapUri = mapUri
    )
}

fun Accommodation.toDto(): AccommodationDto {
    return AccommodationDto(
        name = name,
        phone = phone,
        checkIn = checkIn,
        checkOut = checkOut,
        location = location,
        mapUri = mapUri
    )
}