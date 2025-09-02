/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripMemberMappers.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 23 junio 2025
 */

package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.remote.dto.trip.MembershipStatusDto
import com.serranoie.itinero.core.data.remote.dto.trip.TripMemberDto
import com.serranoie.itinero.core.domain.model.MemberStatus
import com.serranoie.itinero.core.domain.model.MembershipStatus
import com.serranoie.itinero.core.domain.model.TripMember

/**
 * Extension function to convert TripMemberDto to TripMember domain model
 */
fun TripMemberDto.toDomain(): TripMember {
    return TripMember(
        id = this.id,
        name = this.name,
        surname = this.surname ?: "",
        email = this.email,
        status = MemberStatus.fromString(this.status)
    )
}

/**
 * Extension function to convert MembershipStatusDto to MembershipStatus domain model
 */
fun MembershipStatusDto.toDomain(): MembershipStatus {
    return MembershipStatus(
        status = this.status,
        isOwner = this.isOwner,
        isMember = this.isMember,
        isPending = this.isPending
    )
}

/**
 * Extension function to convert TripMember domain model to TripMemberDto
 */
fun TripMember.toDto(): TripMemberDto {
    return TripMemberDto(
        id = this.id,
        name = this.name,
        surname = this.surname.takeIf { it.isNotBlank() },
        email = this.email,
        status = this.status.value
    )
}

/**
 * Extension function to convert list of TripMemberDto to list of TripMember
 */
fun List<TripMemberDto>.toDomain(): List<TripMember> {
    return this.map { it.toDomain() }
}

/**
 * Extension function to convert list of TripMember to list of TripMemberDto
 */
fun List<TripMember>.toDto(): List<TripMemberDto> {
    return this.map { it.toDto() }
}