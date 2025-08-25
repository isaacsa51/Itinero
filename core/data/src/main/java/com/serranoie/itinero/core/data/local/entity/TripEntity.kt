/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripEntity.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 09 June 2025
 */

package com.serranoie.itinero.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val destination: String,
    val groupName: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val totalMembers: Int,
    @Embedded(prefix = "accommodation_")
    val accommodation: EmbeddedAccommodation,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val groupCode: String,
    val ownerId: String,
)

data class EmbeddedAccommodation(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String?,
    val latitude: Double?,
    val longitude: Double?,
)
