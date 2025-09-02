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

package com.serranoie.itinero.core.data.remote.dto.trip

import kotlinx.serialization.Serializable

@Serializable
data class TripMemberDto(
    val id: Int,
    val name: String,
    val surname: String? = null,
    val email: String,
    val status: String
)

@Serializable
data class MembershipStatusDto(
    val status: String,
    val isOwner: Boolean,
    val isMember: Boolean,
    val isPending: Boolean
)