/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserProfile.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.domain.model

data class UserProfile(
    val id: Int?,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String? = null
) {
    val fullName: String
        get() = "$name $lastName".trim()
}