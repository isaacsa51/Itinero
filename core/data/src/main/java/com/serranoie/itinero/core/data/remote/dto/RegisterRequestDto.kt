package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val phone: String,
)
