/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripMember.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TripMember(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val status: MemberStatus,
)

@Serializable
enum class MemberStatus(val value: String) {
    PENDING("PENDING"), ACCEPTED("ACCEPTED"), OWNER("OWNER");

    companion object {
        fun fromString(value: String): MemberStatus {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown MemberStatus: $value")
        }
    }
}
