/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripMemberDto.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripMemberDto(
    val id: Int,
    val name: String,
    val email: String,
    val status: String
)