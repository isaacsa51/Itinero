/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryItemEntity.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itinerary_items")
data class ItineraryItemEntity(
    @PrimaryKey
    val id: Int,
    val groupCode: String,
    val name: String,
    val dateTime: String,
    val location: String,
    val summary: String,
    val isCompleted: Boolean = false
)