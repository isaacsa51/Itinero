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

/**
 * Converts this `ItineraryItemEntity` to a domain model `ItineraryItem`.
 *
 * Maps all fields directly without transformation.
 *
 * @return The corresponding `ItineraryItem` domain object.
 */
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

/**
 * Converts this domain model itinerary item to a local database entity.
 *
 * Maps all fields directly without transformation.
 *
 * @return The corresponding [ItineraryItemEntity] representation.
 */
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

/**
 * Converts a remote itinerary item DTO to a domain model object.
 *
 * Parses the string ID to an integer (defaulting to 0 if parsing fails), combines date and time into a single string, and merges description and notes into the summary field. The group code is set to an empty string.
 *
 * @return The corresponding domain model itinerary item.
 */
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

/**
 * Converts a `CreateItineraryItem` domain model to a `CreateItineraryItemDto` for remote API requests.
 *
 * Splits the `dateTime` field into separate `date` and `time` components, defaulting `time` to "00:00" if not present.
 * Uses "general" as the default category and an empty string for notes if these fields are null.
 *
 * @return The corresponding `CreateItineraryItemDto` with mapped and defaulted fields.
 */
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

/**
 * Converts an [UpdateItineraryItem] domain model to a [UpdateItineraryItemDto] for remote data transfer.
 *
 * Splits the `dateTime` field into separate `date` and `time` components, defaulting `time` to "00:00" if not present.
 * Uses "general" as the default category and an empty string for notes if these fields are null.
 *
 * @return The corresponding [UpdateItineraryItemDto] with mapped and defaulted fields.
 */
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
