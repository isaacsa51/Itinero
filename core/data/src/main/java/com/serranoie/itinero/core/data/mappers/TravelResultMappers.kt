package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.local.entity.EmbeddedAccommodation
import com.serranoie.itinero.core.data.local.entity.TripEntity
import com.serranoie.itinero.core.data.remote.dto.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip

fun TripDto.toDomain(): Trip {
    return Trip(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        totalMembers = totalMembers,
        accommodation = accommodation.toDomain(),
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        groupCode = groupCode,
        ownerId = ownerId
    )
}

fun CreateTripDto.toDomain(): CreateTrip {
    return CreateTrip(
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        accommodation = accommodation.toDomain(),
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo
    )
}

fun CreateTrip.toTrip(
    id: String = "",
    groupCode: String = "",
    ownerId: String = "",
    totalMembers: Int = 1,
    travelDirection: String = ""
): Trip {
    return Trip(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        totalMembers = totalMembers,
        accommodation = accommodation,
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        groupCode = groupCode,
        ownerId = ownerId,
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
        mapUri = mapUri ?: ""
    )
}

fun TripEntity.toDomain(): Trip {
    return Trip(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        totalMembers = totalMembers,
        accommodation = accommodation.toDomain(),
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        groupCode = groupCode,
        ownerId = ownerId
    )
}

fun EmbeddedAccommodation.toDomain(): Accommodation {
    return Accommodation(
        name = name,
        phone = phone,
        checkIn = checkIn,
        checkOut = checkOut,
        location = location,
        mapUri = mapUri
    )
}

fun Trip.toEntity(): TripEntity {
    return TripEntity(
        id = id ?: java.util.UUID.randomUUID().toString(),
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        summary = summary,
        totalMembers = totalMembers,
        accommodation = accommodation.toEmbedded(),
        reservationCode = reservationCode,
        extraInfo = extraInfo,
        additionalInfo = additionalInfo,
        groupCode = groupCode,
        ownerId = ownerId
    )
}

fun Accommodation.toEmbedded(): EmbeddedAccommodation {
    return EmbeddedAccommodation(
        name = name,
        phone = phone,
        checkIn = checkIn,
        checkOut = checkOut,
        location = location,
        mapUri = mapUri
    )
}
