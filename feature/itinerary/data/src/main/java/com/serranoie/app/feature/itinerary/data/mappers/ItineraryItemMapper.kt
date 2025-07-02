/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryItemMapper.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.mappers

import com.serranoie.app.feature.itinerary.data.remote.dto.CreateItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.ItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.UpdateItineraryItemDto
import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.itinero.core.data.local.entity.ItineraryItemEntity

fun ItineraryItemEntity.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id,
        groupCode = groupCode,
        name = name,
        date = date,
        time = time,
        location = location,
        description = description,
        isCompleted = isCompleted
    )
}

fun ItineraryItem.toEntity(): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        groupCode = groupCode,
        name = name,
        date = date,
        time = time,
        location = location,
        description = description,
        isCompleted = isCompleted
    )
}

fun ItineraryItemDto.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id.toIntOrNull() ?: 0,
        groupCode = "",
        name = name,
        date = date,
        time = time,
        location = location,
        description = description,
        isCompleted = isCompleted
    )
}

fun CreateItineraryItem.toDto(): CreateItineraryItemDto {
    return CreateItineraryItemDto(
        name = name,
        description = description,
        date = date,
        time = time,
        location = location,
        isCompleted = isCompleted ?: false
    )
}

fun UpdateItineraryItem.toDto(): UpdateItineraryItemDto {
    return UpdateItineraryItemDto(
        name = name,
        description = description,
        date = date,
        time = time,
        location = location,
        isCompleted = false
    )
}
