package com.serranoie.itinero.core.domain.model

data class RegisterRequest(
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val password: String
)
