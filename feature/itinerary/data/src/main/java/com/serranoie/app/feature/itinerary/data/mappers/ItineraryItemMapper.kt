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

import com.serranoie.app.feature.itinerary.data.local.entity.ItineraryItemEntity
import com.serranoie.app.feature.itinerary.data.remote.dto.CreateItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.ItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.UpdateItineraryItemDto
import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem

fun ItineraryItemEntity.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id,
        groupCode = groupCode,
        name = name,
        dateTime = dateTime,
        location = location,
        summary = summary,
        isCompleted = isCompleted
    )
}

fun ItineraryItem.toEntity(): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        groupCode = groupCode,
        name = name,
        dateTime = dateTime,
        location = location,
        summary = summary,
        isCompleted = isCompleted
    )
}

fun ItineraryItemDto.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id.toIntOrNull() ?: 0,
        groupCode = "",
        name = title,
        dateTime = "$date $time",
        location = location,
        summary = "$description${if (notes.isNotEmpty()) "\n\nNotes: $notes" else ""}",
        isCompleted = isCompleted
    )
}

fun CreateItineraryItem.toDto(): CreateItineraryItemDto {
    return CreateItineraryItemDto(
        title = name,
        description = summary,
        date = dateTime.split(" ")[0],
        time = dateTime.split(" ").getOrNull(1) ?: "00:00",
        location = location,
        category = category ?: "general",
        notes = notes ?: ""
    )
}

fun UpdateItineraryItem.toDto(): UpdateItineraryItemDto {
    return UpdateItineraryItemDto(
        title = name,
        description = summary,
        date = dateTime.split(" ")[0],
        time = dateTime.split(" ").getOrNull(1) ?: "00:00",
        location = location,
        category = category ?: "general",
        notes = notes ?: ""
    )
}