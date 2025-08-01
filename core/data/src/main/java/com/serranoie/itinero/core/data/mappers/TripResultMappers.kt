package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.local.entity.EmbeddedAccommodation
import com.serranoie.itinero.core.data.local.entity.TripEntity
import com.serranoie.itinero.core.data.remote.dto.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.DebtorDto
import com.serranoie.itinero.core.data.remote.dto.PaidByDto
import com.serranoie.itinero.core.data.remote.dto.TodayItineraryDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.data.remote.dto.TripOverviewDto
import com.serranoie.itinero.core.data.remote.dto.UserDto
import com.serranoie.itinero.core.data.remote.dto.YesterdayExpenseDto
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Debtor
import com.serranoie.itinero.core.domain.model.PaidBy
import com.serranoie.itinero.core.domain.model.TodayItinerary
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripOverview
import com.serranoie.itinero.core.domain.model.User
import com.serranoie.itinero.core.domain.model.YesterdayExpense

fun TripDto.toDomain(): Trip {
    return groupName?.let {
        Trip(
            id = id,
            groupName = it,
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
    }!!
}

fun CreateTripDto.toDomain(): CreateTrip {
    return CreateTrip(
        groupName = groupName,
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
): Trip {
    return Trip(
        id = id,
        groupName = groupName,
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
        groupName = groupName,
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
        groupName = groupName,
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

fun TripOverviewDto.toDomain(): TripOverview {
    return TripOverview(
        date = date ?: "",
        todayItinerary = todayItinerary.mapNotNull { it.toDomainSafe() },
        yesterdayDate = yesterdayDate ?: "",
        yesterdayExpenses = yesterdayExpenses.mapNotNull { it.toDomainSafe() }
    )
}

fun TodayItineraryDto.toDomain(): TodayItinerary {
    return TodayItinerary(
        date = date ?: "",
        description = description ?: "",
        groupCode = groupCode ?: "",
        id = id ?: 0,
        isCompleted = isCompleted,
        location = location ?: "",
        name = name ?: "",
        time = time ?: ""
    )
}

fun TodayItineraryDto.toDomainSafe(): TodayItinerary? {
    // Only return if we have essential fields
    return if (name != null && time != null) {
        TodayItinerary(
            date = date ?: "",
            description = description ?: "",
            groupCode = groupCode ?: "",
            id = id ?: 0,
            isCompleted = isCompleted,
            location = location ?: "",
            name = name,
            time = time
        )
    } else null
}

fun YesterdayExpenseDto.toDomain(): YesterdayExpense {
    return YesterdayExpense(
        amount = amount,
        category = category ?: "",
        date = date ?: "",
        debtors = debtors.mapNotNull { it.toDomainSafe() },
        id = id ?: 0,
        isCompleted = isCompleted ?: false,
        name = name ?: "",
        notes = notes,
        paidBy = paidBy?.toDomain() ?: PaidBy(0, "", ""),
        paidByUserId = paidByUserId ?: 0,
        paymentMethod = paymentMethod ?: "",
        splitType = splitType ?: "",
        tripId = tripId ?: 0
    )
}

fun YesterdayExpenseDto.toDomainSafe(): YesterdayExpense? {
    // Only return if we have essential fields
    return if (name != null && paidBy != null) {
        YesterdayExpense(
            amount = amount,
            category = category ?: "",
            date = date ?: "",
            debtors = debtors.mapNotNull { it.toDomainSafe() },
            id = id ?: 0,
            isCompleted = isCompleted ?: false,
            name = name,
            notes = notes,
            paidBy = paidBy.toDomain(),
            paidByUserId = paidByUserId ?: 0,
            paymentMethod = paymentMethod ?: "",
            splitType = splitType ?: "",
            tripId = tripId ?: 0
        )
    } else null
}

fun DebtorDto.toDomain(): Debtor {
    return Debtor(
        amount = amount,
        hasPaid = hasPaid,
        id = id ?: 0,
        splitValue = splitValue,
        user = user?.toDomain() ?: User(0, "", ""),
        userId = userId ?: 0
    )
}

fun DebtorDto.toDomainSafe(): Debtor? {
    return if (user != null) {
        Debtor(
            amount = amount,
            hasPaid = hasPaid,
            id = id ?: 0,
            splitValue = splitValue,
            user = user.toDomain(),
            userId = userId ?: 0
        )
    } else null
}

fun UserDto.toDomain(): User {
    return User(
        id = id ?: 0,
        name = name ?: "",
        surname = surname ?: ""
    )
}

fun PaidByDto.toDomain(): PaidBy {
    return PaidBy(
        id = id ?: 0,
        name = name ?: "",
        surname = surname ?: ""
    )
}
