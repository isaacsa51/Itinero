package com.serranoie.itinero.core.domain.model

data class Accommodation(
    val name: String,
    val phone: String,
    val checkIn: String,
    val checkOut: String,
    val location: String,
    val mapUri: String
)
